package org.openpaas.ieda.web.deploy.servicePack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.deploy.servicepack.dao.ServicePackDAO;
import org.openpaas.ieda.web.deploy.servicepack.dao.ServicePackVO;
import org.openpaas.ieda.web.deploy.servicepack.dto.ServicePackParamDTO;
import org.openpaas.ieda.web.information.manifest.dao.ManifestDAO;
import org.openpaas.ieda.web.information.manifest.dao.ManifestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
@Service
public class ServicePackServiceTest {
	
	@Autowired ServicePackDAO dao;
	@Autowired ManifestDAO manifestDao;
	private final static Logger LOGGER = LoggerFactory.getLogger(ServicePackServiceTest.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 저장
	 * @title               : saveServicePackInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void saveServicePackInfo() {
		ServicePackVO vo = setServicePackInfo();
		dao.insertServicePackInfo(vo);
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 설정
	 * @title               : setServicePackInfo
	 * @return            : ServicePackVO
	***************************************************/
	private ServicePackVO setServicePackInfo() {
		ServicePackVO vo = new ServicePackVO();
		vo.setId(1);
		vo.setCreateUserId("tester");
		vo.setUpdateUserId("tester");
		vo.setDeploymentFile("servicepack-test.yml");
		vo.setDeploymentName("openstack-servicepack");
		vo.setIaas("OPENSTACK");
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 설치
	 * @title               : deploy
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void deploy(ServicePackParamDTO dto){
		ServicePackVO vo = null;
		ManifestVO manifestVo = null;
		String deploymentFileName = null;
		saveServicePackInfo();
		vo = dao.selectServicePackDetailInfo(dto.getId());
		if ( vo != null ) {
			manifestVo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
			deploymentFileName = vo.getDeploymentFile();
		}
		if ( StringUtils.isEmpty(deploymentFileName) ) {
			throw new CommonException("notfound.servicepack.exception",
					"배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		if ( vo != null ) {
			vo.setDeployStatus("deploying");
			vo.setUpdateUserId("tester");
			dao.updateServicePackInfo(vo);
		}
		
		String status = "";
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		String content = "";
		String temp = "";
		String taskId = "";
		
		BufferedReader br = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			PostMethod postMethod = new PostMethod(DirectorRestHelper.getDeployURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "text/yaml");
			
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
			
			fis = new FileInputStream(deployFile);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			
			while ( (temp=br.readLine()) != null) {
				content += temp + "\n";
			}
			
			postMethod.setRequestEntity(new StringRequestEntity(content, "text/yaml", "UTF-8"));
			
		
			int statusCode =  HttpStatus.MOVED_PERMANENTLY.value();
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = postMethod.getResponseHeader("Location");
				taskId = "1";
				status = "done";
			} else {
				status = "error";
			}
		} catch(IOException e){
			status = "error";
		}catch ( Exception e) {
			status = "error";
		} finally {
			try {
				if ( fis != null ) fis.close();
				if ( isr != null ) isr.close();
				if ( br != null ) br.close();
				if( "done".equals(status)){
					if(LOGGER.isDebugEnabled()) LOGGER.debug("서비스팩 설치를 성공하였습니다.");
				}else{
					if(LOGGER.isDebugEnabled()) LOGGER.debug("배포 중 오류가 발생하였습니다.");
				}
			} catch ( Exception e ) {
				if(LOGGER.isDebugEnabled()){ LOGGER.debug("배포 중 오류가 발생하였습니다."); }
			}
		}
		if ( vo != null ) {
			vo.setDeployStatus(status);
			vo.setUpdateUserId("tester");
			dao.updateServicePackInfo(vo);
			manifestVo.setDeployStatus("Y");
			manifestDao.updateManifestInfo(manifestVo);
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 설치 비동기식 요청
	 * @title               : deployAsync
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Async
	public void deployAsync(ServicePackParamDTO dto) {
		deploy(dto);
	}

	/***************************************************
	 * @project 			: OpenPaas 플랫폼 설치 자동화
	 * @description 		: 서비스팩 플랫폼 삭제 요청
	 * @title 				: deleteDeploy
	 * @return 			: void
	***************************************************/
	@Rollback(true)
	public void deleteDeploy(ServicePackParamDTO dto) {
		
		ServicePackVO vo = null;
		ManifestVO manifestVo = null;
		String deploymentName = null;
		saveServicePackInfo();
		vo = dao.selectServicePackDetailInfo(dto.getId());
		if ( vo != null) {
		manifestVo = manifestDao.selectManifestInfoByDeployName(vo.getDeploymentName());
		deploymentName = vo.getDeploymentName();
		}
		if ( StringUtils.isEmpty(deploymentName) ) {
			throw new CommonException("notfound.servicepack.delete.exception",
					"배포정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		
		BufferedReader br = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			DeleteMethod deleteMethod = new DeleteMethod(DirectorRestHelper.getDeleteDeploymentURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
		
			int statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				String taskId = "1";
				
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("배포 삭제 중...");
				}
				
				if ( vo != null ){
					dao.deleteServicePackInfoRecord(vo.getId());
					manifestVo.setDeployStatus(null);
					manifestDao.updateManifestInfo(manifestVo);
				}
				
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("서비스팩 플랫폼 삭제가 성공하였습니다. ");
				}
				
			} else {
				if(LOGGER.isDebugEnabled()) LOGGER.debug("배포삭제 중 오류가 발생하였습니다.");
			}
		} catch ( Exception e) {
			if(LOGGER.isDebugEnabled()) LOGGER.debug("배포삭제 중 Exception이 발생하였습니다.");
		} finally {
			try {
				fis.close();
				isr.close();
				br.close();
			} catch ( Exception e ) {
				if( LOGGER.isErrorEnabled() ) LOGGER.error( e.getMessage() );  
			}
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 삭제 비동기식 요청
	 * @title               : deleteDeployAsync
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Async
	public void deleteDeployAsync(ServicePackParamDTO dto) {
		deleteDeploy(dto);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
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
		return vo;
	}
}
