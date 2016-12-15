package org.openpaas.ieda.web.deploy.bosh;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.deploy.bosh.dao.BoshDAO;
import org.openpaas.ieda.web.deploy.bosh.dao.BoshVO;
import org.openpaas.ieda.web.deploy.bosh.dto.BoshParamDTO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
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
@Service
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class BoshServiceTest {
	
	@Autowired private BoshDAO boshDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_BOSH"; //배포 할 플랫폼명
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BoshServiceTest.class);
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 파일 정보 저장
	 * @title               : saveBoshInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void saveBoshInfo() {
		BoshVO vo = setOpenstackInfo();
		boshDao.saveBoshInfo(vo);
		BoshVO defaultInfo = saveDefaultInfo(vo);
		boshDao.updateBoshInfo(defaultInfo);
		List<NetworkVO> networkList = setNetworkInfo();
		networkDao.insertNetworkList(networkList);
		ResourceVO resourceVo = setResourceInfo();
		resourceDao.insertResourceInfo(resourceVo);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 정보 삭제 
	 * @title               : deleteBoshInfo
	 * @return            : void
	***************************************************/
	public void deleteBoshInfo(){
		BoshVO vo = deleteInfo();
		
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		boshDao.deleteBoshInfoRecord(vo.getId());
		networkDao.deleteNetworkInfoRecord(vo.getId(), codeVo.getCodeName());
		resourceDao.deleteResourceInfo(vo.getId(), codeVo.getCodeName());
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 설치
	 * @title               : deploy
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void deploy(BoshParamDTO.Install dto, Principal principal){
		
		BoshVO vo = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		String deploymentFileName = null;
		
		saveBoshInfo();
		
		vo = boshDao.selectBoshDetailInfo(Integer.parseInt(dto.getId()));
		if ( vo != null ) deploymentFileName = vo.getDeploymentFile();
			
		if ( StringUtils.isEmpty(deploymentFileName) ) {
			throw new CommonException("notfound.cf.exception",
					"배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		if ( vo != null ) {
			vo.setDeployStatus("deploying");
			vo.setUpdateUserId(sessionInfo.getUserId());
			boshDao.updateBoshInfo(vo);
		}
		
		String status = "";
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		String content = "";
		String temp = "";
		String taskId = "1";
		
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
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("BOSH 설치를 성공하였습니다.");
					}
				}else{
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("배포 중 오류가 발생하였습니다.[" + 400 + "]");
					}
				}
			} catch ( Exception e ) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("배포 중 오류가 발생하였습니다.[" + 400 + "]");
				}
			}
		}
		if ( vo != null ) {
			vo.setDeployStatus(status);
			vo.setTaskId(Integer.parseInt(taskId));
			vo.setUpdateUserId(sessionInfo.getUserId());
			boshDao.updateBoshInfo(vo);
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기 처리 방식으로 deploy 호출
	 * @title               : deployAsync
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void deployAsync(BoshParamDTO.Install dto, Principal principal) {
		deploy(dto, principal);
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BOSH 플랫폼 삭제 요청
	 * @title               : deleteDeploy
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void deleteDeploy(BoshParamDTO.Delete dto) {
		
		BoshVO vo = null;
		String deploymentName = null;
		
		saveBoshInfo();
		
		vo = boshDao.selectBoshDetailInfo(Integer.parseInt(dto.getId()));
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		if ( vo != null ) deploymentName = vo.getDeploymentName();
			
		if ( StringUtils.isEmpty(deploymentName) ) {
			throw new CommonException("notfound.cfdelete.exception",
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
					boshDao.deleteBoshInfoRecord(vo.getId());
					networkDao.deleteNetworkInfoRecord( vo.getId(), codeVo.getCodeName() );
					resourceDao.deleteResourceInfo( vo.getId(), codeVo.getCodeName() );
				}
				
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("bosh 플랫폼 삭제가 성공하였습니다. ");
				}
				
			} else {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("배포삭제 중 오류가 발생하였습니다.[" + 400 + "]");
				}
			}
		} catch ( Exception e) {
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("배포삭제 중 Exception이 발생하였습니다.");
			}
		} finally {
			try {
				fis.close();
				isr.close();
				br.close();
			} catch ( Exception e ) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );  
				}
			}
		}

	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기 처리 방식으로 deleteDeploy 호출
	 * @title               : deleteDeployAsync
	 * @return            : void
	***************************************************/
	@Async
	@Rollback(true)
	public void deleteDeployAsync(BoshParamDTO.Delete dto) {
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
		vo.setDirectorUrl("172.16.XXX.XXX");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setDirectorVersion("1.3252.0");
		
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 정보 설정
	 * @title               : setOpenstackInfo
	 * @return            : BoshVO
	***************************************************/
	public BoshVO setOpenstackInfo(){
		BoshVO vo = null;
		vo = new BoshVO();
		vo.setId(1);
		vo.setCreateUserId("tester");
		vo.setIaasType("OPENSTACK");
		vo.setOpenstackAuthUrl("10.10.10.10");
		vo.setOpenstackTenant("Tenant");
		vo.setOpenstackUserName("bosh");
		vo.setOpenstackApiKey("bosh-key");
		vo.setDefaultSecurityGroups("Security-group");
		vo.setPrivateKeyName("keymap");
		vo.setUpdateUserId("tester");
		return vo; 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 설정
	 * @title               : saveDefaultInfo
	 * @return            : BoshVO
	***************************************************/
	public BoshVO saveDefaultInfo(BoshVO vo){
		vo.setId(1);
		vo.setDeploymentName("openstack-bosh-test");
		vo.setDeploymentFile("bosh-openstack-test-1.yml");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setReleaseVersion("bosh/256");	
		vo.setUpdateUserId("tester");
		vo.setNtp("ntp");
		vo.setDirectorName("directorName");
		vo.setSnapshotSchedule("");
		vo.setEnableSnapshots("false");
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 설정
	 * @title               : setNetworkInfo
	 * @return            : List<NetworkVO>
	***************************************************/
	public List<NetworkVO> setNetworkInfo(){
		List<NetworkVO> networkList = new ArrayList<NetworkVO>();
		for(int i=0; i <2; i++){
			NetworkVO vo = new NetworkVO();
			vo.setId(1);
			vo.setDeployType(CODE_NAME);
			if(i == 0){
				vo.setNet("External");
				vo.setSubnetStaticFrom("10.0.20.101");
			}else{
				vo.setNet("Internal");
				vo.setSubnetRange("192.0.20.0/24");
				vo.setSubnetGateway("192.0.20.1");
				vo.setSubnetReservedFrom("192.0.20.2");
				vo.setSubnetReservedTo("192.0.20.100");
				vo.setSubnetStaticFrom("192.0.20.101");
				vo.setSubnetStaticTo("192.0.20.127");
				vo.setSubnetDns("8.8.8.8");
				vo.setSubnetId("Internal");
				vo.setCloudSecurityGroups("test-security");
			}
			vo.setCreateUserId("tester");
			vo.setUpdateUserId("tester");
			networkList.add(vo);
		}
		return networkList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장
	 * @title               : setResourceInfo
	 * @return            : ResourceVO
	***************************************************/
	public ResourceVO setResourceInfo(){
		ResourceVO resourceVo = new ResourceVO();
		
		resourceVo.setId(1);
		resourceVo.setDeployType(CODE_NAME);
		resourceVo.setStemcellName("bosh-openstack-kvm-ubuntu-trusty-go_agent");
		resourceVo.setStemcellVersion("3262");
		resourceVo.setBoshPassword("test-password");
		resourceVo.setSmallFlavor("m1.small");
		resourceVo.setMediumFlavor("m1.medium");
		resourceVo.setLargeFlavor("m1.large");
		resourceVo.setRunnerFlavor("m1.large");
		
		resourceVo.setCreateUserId("tester");
		resourceVo.setUpdateUserId("tester");
		
		return resourceVo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 설정
	 * @title               : deleteInfo
	 * @return            : BoshVO
	***************************************************/
	public BoshVO deleteInfo(){
		BoshVO vo = new BoshVO();
		vo.setId(1);
		vo.setDeploymentName("openstack-bosh-test");
		vo.setDeploymentFile("bosh-openstack-test-1.yml");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setReleaseVersion("bosh/256");	
		vo.setUpdateUserId("tester");
		vo.setNtp("ntp");
		vo.setDirectorName("directorName");
		vo.setSnapshotSchedule("");
		vo.setEnableSnapshots("false");
		return vo;
	}
}
