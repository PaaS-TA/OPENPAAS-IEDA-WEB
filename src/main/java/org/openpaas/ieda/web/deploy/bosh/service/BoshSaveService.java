package org.openpaas.ieda.web.deploy.bosh.service;

import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.bosh.dao.BoshDAO;
import org.openpaas.ieda.web.deploy.bosh.dao.BoshVO;
import org.openpaas.ieda.web.deploy.bosh.dto.BoshParamDTO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BoshSaveService {
	
	@Autowired private BoshDAO boshDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_BOSH"; //배포 할 플랫폼명
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh AWS 정보 저장
	 * @title               : saveBoshAwsInfo
	 * @return            : BoshVO
	***************************************************/
	@Transactional
	public BoshVO saveBoshAwsInfo(BoshParamDTO.AWS dto, String test){
		BoshVO vo = null ;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		if( StringUtils.isEmpty(dto.getId()) ||  "Y".equals(test) ){
			vo = new BoshVO();
			vo.setCreateUserId(sessionInfo.getUserId());
			vo.setIaasType(dto.getIaas());
			if( "Y".equals(test) ) vo.setId(Integer.parseInt(dto.getId()));
		}
		else {
			vo = boshDao.selectBoshDetailInfo(Integer.parseInt(dto.getId()));
		}
		
		vo.setAwsAccessKeyId(dto.getAccessKeyId());
		vo.setAwsSecretAccessId(dto.getSecretAccessKey());
		vo.setPrivateKeyName(dto.getPrivateKeyName());
		vo.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		vo.setAwsRegion(dto.getRegion());
		vo.setUpdateUserId(sessionInfo.getUserId());
		vo.setAwsAvailabilityZone(dto.getAvailabilityZone());
		
		if( StringUtils.isEmpty(dto.getId()) ||  "Y".equals(test) ){
			boshDao.saveBoshInfo(vo);
		}else{
			boshDao.updateBoshInfo(vo);
		}
		
		return vo; 
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh Openstack 정보 저장
	 * @title               : saveOpenstackInfo
	 * @return            : BoshVO
	***************************************************/
	@Transactional
	public BoshVO saveOpenstackInfo(BoshParamDTO.Openstack dto, String test){
		BoshVO vo = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		if( StringUtils.isEmpty(dto.getId()) ||  "Y".equals(test) ){
			vo = new BoshVO();
			vo.setCreateUserId(sessionInfo.getUserId());
			vo.setIaasType(dto.getIaas());
			if( "Y".equals(test) ) vo.setId(Integer.parseInt(dto.getId()));
		}
		else{
			vo = boshDao.selectBoshDetailInfo(Integer.parseInt(dto.getId()));
		}
		
		vo.setOpenstackAuthUrl(dto.getAuthUrl());
		vo.setOpenstackTenant(dto.getTenant());
		vo.setOpenstackUserName(dto.getUserName());
		vo.setOpenstackApiKey(dto.getApiKey());
		
		vo.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		vo.setPrivateKeyName(dto.getPrivateKeyName());
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		if( StringUtils.isEmpty(dto.getId()) ||  "Y".equals(test)){
			boshDao.saveBoshInfo(vo);
		}else{
			boshDao.updateBoshInfo(vo);
		}
		
		return vo; 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Vsphere 기본 정보 저장
	 * @title               : saveVsphereInfo
	 * @return            : BoshVO
	***************************************************/
	@Transactional
	public BoshVO saveVsphereInfo(BoshParamDTO.VSphere dto, String test) {
		BoshVO vo;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		if( StringUtils.isEmpty(dto.getId())  ||  "Y".equals(test) ){
			vo = new BoshVO();
			vo.setIaasType(dto.getIaas());
			vo.setCreateUserId(sessionInfo.getUserId());
			if( "Y".equals(test) ) vo.setId(Integer.parseInt(dto.getId()));
		} else { 
			vo = boshDao.selectBoshDetailInfo(Integer.parseInt(dto.getId()));
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
		if( StringUtils.isEmpty(dto.getId())  ||  "Y".equals(test) ){ 
			boshDao.saveBoshInfo(vo); 
		}else{ 
			boshDao.updateBoshInfo(vo); 
		}
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 기본 정보 저장 
	 * @title               : saveDefaultInfo
	 * @return            : BoshVO
	***************************************************/
	@Transactional
	public BoshVO saveDefaultInfo(BoshParamDTO.DefaultInfo dto){
		BoshVO vo = boshDao.selectBoshDetailInfo(Integer.parseInt(dto.getId()));
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		vo.setDeploymentName(dto.getDeploymentName());
		vo.setDirectorUuid(dto.getDirectorUuid());
		vo.setReleaseVersion(dto.getReleaseVersion());	
		vo.setUpdateUserId(sessionInfo.getUserId());
		vo.setNtp(dto.getNtp());
		vo.setDirectorName(dto.getDirectorName());
		vo.setSnapshotSchedule(dto.getSnapshotSchedule());
		vo.setEnableSnapshots(dto.getEnableSnapshots());
		boshDao.updateBoshInfo(vo);
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Openstack 환경의 네트워크 정보 저장 
	 * @title               : saveNetworkInfo
	 * @return            : BoshVO
	***************************************************/
	@Transactional
	public BoshVO saveNetworkInfo(List<NetworkDTO> dto){
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		List<NetworkVO> networkList = new ArrayList<NetworkVO>();
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		if(dto.size() > 0){
			for(NetworkDTO network: dto){
				NetworkVO vo = new NetworkVO();
				vo.setId(Integer.parseInt(network.getBoshId()));
				vo.setDeployType(codeVo.getCodeName());
				vo.setPublicStaticIP(network.getPublicStaticIP());
				vo.setNet(network.getNet());
				vo.setSubnetRange(network.getSubnetRange());
				vo.setSubnetGateway(network.getSubnetGateway());
				vo.setSubnetDns(network.getSubnetDns());
				vo.setSubnetReservedFrom(network.getSubnetReservedFrom());
				vo.setSubnetReservedTo(network.getSubnetReservedTo());
				vo.setSubnetStaticFrom(network.getSubnetStaticFrom());
				vo.setSubnetStaticTo(network.getSubnetStaticTo());
				vo.setSubnetId(network.getSubnetId());
				vo.setCloudSecurityGroups(network.getCloudSecurityGroups());
				vo.setCreateUserId(sessionInfo.getUserId());
				vo.setUpdateUserId(sessionInfo.getUserId());
				networkList.add(vo);
			}
			int cnt = networkDao.selectNetworkList( Integer.parseInt(dto.get(0).getBoshId()), codeVo.getCodeName() ).size();
			if(cnt > 0){
				networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.get(0).getBoshId()), codeVo.getCodeName() );
			}
			networkDao.insertNetworkList(networkList);
		}
		BoshVO boshVO = new BoshVO();
		boshVO.setNetworks(networkList);
		return boshVO;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 및 배포파일 정보 저장 
	 * @title               : saveResourceInfo
	 * @return            : BoshVO
	***************************************************/
	@Transactional
	public BoshVO saveResourceInfo(ResourceDTO dto){
		String deplymentFileName = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		ResourceVO resourceVo = new ResourceVO();
		
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		BoshVO vo = boshDao.selectResourceInfoById(Integer.parseInt(dto.getId()), codeVo.getCodeName());
		
		if(vo.getDeploymentFile() == null  || StringUtils.isEmpty(vo.getDeploymentFile()))
			deplymentFileName = makeDeploymentName(vo);
		else
			deplymentFileName = vo.getDeploymentFile();
		
		//3. set resourceVo(insert/update)
		if( vo.getResource() != null ){
			resourceVo = vo.getResource();
			resourceVo.setId(vo.getId());
		}else{
			resourceVo.setId(vo.getId());
			resourceVo.setDeployType(codeVo.getCodeName());
			resourceVo.setCreateUserId(sessionInfo.getUserId());
		}
		resourceVo.setUpdateUserId(sessionInfo.getUserId());
		resourceVo.setStemcellName(dto.getStemcellName());
		resourceVo.setStemcellVersion(dto.getStemcellVersion());
		resourceVo.setBoshPassword(dto.getBoshPassword());
		if(!"VSPHERE".equals(vo.getIaasType())){
			resourceVo.setSmallFlavor(dto.getSmallFlavor());
			resourceVo.setMediumFlavor(dto.getMediumFlavor());
		}else{
			resourceVo.setSmallRam(Integer.parseInt(dto.getSmallRam()));
			resourceVo.setSmallCpu(Integer.parseInt(dto.getSmallCpu()));
			resourceVo.setSmallDisk(Integer.parseInt(dto.getSmallDisk()));
			resourceVo.setMediumCpu(Integer.parseInt(dto.getMediumCpu()));
			resourceVo.setMediumDisk(Integer.parseInt(dto.getMediumDisk()));
			resourceVo.setMediumRam(Integer.parseInt(dto.getMediumRam()));
		}
		vo.setDeploymentFile(deplymentFileName);
		vo.setUpdateUserId(sessionInfo.getUserId());
		boshDao.updateBoshInfo(vo);
		if( vo.getResource() == null)
			resourceDao.insertResourceInfo(resourceVo);
		else
			resourceDao.updateResourceInfo(resourceVo);
		vo.setResource(resourceVo);
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  Bosh Manifest 파일명 생성
	 * @title               : makeDeploymentName
	 * @return            : String
	***************************************************/
	public String makeDeploymentName(BoshVO boshVo ){
		String settingFileName = "";
		if(boshVo.getIaasType() != null || boshVo.getId() != null){
			settingFileName = boshVo.getIaasType().toLowerCase() + "-bosh-"+ boshVo.getId() +".yml";
		}else{
			throw new CommonException("notfoundBoshInfo.bosh.exception",
					"BOSH 배포 파일명을 생성할 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		return settingFileName;
	}

}
