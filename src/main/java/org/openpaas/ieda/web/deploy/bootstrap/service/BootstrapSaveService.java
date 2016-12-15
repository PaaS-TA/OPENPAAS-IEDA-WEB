package org.openpaas.ieda.web.deploy.bootstrap.service;

import javax.transaction.Transactional;

import org.hsqldb.lib.StringUtil;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootStrapParamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BootstrapSaveService {

	@Autowired private BootstrapDAO bootstrapDao;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : AWS 정보 저장
	 * @title               : saveAwsInfo
	 * @return            : BootstrapVO
	***************************************************/
	@Transactional
	public BootstrapVO saveAwsInfo(BootStrapParamDTO.Aws dto, String flag){
		
		BootstrapVO vo;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		if( StringUtils.isEmpty(dto.getId()) || "Y".equals(flag)){
			vo = new BootstrapVO();
			if( "Y".equals(flag) ) vo.setId(1); 
			vo.setIaasType(dto.getIaas().trim());
			vo.setCreateUserId(sessionInfo.getUserId());
		} else { 
			vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		}
		
		//aws정보 저장
		vo.setAwsAccessKeyId(dto.getAccessKeyId());
		vo.setAwsSecretAccessId(dto.getSecretAccessId());
		vo.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		vo.setAwsRegion(dto.getRegion());
		vo.setAwsAvailabilityZone(dto.getAvailabilityZone());
		vo.setPrivateKeyName(dto.getPrivateKeyName());
		vo.setPrivateKeyPath(dto.getPrivateKeyPath());
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		if( StringUtils.isEmpty(dto.getId()) || "Y".equals(flag)){
			bootstrapDao.insertBootStrapInfo(vo);
		}else{
			bootstrapDao.updateBootStrapInfo(vo);
		}
		
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 오픈스택 정보 저장
	 * @title               : saveOpenstackInfo
	 * @return            : BootstrapVO
	***************************************************/
	@Transactional
	public BootstrapVO saveOpenstackInfo(BootStrapParamDTO.Openstack dto,String flag) {
		BootstrapVO vo;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		if( StringUtils.isEmpty(dto.getId()) || "Y".equals(flag)){
			vo = new BootstrapVO();
			if( "Y".equals(flag) ){ 
				vo.setId(Integer.parseInt(dto.getId())); 
			}
			vo.setIaasType(dto.getIaas());
			vo.setCreateUserId(sessionInfo.getUserId());
		} else { 
			vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		}
		
		vo.setOpenstackAuthUrl(dto.getAuthUrl());
		vo.setOpenstackTenant(dto.getTenant());
		vo.setOpenstackUserName(dto.getUserName());
		vo.setOpenstackApiKey(dto.getApiKey());
		vo.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		vo.setPrivateKeyName(dto.getPrivateKeyName());
		vo.setPrivateKeyPath(dto.getPrivateKeyPath());
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		if( StringUtils.isEmpty(dto.getId()) || "Y".equals(flag)){ 
			bootstrapDao.insertBootStrapInfo(vo); 
		}else{ 
			bootstrapDao.updateBootStrapInfo(vo); 
		}
		
		return vo;
	
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : vSphere 정보 저장
	 * @title               : saveVSphereInfo
	 * @return            : BootstrapVO
	***************************************************/
	@Transactional
	public BootstrapVO saveVSphereInfo(BootStrapParamDTO.VSphere dto,String flag){
		BootstrapVO vo;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		if( StringUtils.isEmpty(dto.getId()) || "Y".equals(flag)){
			vo = new BootstrapVO();
			if( "Y".equals(flag) ) vo.setId(1); 
			vo.setIaasType(dto.getIaas());
			vo.setCreateUserId(sessionInfo.getUserId());
		} else { 
			vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		}
		vo.setvCenterAddress(dto.getvCenterAddress());
		vo.setvCenterUser(dto.getvCenterUser());
		vo.setvCenterPassword(dto.getvCenterPassword());
		vo.setvCenterDatacenterName(dto.getvCenterName());
		vo.setvCenterVMFolder(dto.getvCenterVMFolder());
		vo.setvCenterTemplateFolder(dto.getvCenterTemplateFolder());
		vo.setvCenterDatastore(dto.getvCenterDatastore());
		vo.setvCenterPersistentDatastore(dto.getvCenterPersistentDatastore());
		vo.setvCenterDiskPath(dto.getvCenterDiskPath());
		vo.setvCenterCluster(dto.getvCenterCluster());
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		if( StringUtils.isEmpty(dto.getId()) || "Y".equals(flag)){ 
			bootstrapDao.insertBootStrapInfo(vo); 
		}else{ 
			bootstrapDao.updateBootStrapInfo(vo); 
		}
		
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : BootstrapVO
	***************************************************/
	@Transactional
	public BootstrapVO saveDefaultInfo(BootStrapParamDTO.Default dto) {
		
		BootstrapVO vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		vo.setDeploymentName(dto.getDeploymentName().trim());
		vo.setDirectorName(dto.getDirectorName().trim());
		vo.setBoshRelease(dto.getBoshRelease().trim());
		vo.setNtp(dto.getNtp());
		vo.setBoshCpiRelease(dto.getBoshCpiRelease().trim());
		vo.setEnableSnapshots(dto.getEnableSnapshots().trim());
		vo.setSnapshotSchedule(dto.getSnapshotSchedule().trim());
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		bootstrapDao.updateBootStrapInfo(vo);
		return vo;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 저장
	 * @title               : saveNetworkInfo
	 * @return            : BootstrapVO
	***************************************************/
	@Transactional
	public BootstrapVO saveNetworkInfo(BootStrapParamDTO.Network dto) {
		
		BootstrapVO vo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		vo.setSubnetId(dto.getSubnetId());
		vo.setPrivateStaticIp(dto.getPrivateStaticIp());
		vo.setPublicStaticIp(dto.getPublicStaticIp());
		vo.setSubnetRange(dto.getSubnetRange());
		vo.setSubnetGateway(dto.getSubnetGateway());
		vo.setSubnetDns(dto.getSubnetDns());
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		if("VSPHERE".equals(vo.getIaasType().toUpperCase())){
			vo.setPublicSubnetId(dto.getPublicSubnetId());
			vo.setPublicSubnetRange(dto.getPublicSubnetRange());
			vo.setPublicSubnetGateway(dto.getPublicSubnetGateway());
			vo.setPublicSubnetDns(dto.getPublicSubnetDns());
		}

		bootstrapDao.updateBootStrapInfo(vo);
		
		return vo;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 저장
	 * @title               : saveResourcesInfo
	 * @return            : BootstrapVO
	***************************************************/
	@Transactional
	public BootstrapVO saveResourcesInfo(BootStrapParamDTO.Resource dto) {

		String deplymentFileName = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		BootstrapVO bootstrapVo = null;
		//Dto Null Check
		if( dto.getId() != null  ) {
			bootstrapVo = bootstrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		}
		//Result Check
		if(bootstrapVo != null){
			if(bootstrapVo.getDeploymentFile() == null || StringUtil.isEmpty(bootstrapVo.getDeploymentFile())){
				 deplymentFileName  = makeDeploymentName(bootstrapVo);
			}else{
				deplymentFileName = bootstrapVo.getDeploymentFile();
			}
			
			bootstrapVo.setStemcell(dto.getStemcell());
			bootstrapVo.setCloudInstanceType(dto.getCloudInstanceType());
			bootstrapVo.setBoshPassword(dto.getBoshPassword());
			bootstrapVo.setResourcePoolCpu(dto.getResourcePoolCpu());
			bootstrapVo.setResourcePoolRam(dto.getResourcePoolRam());
			bootstrapVo.setResourcePoolDisk(dto.getResourcePoolDisk());

			bootstrapVo.setDeploymentFile(deplymentFileName);
			bootstrapVo.setUpdateUserId(sessionInfo.getUserId());
		}
		
		bootstrapDao.updateBootStrapInfo(bootstrapVo);
		return bootstrapVo;
	}	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일명 생성
	 * @title               : makeDeploymentName
	 * @return            : String
	***************************************************/
	public String makeDeploymentName(BootstrapVO bootstrapVo ){
		String settingFileName = "";
		if(bootstrapVo.getIaasType() != null || bootstrapVo.getId() != null){
			settingFileName = bootstrapVo.getIaasType().toLowerCase() + "-microbosh-"+ bootstrapVo.getId() +".yml";
		}else{
			throw new CommonException("ioExcepion.bootstrap.exception",
					"BOOTSTRAP 배포 파일명을 생성할 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		return settingFileName;
	}
}