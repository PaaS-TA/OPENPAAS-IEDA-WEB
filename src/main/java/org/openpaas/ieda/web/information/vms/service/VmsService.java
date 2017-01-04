package org.openpaas.ieda.web.information.vms.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.service.CommonUtils;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.information.vms.dto.VmsListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class VmsService {
	
	@Autowired private DirectorConfigService directorConfigService;
	final private static int THREAD_SLEEP_TIME = 1 * 1000;
	private final static Logger LOGGER = LoggerFactory.getLogger(DirectorConfigService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : VM 정보 목록 조회
	 * @title               : getVmList
	 * @return            : List<VmsListDTO>
	***************************************************/
	public List<VmsListDTO> getVmList(String deployment){
		//1.1 git director Info
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.dirctor.exception",
					"설치 관리자 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		List<VmsListDTO> vmInfoList = null;
		String taskId = "";
		BufferedReader bufferedReader = null;
		String info = null;
		String logType = "result";
		HttpClient client = null;
		GetMethod getMethod= null;
		GetMethod getTaskStaus = null;
		try{
			int offset = 0;
			//1.1 task Info by deployment
			client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			getMethod = new GetMethod(DirectorRestHelper.getVmListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deployment));
			getMethod = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)getMethod);
			int vmStatusCode = client.executeMethod(getMethod);
			
			if (HttpStatus.valueOf(vmStatusCode) != HttpStatus.OK) {
				throw new CommonException("notfound.vm.exception",  " VM 정보가 존재 하지 않습니다.", HttpStatus.NOT_FOUND);
			}
			
			String[] segments  = getMethod.getPath().split("/");
			taskId = segments[segments.length - 1];
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//1.2 Task 상태 정보 요청
			getTaskStaus = new GetMethod(DirectorRestHelper
					.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
			getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
					defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
			client.executeMethod(getTaskStaus);
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//1.3 Vms vitals Info by taskId
			GetMethod getTaskOutput = new GetMethod(DirectorRestHelper.getTaskOutputURI(
					defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, logType));
			getTaskOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
					defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutput);
			String range = "bytes=" + offset + "-";
			getTaskOutput.setRequestHeader("Range", range);
			int taskStatusCode = client.executeMethod(getTaskOutput);
			if (taskStatusCode == HttpStatus.OK.value() || taskStatusCode == HttpStatus.PARTIAL_CONTENT.value()) {

				vmInfoList = new ArrayList<VmsListDTO>();
				bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(getTaskOutput.getResponseBodyAsString().getBytes("UTF-8")),"UTF-8"));
				int recid = 0;
				while ((info = bufferedReader.readLine()) != null){ 
					JSONObject obj = new JSONObject(info);
					
					VmsListDTO dto = new VmsListDTO();
					dto.setRecid(recid ++);
					dto.setDeploymentName(deployment);
					dto.setJobName(obj.get("job_name").toString()+ "/" + obj.getInt("index") );
					dto.setJobState(obj.get("job_state").toString() );
					if( "null".equals(obj.get("az").toString()) &&  !(StringUtils.isEmpty(obj.get("az").toString())) ){
						dto.setAz(obj.get("az").toString() );
					}else{
						dto.setAz("n/a");
					}
					dto.setVmType(obj.get("vm_type").toString() );
					
					//ips
					StringBuffer ips = new StringBuffer();
					for(int i=0; i < obj.getJSONArray("ips").length(); i ++){
						ips.append(obj.getJSONArray("ips").get(i).toString() + "<br/>");
					}
					dto.setIps(ips.toString());
					
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
					StringBuffer load = new StringBuffer();
					for(int i=0; i < vitalsObj.getJSONArray("load").length(); i ++){
						load.append(vitalsObj.getJSONArray("load").get(i).toString() + "<br/>");
					}
					dto.setLoad(load.toString());
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
		}catch(RuntimeException e){
			throw new CommonException("runtime.vm.exception",
					"VM 정보를 가져올  수 없습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(Exception e){
			throw new CommonException("notfound.vm.exception",
					"VM 정보를 가져올  수 없습니다. ", HttpStatus.NOT_FOUND);
		}finally{
			if( getTaskStaus  != null){
				getTaskStaus.releaseConnection();
			}
			if( getMethod  != null){
				getMethod.releaseConnection();
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
}
