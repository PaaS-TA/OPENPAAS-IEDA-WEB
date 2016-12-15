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
import org.openpaas.ieda.web.deploy.common.dao.key.KeyDAO;
import org.openpaas.ieda.web.deploy.common.dao.key.KeyVO;
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
	@Autowired private KeyDAO keyDao;
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
		//1.3 insert Uaa Key Info
		KeyVO uaaKeyVo = setUaaKeyInfo();
		keyDao.insertKeyInfo(uaaKeyVo);
		//1.4 insert Consul Key Info
		KeyVO consulKeyVo = setConsulKeyInfo();
		keyDao.insertKeyInfo(consulKeyVo);
		//1.5 insert Blobstore Key Info
		KeyVO blobstoreKeyVo = setBlobstoreKeyInfo();
		keyDao.insertKeyInfo(blobstoreKeyVo);
		//1.6 insert Resource Info
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
					keyDao.deleteKeyInfo( vo.getId(), codeVo.getCodeName() );
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
		vo.setLoginSecret("test-login-security");
		//프록시 정보 - HAProxy 공인 IP
		vo.setProxyStaticIps("172.XX.XXX.103");
		
		//프록시 정보 - HAProxy 인증서
		String sslPemPub = "-----BEGIN CERTIFICATE-----" + "\n";
		sslPemPub += "MIICnzCCAggCCQCKDfbzvFEfUTANBgkqhkiG9w0BAQsFADCBkzELMAkGA1UEBhMC" + "\n";
		sslPemPub += "S1IxDjAMBgNVBAgMBVNlb3VsMQ4wDAYDVQQHDAVTZW91bDEQMA4GA1UECgwHY2xv" + "\n";
		sslPemPub += "dWQ0dTEMMAoGA1UECwwDT0NQMSAwHgYDVQQDDBcqLjE3Mi4xNi4xMDAuMTA5Lnhp" + "\n";
		sslPemPub += "...testing" + "\n";
		vo.setSslPemPub(sslPemPub);
		
		//프록시 정보 - HAProxy 개인키
		String sslPemRsa = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		sslPemRsa += "MIICXQIBAAKBgQDpfkbjspe++72gufsWV7kfT9wjMTxeWp4LmML7qt2NSSuTQ05E" + "\n";
		sslPemRsa += "choQei0FMj1AV2A2nHbnEahyPNNoUpV7Oc2DlJYREZVzfok+6qYSGbHZBKzp2kiO" + "\n";
		sslPemRsa += "E07E75mLAs5vHAWv3CBKFsxfJ2GZf+3FfLChVsKpLImywHrwwq27SODnhQIDAQAB" + "\n";
		sslPemRsa += "...testing" + "\n";
		vo.setSslPemRsa(sslPemRsa);
		
		//암호화 키 
		vo.setEncryptKeys("test-encryptKeys");
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
	 * @description   : Uaa Key 정보 설정
	 * @title               : setUaaKeyInfo
	 * @return            : KeyVO
	***************************************************/
	public KeyVO setUaaKeyInfo(){
		KeyVO keyVo = new KeyVO();
		
		//1.1 Uaa Info
		keyVo.setId(1);
		keyVo.setDeployType(CODE_NAME);
		keyVo.setKeyType(1310);
		//공개키
		String verificationKey = "-----BEGIN PUBLIC KEY-----" +"\n";
		verificationKey += "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1kp7Wg/cyq12DWTin7Tu"+"\n";
		verificationKey += "HKZjUolmOxj93iMj4PePxrvHgTkLs4xA5smR9w6BhCMJ/B0fpJvca8TqXgvVtDfx" + "\n";
		verificationKey += "2ui9NuDQKB477mOfg/SHrB2h9G9JZdsJdbIqSEiXW0XugJU/vm3qiV/RTisZYhX4" + "\n";
		verificationKey += "...testing" + "\n";
		verificationKey += "-----END PUBLIC KEY-----";
		keyVo.setPublicKey(verificationKey);
		
		//개인키
		String signingKey= "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		signingKey += "MIIEowIBAAKCAQEA1kp7Wg/cyq12DWTin7TuHKZjUolmOxj93iMj4PePxrvHgTkL" + "\n";
		signingKey += "s4xA5smR9w6BhCMJ/B0fpJvca8TqXgvVtDfx2ui9NuDQKB477mOfg/SHrB2h9G9J" + "\n";
		signingKey += "ZdsJdbIqSEiXW0XugJU/vm3qiV/RTisZYhX4P8kXcbQZJBKdqrHaAjJijrsUqp78" + "\n";
		signingKey += "...testing" + "\n";
		signingKey += "-----END RSA PRIVATE KEY-----";
		keyVo.setPrivateKey(signingKey);
		keyVo.setCreateUserId("tester");
		keyVo.setUpdateUserId("tester");
		
		return keyVo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Consul Key 정보 설정
	 * @title               : setConsulKeyInfo
	 * @return            : KeyVO
	***************************************************/
	public KeyVO setConsulKeyInfo(){
		KeyVO keyVo = new KeyVO();
		
		keyVo.setId(1);
		keyVo.setDeployType(CODE_NAME);
		keyVo.setKeyType(1320);
		
		String agentCert = "-----BEGIN CERTIFICATE-----" + "\n";
		agentCert += "MIIEJjCCAg6gAwIBAgIRAJFxJohnE9e10yrz0P9QET0wDQYJKoZIhvcNAQELBQAw" + "\n";
		agentCert += "EzERMA8GA1UEAxMIY29uc3VsQ0EwHhcNMTYwNjMwMDEwOTA4WhcNMTgwNjMwMDEw" + "\n";
		agentCert += "OTA4WjAXMRUwEwYDVQQDEwxjb25zdWwgYWdlbnQwggEiMA0GCSqGSIb3DQEBAQUA" + "\n";
		agentCert += "...testing" + "\n";
		agentCert += "-----END CERTIFICATE-----";
		keyVo.setAgentCert(agentCert);
		
		//에이전트 개인키
		String agentKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		agentKey += "MIIEogIBAAKCAQEAwAG6admvDNWfWgmH2PKAcqXPGiayFTcQZLQLxjFgjEmjyv8r" + "\n";
		agentKey += "5A2mg58fLOG59VuGMLTjAsEuqR2rkBjsSEoVwiRkC108bGoGQi2eHj2UtImYAfw1" + "\n";
		agentKey += "x2YAbpocIyAc70Rb90CF/R05BuLlLRZ+fVQOn0OoGd3Cba3PwMJ2Nz0HonrEBFcE" + "\n";
		agentKey += "...testing" + "\n";
		agentKey += "-----END RSA PRIVATE KEY-----";
		keyVo.setAgentKey(agentKey);
		
		//서버 CA 인증서
		String caCert = "-----BEGIN CERTIFICATE-----" + "\n";
		caCert += "MIIFBzCCAu+gAwIBAgIBATANBgkqhkiG9w0BAQsFADATMREwDwYDVQQDEwhjb25z" + "\n";
		caCert += "dWxDQTAeFw0xNjA2MzAwMTA4NTlaFw0yNjA2MzAwMTA5MDZaMBMxETAPBgNVBAMT" + "\n";
		caCert += "CGNvbnN1bENBMIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA21gc29p5" + "\n";
		caCert += "...testing" + "\n";
		caCert += "-----END CERTIFICATE-----";
		keyVo.setCaCert(caCert);
		
		//서버 인증서
		String serverCert = "-----BEGIN CERTIFICATE-----" + "\n";
		serverCert += "MIIELzCCAhegAwIBAgIQC/1znIT58wJhzfjeFU9EbzANBgkqhkiG9w0BAQsFADAT" + "\n";
		serverCert += "MREwDwYDVQQDEwhjb25zdWxDQTAeFw0xNjA2MzAwMTA5MDdaFw0xODA2MzAwMTA5" + "\n";
		serverCert += "MDdaMCExHzAdBgNVBAMTFnNlcnZlci5kYzEuY2YuaW50ZXJuYWwwggEiMA0GCSqG" + "\n";
		serverCert += "...testing" + "\n";
		serverCert += "-----END CERTIFICATE-----";
		keyVo.setServerCert(serverCert);
		
		//서버 개인키
		String serverKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		serverKey += "MIIEpAIBAAKCAQEA1V2Q0MwP2ucCvCDuXgVrShUH9g+uXDkyUQh1lXuylGW2tbQw" + "\n";
		serverKey += "v8bijtVvGYJaWNFSPOoPBbU03nw7e+jPrHbNt1PcrmHTOLqvZZwJ1nGs93LefpMv" + "\n";
		serverKey += "lUeg7omYDTi8BU3Y+zmZH3yik9QIcxRStTWJtFrg45H2DhP2DT1v+dIg2AjLgYtC" + "\n";
		serverKey += "...testing" + "\n";
		serverKey += "-----END RSA PRIVATE KEY-----";
		keyVo.setServerKey(serverKey);
		
		keyVo.setCreateUserId("tester");
		keyVo.setUpdateUserId("tester");
		
		return keyVo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Blobstore Key 정보 설정
	 * @title               : setBlobstoreKeyInfo
	 * @return            : KeyVO
	***************************************************/
	public KeyVO setBlobstoreKeyInfo(){
		KeyVO keyVo = new KeyVO();
		
		keyVo.setId(1);
		keyVo.setDeployType(CODE_NAME);
		keyVo.setKeyType(1330);
		
		//1.1 Blobstore Tls Cert Info
		String blobstoreTlsCert = "-----BEGIN CERTIFICATE-----" + "\n";
		blobstoreTlsCert += "MIIENDCCAhygAwIBAgIQfZsdWwO8eOvEphuoeM3qsTANBgkqhkiG9w0BAQsFADAR" + "\n";
		blobstoreTlsCert += "MQ8wDQYDVQQDEwZibG9iQ0EwHhcNMTYwNjMwMDEwOTQ1WhcNMTgwNjMwMDEwOTQ1" + "\n";
		blobstoreTlsCert += "WjAoMSYwJAYDVQQDEx1ibG9ic3RvcmUuc2VydmljZS5jZi5pbnRlcm5hbDCCASIw" + "\n";
		blobstoreTlsCert += "...testing" + "\n";
		blobstoreTlsCert += "-----END CERTIFICATE-----";
		keyVo.setTlsCert(blobstoreTlsCert);
		
		//1,2 Blobstore Private Key Info
		String blobstorePrivateKey = "-----BEGIN RSA PRIVATE KEY-----" + "\n";
		blobstorePrivateKey += "MIIEowIBAAKCAQEAxKqzlMyLyFRnw31br+nVbBI6SV+RRAnOaLq66MM37w/mRUoh" + "\n";
		blobstorePrivateKey += "nk4EQVMLgHTnV3Rb7ZGpD2fS+ARd6HEmIl0RwLEgBu/TGD91PCzBsibipxxD8M/u" + "\n";
		blobstorePrivateKey += "adYmJvQFGCpnXg9bJi42cUCWOy8QRTx4HGuqZAWBfbbFLKDZDFAcXu4/aNML+ZSu" + "\n";
		blobstorePrivateKey += "...testing" + "\n";
		blobstoreTlsCert += "-----END RSA PRIVATE KEY-----";
		keyVo.setPrivateKey(blobstorePrivateKey);
		
		//1.3 blobstore Ca Cert Info
		String blobstoreCaCert = "-----BEGIN CERTIFICATE-----" + "\n";
		blobstoreCaCert += "MIIFAzCCAuugAwIBAgIBATANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDEwZibG9i" + "\n";
		blobstoreCaCert += "Q0EwHhcNMTYwNjMwMDEwOTQ0WhcNMjYwNjMwMDEwOTQ1WjARMQ8wDQYDVQQDEwZi" + "\n";
		blobstoreCaCert += "bG9iQ0EwggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQCpiIZ3VyNfK9CV" + "\n";
		blobstoreCaCert += "...testing" + "\n";
		blobstoreCaCert += "-----END CERTIFICATE-----";
		keyVo.setCaCert(blobstoreCaCert);
		
		keyVo.setCreateUserId("tester");
		keyVo.setUpdateUserId("tester");
		
		return keyVo;
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
