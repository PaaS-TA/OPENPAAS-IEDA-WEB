package org.openpaas.ieda.web.deploy.bootstrap;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.Principal;

import org.openpaas.ieda.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootStrapParamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Transactional
@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class BootstrapServiceTest{
	
	@Autowired BootstrapDAO bootstrapDAO;
	private final static Logger LOGGER = LoggerFactory.getLogger(BootstrapServiceTest.class);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치
	 * @title               : testDeployAsync
	 * @return            : void
	***************************************************/
	public void testDeployAsync(BootStrapParamDTO.Install dto, Principal principal)throws Exception {
		
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		String deploymentFileName = null;
		BootstrapVO bootStrapVo  = null;
		String publicIp = "";
		
		//BootstrapVO bootStrapVo = bootstrapDao.bootstrapFindOne(Integer.parseInt(dto.getId()));
		bootStrapVo = setbootstrapFindOne("OPENSTACK");
		if ( bootStrapVo != null ) {
			bootStrapVo.setUpdateUserId(sessionInfo.getUserId());
			publicIp = bootStrapVo.getPublicStaticIp();
			deploymentFileName = bootStrapVo.getDeploymentFile();
		}
			
		if ( StringUtils.isEmpty(deploymentFileName)) {
			throw new CommonException("notfound.bootstrap.delete.exception",
					"배포파일 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
		}
		
		String status = "started";
		String accumulatedLog = "";
		File deploymentFile = null;
		DirectorInfoDTO directorInfo = null;

		try {
			String deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
			deploymentFile = new File(deployFile);
			
			if( deploymentFile.exists() ) {
				//1. 배포상태 설정
				status = "deploying";
				saveDeployStatus(bootStrapVo, status);
				accumulatedLog = "test Success deploying";
				
				if ( bootStrapVo != null ) bootStrapVo.setDeployLog(accumulatedLog);
				if ( accumulatedLog.contains("Failed deploying")) {
					status = "error";
					saveDeployStatus(bootStrapVo, status);
				}else {
					directorInfo = setDirectorInfo(publicIp, 25555, "admin", "admin");
					if ( directorInfo == null ) {
						status = "error";
						saveDeployStatus(bootStrapVo, status);
					} else {
						status = "done";
						saveDeployStatus(bootStrapVo, status);
					}
				}
			}else{
				if ( bootStrapVo != null ) bootStrapVo.setDeployLog(accumulatedLog);
				status = "error";
				saveDeployStatus(bootStrapVo, status);
			}
		}catch ( Exception e) {
			status = "error";
			if ( bootStrapVo != null ) bootStrapVo.setDeployLog(accumulatedLog);
			saveDeployStatus(bootStrapVo, status);
			
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포상태를 설정하여 저장
	 * @title               : saveDeployStatus
	 * @return            : BootstrapVO
	***************************************************/
	public BootstrapVO saveDeployStatus(BootstrapVO bootstrapVo, String status) {
		if ( bootstrapVo == null ) return null;
		bootstrapVo.setDeployStatus(status);
		try{
			assertTrue(bootstrapDAO.updateBootStrapInfo(bootstrapVo) == 1);
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("bootstrap flatform install success!!!");
			}
		}catch(AssertionError e){
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("bootstrap flatform install fail!!!");
			}
		}
		return bootstrapVo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 정보 설정
	 * @title               : setDirectorInfo
	 * @return            : DirectorInfoDTO
	***************************************************/
	public DirectorInfoDTO setDirectorInfo(String directorUrl, int port, String userId, String password){
		DirectorInfoDTO dto = new DirectorInfoDTO();
		dto.setUser("tester");
		dto.setUuid("tset-9682-47d2-9522-32fa5fa6c4f7");
		dto.setVersion("1.3147.0 (00000000)");
		dto.setName("tester");
		dto.setCpi("test-cpi(openstack)");
		
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : bootstrap vo 설정
	 * @title               : setbootstrapFindOne
	 * @return            : BootstrapVO
	***************************************************/
	public BootstrapVO setbootstrapFindOne(String iaas) throws Exception{
		
		BootstrapVO vo = new BootstrapVO();

		vo.setId(1);
		if("OPENSTACK".equals(iaas)){
			/** Openstack Info **/
			
			vo.setOpenstackAuthUrl("bootstrap-openstack-authUrl");
			vo.setOpenstackTenant("bosh");
			vo.setOpenstackUserName("bosh");
			vo.setOpenstackApiKey("1234");
			
		}else{
			/**AWS Setting Info**/
			vo.setIaasType("AWS");
			vo.setAwsAccessKeyId("bootstrap-aws");
			vo.setAwsSecretAccessId("boostrap-aws-secret");
			vo.setAwsRegion("m.east");
			vo.setAwsAvailabilityZone("m.east_1");
		}
		vo.setCreateUserId("tester");
		vo.setUpdateUserId("tester");
		vo.setDefaultSecurityGroups("test-security");
		vo.setPrivateKeyName("test-key");
		vo.setPrivateKeyPath("test-key.pem");
		
		/** Default Info **/
		vo.setDeploymentName("bosh");
		vo.setDirectorName("test-bosh");
		vo.setBoshRelease("bosh-233.tgz");
		vo.setBoshCpiRelease("bosh-openstack-cpi-release-14.tgz");
		/** Network Info **/
		vo.setSubnetId("text-subnetId-12345");
		vo.setPrivateStaticIp("10.0.100.11");
		vo.setPublicStaticIp("10.0.20.6");
		vo.setSubnetRange("10.0.20.0/24");
		vo.setSubnetGateway("10.0.20.1");
		vo.setSubnetDns("8.8.8.8");
		vo.setNtp("0.asia.pool.ntp.org");
		/** Resource Info **/
		vo.setStemcell("light-bosh-stemcell-3147-aws-xen-ubuntu-trusty-go_agent.tgz");
		vo.setCloudInstanceType("m1.large");
		vo.setBoshPassword("1234");
		vo.setDeploymentFile("openstack-microbosh-test-1.yml");
		vo.setDeployStatus("");
		vo.setDeployLog("");
		
		return vo;
	}

}
