package org.openpaas.ieda.web.deploy.cf;

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
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.cf.dto.CfParamDTO;
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
public class CfServiceTest {
	
	@Autowired private CfDAO cfDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_CF"; //배포 할 플랫폼명
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String MANIFEST_PATH = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets";
	private final static Logger LOGGER = LoggerFactory.getLogger(CfServiceTest.class);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 파일 정보 저장
	 * @title               : saveCfInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void saveCfInfo(){
		//1.1 insert Default Info
		CfVO vo = setDefaultInfo();
		cfDao.insertCfInfo(vo);
		//1.2 insert Network Info
		List<NetworkVO> networkList = setNetworkInfo();
		networkDao.insertNetworkList(networkList);
		//1.3 insert Resource Info
		ResourceVO resourceVo = setResourceInfo();
		resourceDao.insertResourceInfo(resourceVo);
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Cf 설치
	 * @title               : deploy
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void deploy(CfParamDTO.Install dto, Principal principal, String install){
		
		CfVO vo = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		String deploymentFileName = null;
		
		saveCfInfo();
		
		vo = cfDao.selectCfInfoById(Integer.parseInt(dto.getId()));
		if ( vo != null ) deploymentFileName = vo.getDeploymentFile();
			
		if ( StringUtils.isEmpty(deploymentFileName) ) {
			throw new CommonException("notfound.cf.exception",
					"배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		if ( vo != null ) {
			vo.setDeployStatus("deploying");
			vo.setUpdateUserId(sessionInfo.getUserId());
			cfDao.updateCfInfo(vo);
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
			
			String deployFile = MANIFEST_PATH  + SEPARATOR + deploymentFileName;
			
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
						LOGGER.debug("CF 설치를 성공하였습니다.");
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
			cfDao.updateCfInfo(vo);
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기 처리 방식으로 deploy 호출
	 * @title               : deployAsync
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void deployAsync(CfParamDTO.Install dto, Principal principal, String install) {
		deploy(dto, principal, install);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 플랫폼 삭제 요청
	 * @title               : deleteDeploy
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void deleteDeploy(CfParamDTO.Delete dto) {
		
		CfVO vo = null;
		String deploymentName = null;
		
		saveCfInfo();
		
		vo = cfDao.selectCfInfoById(Integer.parseInt(dto.getId()));
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
					cfDao.deleteCfInfoRecord(vo.getId());
					networkDao.deleteNetworkInfoRecord( vo.getId(), codeVo.getCodeName() );
					resourceDao.deleteResourceInfo( vo.getId(), codeVo.getCodeName() );
				}
				
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("CF 플랫폼 삭제가 성공하였습니다. ");
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
	@Rollback(true)
	public void deleteDeployAsync(CfParamDTO.Delete dto) {
		deleteDeploy(dto);
	}	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 설정
	 * @title               : setDefaultInfo
	 * @return            : CfVO
	***************************************************/
	public CfVO  setDefaultInfo(){
		CfVO vo = new CfVO();
		vo.setId(1);
		vo.setIaasType("openstack");
		vo.setDiegoYn("true");
		vo.setDeploymentName("test-openstack");
		vo.setDirectorUuid("test-director-Uuid");
		vo.setReleaseName("cf");
		vo.setReleaseVersion("236");
		vo.setAppSshFingerprint("test-app-ssh-Finger-print");
		vo.setDeaMemoryMB(32768);
		vo.setDeaDiskMB(8192);
		vo.setDomain("172.xx.xx.100.xip.io");
		vo.setDescription("test-domain");
		vo.setDomainOrganization("test-org");
		vo.setProxyStaticIps("172.xx.xx.100");
		vo.setLoginSecret("1234");
		vo.setKeyFile("vsphere-cf-key-1.yml");
		vo.setCountryCode("KR");
		vo.setStateName("Seoul");
		vo.setLocalityName("Seoul");
		vo.setOrganizationName("PaaS");
		vo.setUnitName("unit");
		vo.setEmail("paas@example.co.kr");
		
		vo.setDeploymentFile("openstack-cf-1-test.yml");
		vo.setUpdateUserId("tester");
		vo.setCreateUserId("tester");
		
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
	
}
