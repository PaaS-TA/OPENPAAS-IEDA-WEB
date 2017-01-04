package org.openpaas.ieda.web.information.vms.service;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONException;
import org.json.JSONObject;
import org.openpaas.ieda.api.deployment.DeploymentDTO;
import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.service.CommonUtils;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.information.vms.dto.VmsListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@TransactionConfiguration(defaultRollback=true)
@Transactional
@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class VmsServiceTest {

	@Autowired DirectorConfigDAO dao;
	
	final static private int THREAD_SLEEP_TIME = 1 * 1000;
	final static private String SEPARATOR      = System.getProperty("file.separator");
	final static private String FILE_PATH = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets/consul_z1_0_test_job.tgz";
	final private static Logger LOGGER = LoggerFactory.getLogger(VmsServiceTest.class);
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포명 조회
	 * @title               : listDeployment
	 * @return            : List<DeploymentInfoDTO>
	***************************************************/
	public List<DeploymentInfoDTO> listDeployment(){
		//1.1 select default director info
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		List<DeploymentInfoDTO> deploymentInfoList = null;
		try {
			//1.1 request client
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getDeploymentListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			
			String responseBody = setDeploymentResponseBody(); 
			ObjectMapper mapper = new ObjectMapper();
			
			DeploymentDTO[] deploymentList = mapper.readValue(responseBody, DeploymentDTO[].class);
			
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("httpClient:" + httpClient);
			}
			
			int idx = 0;
			for ( DeploymentDTO deployment : deploymentList ) {
				if ( deploymentInfoList == null ) 
					deploymentInfoList = new ArrayList<DeploymentInfoDTO>();
				
				DeploymentInfoDTO deploymentInfo = new DeploymentInfoDTO();
				
				deploymentInfo.setRecid(idx++);
				deploymentInfo.setName(deployment.getName());
				String releaseInfo = "";
				for ( HashMap<String, String> release : deployment.getReleases()) {
					releaseInfo = releaseInfo + release.get("name") + " (" + release.get("version") + ")<br>";
				}
				deploymentInfo.setReleaseInfo(releaseInfo);
				
				String stemcellInfo = "";
				for ( HashMap<String, String> stemcell : deployment.getStemcells()) {
					stemcellInfo = stemcellInfo + stemcell.get("name") + " (" + stemcell.get("version") + ")<br>";
				}
				deploymentInfo.setStemcellInfo(stemcellInfo);
				deploymentInfoList.add(deploymentInfo);
			}
		} catch (IOException e) {
			throw new CommonException("notfound.deployment.exception", " 배포 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		return deploymentInfoList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  VM 정보 목록 조회
	 * @title               : getVmList
	 * @return            : List<VmsListDTO>
	***************************************************/
	public List<VmsListDTO> getVmList(String deploymentName){
		//1.1 select default director info
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		List<VmsListDTO> vmInfoList = null;
		String taskId = "";
		BufferedReader bufferedReader = null;
		InputStream input  = null;
		String info = null;
		String logType = "result";
		HttpClient client = null;
		GetMethod getMethod= null;
		GetMethod getTaskStaus = null;
		try{
			int offset = 0;
			//1.1 task Info by deployment
			client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			getMethod = new GetMethod(DirectorRestHelper.getVmListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
			getMethod = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)getMethod);
			
			int statusCode = HttpStatus.OK.value();
			
			if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
				throw new CommonException("notfound.vm.exception",  "VM 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
			}
			
			taskId = String.valueOf(1);
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//1.2 Task 상태 정보 요청
			getTaskStaus = new GetMethod(DirectorRestHelper
					.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
			getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
					defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//1.3 Vms vitals Info by taskId
			GetMethod getTaskOutput = new GetMethod(DirectorRestHelper.getTaskOutputURI(
					defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, logType));
			getTaskOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
					defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutput);
			String range = "bytes=" + offset + "-";
			getTaskOutput.setRequestHeader("Range", range);

			statusCode = HttpStatus.PARTIAL_CONTENT.value();
			
			if (statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()) {
				//Response body byte
				String responseBody = settingVmListInfo();
		        
				vmInfoList = new ArrayList<VmsListDTO>();
				
				input= new ByteArrayInputStream(responseBody.getBytes("UTF-8"));
				bufferedReader = new BufferedReader(new InputStreamReader(input));
				int recid = 0;
				while ((info = bufferedReader.readLine()) != null){ 
					JSONObject obj = new JSONObject(info);
					
					VmsListDTO dto = new VmsListDTO();
					dto.setRecid(recid ++);
					dto.setDeploymentName(deploymentName);
					dto.setJobName(obj.get("job_name").toString()+ "/" + obj.getInt("index") );
					dto.setJobState(obj.get("job_state").toString() );
					if( obj.get("az").toString().equals("null") &&  !(StringUtils.isEmpty(obj.get("az").toString())) ){
						dto.setAz(obj.get("az").toString() );
					}else{
						dto.setAz("n/a");
					}
					dto.setVmType(obj.get("vm_type").toString() );
					
					//ips
					String ips = "";
					for(int i=0; i < obj.getJSONArray("ips").length(); i ++){
						ips += obj.getJSONArray("ips").get(i).toString() + "<br/>";
					}
					dto.setIps(ips);
					
					//1. vitals
					JSONObject vitalsObj = obj.getJSONObject("vitals");
					//1.1 disk
					dto.setDiskSystem(vitalsObj.getJSONObject("disk").getJSONObject("system").get("percent").toString() + "%");
					dto.setDiskEphemeral(vitalsObj.getJSONObject("disk").getJSONObject("ephemeral").get("percent").toString() + "%" );
					Iterator<?> iterator = vitalsObj.getJSONObject("disk").keys();
					while( iterator.hasNext() ){
						if( "persistent".equals(iterator.next().toString()) ){
							dto.setDiskPersistent(vitalsObj.getJSONObject("disk").getJSONObject("persistent").get("percent").toString() + "%" );
						}else{
							dto.setDiskPersistent("n/a");
						}
					}
					//1.2 load
					String load = "";
					for(int i=0; i < vitalsObj.getJSONArray("load").length(); i ++){
						load += vitalsObj.getJSONArray("load").get(i).toString() + "<br/>";
					}
					dto.setLoad(load);
					//1.3 swap
					String swapMb = vitalsObj.getJSONObject("swap").get("kb").toString();
					dto.setSwapUsage( vitalsObj.getJSONObject("swap").get("percent").toString() + "% (" + swapMb +"B)" );
					//1.4 memory7
					String memeoryMb = CommonUtils.formatSizeUnit(Long.parseLong(vitalsObj.getJSONObject("mem").get("kb").toString() ));
					dto.setMemoryUsage(vitalsObj.getJSONObject("mem").get("percent").toString() + "% (" +memeoryMb + ")");
					
					//1.5 cpu
					dto.setCpuWait(vitalsObj.getJSONObject("cpu").get("wait").toString()  +"%");
					dto.setCpuSys(vitalsObj.getJSONObject("cpu").get("sys").toString() +"%" );
					dto.setCpuUser(vitalsObj.getJSONObject("cpu").get("user").toString() +"%");
					
					vmInfoList.add(dto);
				}
			}	
		}catch(InterruptedException e){
			throw new CommonException("InterruptedException.vm.exception", "VM 정보를 가져올  수 없습니다. ", HttpStatus.BAD_REQUEST);
		}catch (UnsupportedEncodingException e) {
			throw new CommonException("UnsupportedEncodingException.vm.exception", "VM 정보를 가져올  수 없습니다. ", HttpStatus.BAD_REQUEST);
		}catch (NumberFormatException e) {
			throw new CommonException("NumberFormatException.vm.exception", "VM 정보를 가져올  수 없습니다. ", HttpStatus.BAD_REQUEST);
		}catch (JSONException e) {
			throw new CommonException("JSONException.vm.exception", "VM 정보를 가져올  수 없습니다. ", HttpStatus.BAD_REQUEST);
		}catch (IOException e) {
			throw new CommonException("IOException.vm.exception", "VM 정보를 가져올  수 없습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		finally{
			if( getTaskStaus  != null){
				getTaskStaus.releaseConnection();
			}
			if( getMethod  != null){
				getMethod.releaseConnection();
			}
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );  
					}
				}
			}
			if(bufferedReader != null){
				try {
					bufferedReader.close();
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );  
					}
				}
			}
		}
		return vmInfoList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로그 다운로드
	 * @title               : doDownloadLogTest
	 * @return            : int
	***************************************************/
	public int doDownloadLogTest(String jobName, String index, String deploymentName, String type, HttpServletRequest request, HttpServletResponse response){
		//1.1 select default director info
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		int statusCode = 0;
		String taskId = "";
		String logFile = "";
		int status = 0;
		try{
			//Create Agent/Job Log
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod getLogMethod = new GetMethod(DirectorRestHelper.createLogURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(),  deploymentName, jobName, index, type));
			getLogMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getLogMethod);
			statusCode = HttpStatus.OK.value();
			
			//get taskId
			taskId = String.valueOf(1);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("httpClient:" + httpClient);
				LOGGER.debug("taskId: " + taskId);
			}
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//Task status Info
			GetMethod getTaskStaus = new GetMethod(DirectorRestHelper.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
			getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
			statusCode = HttpStatus.OK.value();
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			if ( statusCode == HttpStatus.OK.value() ){
				
				String responseBody = seLogResponseBody();
				JSONObject obj = new JSONObject(responseBody);
				String result = obj.get("result").toString();
				// download log by result
				GetMethod getResultOutput = new GetMethod(DirectorRestHelper.getResultOutputURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), result));
				getResultOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getResultOutput);
				String range = "bytes=" + 0 + "-";
				getResultOutput.setRequestHeader("Range", range);
				
				statusCode = HttpStatus.PARTIAL_CONTENT.value();
				
				if ( statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()){
						
					byte[] content = Files.readAllBytes(new File(FILE_PATH).toPath());
					
					Date now = new Date();
					SimpleDateFormat dataformat = new SimpleDateFormat("yyyymmdd_HHmmss", Locale.KOREA);
					logFile = jobName+"_"+index+"_"+dataformat.format(now)+"_"+type;
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename=" + logFile+".tgz");
					IOUtils.write(content, response.getOutputStream());

					status = 200;
				}else{
					status =404;
					throw new CommonException("not found.logDownload.exception",
							"리소스를 다운로드 할 수 없습니다. ", HttpStatus.NOT_FOUND);
				}
			}
		}catch(Exception e){
			status = 404;
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );  
			}
		}
		
		return status;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : saveDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO saveDefaultDirector(){
		//director 설정
		DirectorConfigVO defaultDirector = dao.selectDirectorConfigByDefaultYn("Y");
		if( defaultDirector != null ){
			dao.deleteDirecotr(defaultDirector.getIedaDirectorConfigSeq());
		}
		DirectorConfigVO director = settingDefaultDirector();
		assertTrue(dao.insertDirector( director) == 1);
		
		return director;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 정보 설정
	 * @title               : settingDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO settingDefaultDirector(){
		DirectorConfigVO vo = new DirectorConfigVO();
		vo.setIedaDirectorConfigSeq(1);
		vo.setDefaultYn("Y");
		vo.setDirectorCpi("openstack-cpi");
		vo.setDirectorName("bosh");
		vo.setDirectorPort(25555);
		vo.setDirectorUrl("10.10.10.10");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setDirectorVersion("1.3252.0");
		vo.setCreateUserId("test");
		vo.setUpdateUserId("test");
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포명 client 요청 결과 설정
	 * @title               : setDeploymentResponseBody
	 * @return            : String
	***************************************************/
	public String setDeploymentResponseBody(){
		String info = "[{\"name\":\"cf-openstack\",";
				info += "\"releases\":[{\"name\":\"cf\",\"version\":\"236\"}],";
				info += "\"stemcells\":[{\"name\":\"bosh-openstack-kvm-ubuntu-trusty-go_agent\",\"version\":\"3262\"}],";
				info += "\"cloud_config\":\"none\"},";
				info += "{\"name\":\"cf-openstack-diego\",";
				info += "\"releases\":[{\"name\":\"diego\",\"version\":\"0.1481.0\"},";
				info += "{\"name\":\"cflinuxfs2-rootfs\",\"version\":\"1.21.0\"},";
				info += "{\"name\":\"etcd\",\"version\":\"63\"},";
				info += "{\"name\":\"garden-linux\",\"version\":\"0.339.0\"},";
				info += "{\"name\":\"cf\",\"version\":\"236\"}],";
				info += "\"stemcells\":[{\"name\":\"bosh-openstack-kvm-ubuntu-trusty-go_agent\",\"version\":\"3262\"}],";
				info += "\"cloud_config\":\"none\"}]"; 
		return info;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : VM 정보 client 요청 결과 설정
	 * @title               : settingVmListInfo
	 * @return            : String
	***************************************************/
	public String settingVmListInfo(){
		
		String info = "{\"vm_cid\":\"55a069b8-e549-492f-9da5-bdf3801c79b9\",";
		info += "\"disk_cid\":null,\"ips\":[\"192.168.XX.XXX\"],";
		info += "\"dns\":[],\"agent_id\":\"a923fd20-b981-4fd5-9cf6-55c0db630771\",";
		info += "\"job_name\":\"loggregator_trafficcontroller_z1\",\"index\":0,";
		info += "\"job_state\":\"running\",\"state\":\"started\",\"resource_pool\":\"small_z1\",";
		info += "\"vm_type\":\"small_z1\",\"vitals\":{\"cpu\":{\"sys\":\"0.0\",\"user\":\"0.1\",\"wait\":\"0.7\"},";
		info += "\"disk\":{\"ephemeral\":{\"inode_percent\":\"0\",\"percent\":\"2\"},";
		info += "\"system\":{\"inode_percent\":\"33\",\"percent\":\"48\"}},";
		info += "\"load\":[\"0.13\",\"0.04\",\"0.05\"],";
		info += "\"mem\":{\"kb\":\"78944\",\"percent\":\"4\"},";
		info +="\"swap\":{\"kb\":\"0\",\"percent\":\"0\"}},";
		info +="\"processes\":[{\"name\":\"loggregator_trafficcontroller\",\"state\":\"running\",\"uptime\":{\"secs\":85903},";
		info +="\"mem\":{\"kb\":13168,\"percent\":0.6},\"cpu\":{\"total\":0}},";
		info +="{\"name\":\"metron_agent\",\"state\":\"running\",\"uptime\":{\"secs\":85901},";
		info +="\"mem\":{\"kb\":13932,\"percent\":0.6},\"cpu\":{\"total\":0}},{\"name\":\"route_registrar\",\"state\":\"running\",";
		info +="\"uptime\":{\"secs\":85901},\"mem\":{\"kb\":9068,\"percent\":0.4},\"cpu\":{\"total\":0}}],";
		info +="\"resurrection_paused\":false,\"az\":null,\"id\":\"95c858bb-5cee-48a3-b0b4-623d45512847\",\"bootstrap\":true,\"ignore\":false}";
	
		return info;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 다운로드 로그 정보
	 * @title               : seLogResponseBody
	 * @return            : String
	***************************************************/
	public String seLogResponseBody(){
		String info = "{\"id\":\"1\",";
		info += "\"state\":\"processing\",";
		info += "\"description\":\"fetch logs\",";
		info += "\"timestamp\":\"1474333197\",";
		info += "\"started_at\":\"1474333197\",";
		info += "\"result\":\"null\",";
		info += "\"user\":\"admin\",";
		info += "\"deployment\":\"cf-openstack\"}";
		
		return info;
	}
	
}
