package org.openpaas.ieda.web.deploy.diego;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
public class DiegoServiceTest {
	
	@Autowired private DiegoDAO diegoDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private SimpMessagingTemplate messagingTemplate;
	final private static String CODE_NAME="DEPLOY_TYPE_DIEGO"; //배포 할 플랫폼명
	final private static String MESSAGE_ENDPOINT = "/deploy/diego/install/logs"; 
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String MANIFEST_PATH = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets";
	private final static Logger LOGGER = LoggerFactory.getLogger(DiegoServiceTest.class);
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 정보 설정
	 * @title               : insertDiegoInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void insertDiegoInfo() {
		//save the default info
		diegoDao.insertDiegoDefaultInfo(setDefaultDiegoInfo());
		//save the netowrk info
		networkDao.insertNetworkList(setNetworkList());
		//save the resource info
		resourceDao.insertResourceInfo(setResource());
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 설정
	 * @title               : setDefaultDiegoInfo
	 * @return            : DiegoVO
	***************************************************/
	public DiegoVO setDefaultDiegoInfo(){
		DiegoVO vo = new DiegoVO();
		vo.setId(1);
		vo.setIaasType("OPENSTACK");
		vo.setDeploymentName("cf-openstack-diego-test");
		vo.setDirectorUuid("86299086-1710-4022-b48f-c5ef6a507924");
		vo.setDiegoReleaseName("diego");
		vo.setDiegoReleaseVersion("0.1481.0");
		vo.setCflinuxfs2rootfsreleaseName("cflinuxfs2-rootfs");
		vo.setCflinuxfs2rootfsreleaseVersion("1.21.0");
		vo.setGardenReleaseName("garden-linux");
		vo.setGardenReleaseVersion("0.339.0");
		vo.setEtcdReleaseName("etcd");
		vo.setEtcdReleaseVersion("63");
		vo.setUpdateUserId("tester");
		vo.setCfDeployment("openstack-cf-test-1.yml");
		vo.setCreateUserId("tester");
		vo.setUpdateUserId("tester");
		vo.setDeploymentFile("openstack-diego-test-1.yml");
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 목록 설정
	 * @title               : setNetworkList
	 * @return            : List<NetworkVO>
	***************************************************/
	public List<NetworkVO> setNetworkList(){
		List<NetworkVO> networkList = new ArrayList<NetworkVO>();
		for(int i=0;i<2;i++){
			NetworkVO vo = new NetworkVO();
			if(i==0){
				vo.setId(1);
				vo.setDeployType(CODE_NAME);
				vo.setNet("External");
				vo.setSubnetStaticFrom("10.10.10.10");
				vo.setCreateUserId("tester");
				vo.setUpdateUserId("tester");
			}
			if(i==1){
				vo.setId(1);
				vo.setDeployType(CODE_NAME);
				vo.setNet("Internal");
				vo.setSubnetRange("10.10.40.0/24");
				vo.setSubnetGateway("10.10.40.1");
				vo.setSubnetDns("8.8.8.8");
				vo.setSubnetReservedFrom("10.10.40.2");
				vo.setSubnetReservedTo("10.10.40.100");
				vo.setSubnetStaticFrom("10.10.40.101");
				vo.setSubnetStaticTo("10.10.40.127");
				vo.setSubnetId("83d71002-d8f7-4b52-af99-b5b1d16baf65");
				vo.setCloudSecurityGroups("bosh-security, cf-security");
				vo.setCreateUserId("tester");
				vo.setUpdateUserId("tester");
			}
			networkList.add(vo);
		}
		return networkList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 설정
	 * @title               : setResource
	 * @return            : ResourceVO
	***************************************************/
	public ResourceVO setResource(){
		ResourceVO resourceVo = new ResourceVO();
		resourceVo.setId(1);
		resourceVo.setDeployType(CODE_NAME);
		resourceVo.setStemcellName("bosh-openstack-kvm-ubuntu-trusty-go_agent");
		resourceVo.setStemcellVersion("3262");
		resourceVo.setBoshPassword("$6$4gDD3aV0rdqlrKC$2axHCxGKIObs6tAmMTqYCspcdvQXh3JJcvWOY2WGb4SrdXtnCyNaWlrf3WEqvYR2MYizEGp3kMmbpwBC6jsHt0");
		resourceVo.setSmallFlavor("m1.small");
		resourceVo.setMediumFlavor("m1.medium");
		resourceVo.setLargeFlavor("m1.large");
		resourceVo.setRunnerFlavor("m1.xlarge");
		resourceVo.setCreateUserId("tester");
		resourceVo.setUpdateUserId("tester");
		return resourceVo;
	}
	
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일 정보 설정
	 * @title               : setDeploymentFile
	 * @return            : DiegoVO
	***************************************************/
	public DiegoVO setDeploymentFile(){
		DiegoVO vo = new DiegoVO();
		vo.setDeploymentFile("openstack-diego-1.yml");
		vo.setUpdateUserId("tester");
		return vo;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 플랫폼 설치를 요청
	 * @title               : testDeploy
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void testDeploy(DiegoParamDTO.Install dto,Principal principal, String install) {
		
		DiegoVO vo = null;
		String deploymentFileName = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		
		insertDiegoInfo();
		
		vo = diegoDao.selectDiegoInfo(Integer.parseInt(dto.getId()));
		if ( vo != null ) deploymentFileName = vo.getDeploymentFile();
			
		if (  StringUtils.isEmpty(deploymentFileName) ) {
			throw new CommonException("notfound.diego.exception",
					"배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		if ( vo != null ) {
			vo.setDeployStatus("deploying");
			vo.setUpdateUserId(sessionInfo.getUserId());
			diegoDao.updateDiegoDefaultInfo(vo);
		}
		
		String status = "";
		String content = "";
		String temp = "";
		String taskId = "1";
		DirectorConfigVO defaultDirector = settingDefaultDirector();
		
		BufferedReader br = null;
		InputStreamReader isr = null;
		FileInputStream fis = null;

		try {
			
			//Instantiate an HttpClient
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			PostMethod postMethod = new PostMethod(DirectorRestHelper.getDeployURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod);
			postMethod.setRequestHeader("Content-Type", "text/yaml"); //header 정의
			
			String deployFile = MANIFEST_PATH + SEPARATOR + deploymentFileName;
			
			
			fis = new FileInputStream(deployFile);
			isr = new InputStreamReader(fis, "UTF-8");
			br = new BufferedReader(isr);
			
			while ( (temp=br.readLine()) != null) {
				content += temp + "\n";
			}
			
			postMethod.setRequestEntity(new StringRequestEntity(content, "text/yaml", "UTF-8"));
			
			//HTTP 요청 및 요청 결과
			int statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = postMethod.getResponseHeader("Location");
				taskId = "1";
				
				status = "done";
				
			} else {
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}

		}  catch ( IOException e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} catch ( Exception e) {
			status = "error";
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포 중 Exception이 발생하였습니다."));
		} finally {
			try {
				if ( fis != null ) fis.close();
				if ( isr != null ) isr.close();
				if ( br != null ) br.close();
			} catch ( Exception e ) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );  
				}
			}
		}
		
		if ( vo != null ) {
			vo.setDeployStatus(status);
			vo.setTaskId(Integer.parseInt(taskId));
			vo.setUpdateUserId(sessionInfo.getUserId());
			diegoDao.updateDiegoDefaultInfo(vo);
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 플랫폼 삭제를 요청
	 * @title               : testdeleteDeploy
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void testdeleteDeploy(DiegoParamDTO.Delete dto, Principal principal) {
			
			DiegoVO config = null;
			String deploymentName = null;
			insertDiegoInfo();
			config = diegoDao.selectDiegoInfo(Integer.parseInt(dto.getId()));
			if ( config != null ) deploymentName = config.getDeploymentName();
				
			if ( StringUtils.isEmpty(deploymentName) ) {
				throw new CommonException("notfound.diegodelete.exception",
					"배포정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
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
				
				if ( config != null ) diegoDao.deleteDiegoInfoRecord(config.getId());
				
			} else {
				DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포삭제 중 오류가 발생하였습니다.[" + statusCode + "]"));
			}
	
		} catch ( Exception e) {
			DirectorRestHelper.sendTaskOutput(principal.getName(), messagingTemplate, MESSAGE_ENDPOINT, "error", Arrays.asList("배포삭제 중 Exception이 발생하였습니다."));
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
		
		return vo;
	}
	
}
