package org.openpaas.ieda.web.information.vms.service;

import static org.junit.Assert.assertTrue;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
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

@TransactionConfiguration(defaultRollback=true)
@Transactional
@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class VmsAsyncServiceTest {
	
	@Autowired DirectorConfigDAO dao;
	final private static int THREAD_SLEEP_TIME = 1 * 1000;
	private final static Logger LOGGER = LoggerFactory.getLogger(VmsAsyncServiceTest.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Job 상태 변화 로그를 웹소켓을 통해 전달
	 * @title               : doGetJobLog
	 * @return            : int
	***************************************************/
	public String doGetJobLog( VmsListDTO dto ){
		//1.1 git director Info
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		String content = "";
		String taskId = "";
		String status = "";
		HttpClient httpClient  = null;
		GetMethod getMethod = null;
		PutMethod putMehotd  = null;
		try{
			//1.1 get manifest content by deployment
			httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			getMethod  = new GetMethod(DirectorRestHelper.getManifestURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName()));
			getMethod = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)getMethod);
			int statusCode = HttpStatus.OK.value();
			
			if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
				throw new CommonException("notfound.vm.exception", 
						"Manifest 조회 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
			}
			
			String responseBody = settngResponseBody();
			LOGGER.debug(responseBody);
			JSONObject obj = new JSONObject(responseBody);
			content = obj.get("manifest").toString();
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//1.2 put job state
			putMehotd  = new PutMethod(DirectorRestHelper
					.getJobStateURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName(), dto.getJobName(), dto.getIndex(),  dto.getState()));
			putMehotd = (PutMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)putMehotd);
			putMehotd.setRequestHeader("Content-Type", "text/yaml");
			
			putMehotd.setRequestEntity(new StringRequestEntity(content, "text/yaml", "UTF-8"));
			
			statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
					  || statusCode == HttpStatus.MOVED_TEMPORARILY.value() ) {
				
				Header location = putMehotd.getResponseHeader("Location");
				taskId = String.valueOf(1);
				status = "done";
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Job 상태가 " +dto.getState() + "로 변경되었습니다.");
					LOGGER.debug("Location: " + location);
					LOGGER.debug("task Id: " + taskId);
					LOGGER.debug("HttpClient: " + httpClient);
				}
			}else {
				status = "error";
				if(LOGGER.isErrorEnabled()){
					LOGGER.error(" Exception이 발생했습니다.");
				}
			}
		}catch(Exception e){
			status = "error";
			if(LOGGER.isErrorEnabled()){
				LOGGER.error(" Exception이 발생했습니다.");
			}
			
		}finally{
			if ( getMethod != null ){
				getMethod.releaseConnection();
			}
			if ( putMehotd != null ){
				putMehotd.releaseConnection();
			}
		}
		return status;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 doGetJobLog 호출
	 * @title               : doGetJobLogAsync
	 * @return            : int
	***************************************************/
	public String doGetJobLogAsync(VmsListDTO dto) {
		return doGetJobLog(dto);
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
	 * @description   : Manifest 파일 정보 설정
	 * @title               : settngResponseBody
	 * @return            : String
	***************************************************/
	public String settngResponseBody(){
		String content = "{\"manifest\":\"---\\ncompilation:\\n  cloud_properties:\\n    ";
				content += "instance_type: m1.medium\\n  network: cf1\\n  reuse_compilation_vms: true\\n  ";
				content += "workers: 6\\ndirector_uuid: 8592e51e-c2c8-4f42-9904-65ea63072test\\njobs:\\n";
				content += "- default_networks:\\n  - name: cf1\\n  instances: 1\\n  name: consul_z1\\n  networks:\\n  ";
				content += "- name: cf1\\n    static_ips:\\n    - 192.168.XX.XXX\\n  persistent_disk: 1024\\n  properties:\\n    ";
				content += "consul:\\n      agent:\\n        mode: server\\n    metron_agent:\\n      ";
				content += "zone: z1\\n  resource_pool: small_z1\\n  templates:\\n  - name: consul_agent\\n    ";
				content += "release: cf\\n  - name: metron_agent\\n    release: cf\\n..\"}";
		return content;
	}
	
}
