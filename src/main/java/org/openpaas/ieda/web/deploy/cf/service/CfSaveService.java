package org.openpaas.ieda.web.deploy.cf.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.KeyInfoDTO;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.cf.dto.CfParamDTO;
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
import org.springframework.util.StringUtils;

@Service
public class CfSaveService {
	
	@Autowired private CfDAO cfDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_CF"; //배포 할 플랫폼명
	
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 기본 정보 저장
	 * @title         : saveDefaultInfo
	 * @return        : CfVO
	***************************************************/
	@Transactional
	public CfVO saveDefaultInfo(CfParamDTO.Default dto, String test) {
		CfVO vo;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		if( StringUtils.isEmpty(dto.getId()) ||  "Y".equals(test) ){
			vo = new CfVO();
			vo.setIaasType(dto.getIaas());
			vo.setDiegoYn(dto.getDiegoYn());
			vo.setCreateUserId(sessionInfo.getUserId());
			if( "Y".equals(test) ) vo.setId(Integer.parseInt(dto.getId()));
		}else{
			vo = cfDao.selectCfInfoById(Integer.parseInt(dto.getId()));
		}
		
		// 1.1 Deployment 정보
		vo.setDeploymentName(dto.getDeploymentName());
		vo.setDirectorUuid(dto.getDirectorUuid());
		vo.setReleaseName(dto.getReleaseName());
		vo.setReleaseVersion(dto.getReleaseVersion());
		vo.setAppSshFingerprint(dto.getAppSshFingerprint());
		vo.setDeaMemoryMB(Integer.parseInt(dto.getDeaMemoryMB()));
		vo.setDeaDiskMB(Integer.parseInt(dto.getDeaDiskMB()));
		
		// 1.2 기본정보
		vo.setDomain(dto.getDomain());
		vo.setDescription(dto.getDescription());
		vo.setDomainOrganization(dto.getDomainOrganization());
		vo.setLoginSecret(dto.getLoginSecret());
		
		//1.3 PaaS-TA 모니터링 
		vo.setPaastaMonitoringUse(dto.getPaastaMonitoringUse());
		vo.setIngestorIp(dto.getIngestorIp());
		vo.setIngestorPort(dto.getIngestorPort());
		
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		if( StringUtils.isEmpty(dto.getId()) ||  "Y".equals(test) )
			cfDao.insertCfInfo(vo);
		else
			cfDao.updateCfInfo(vo);
		
		return vo;
	}
	
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : Key 생성 정보 저장
	 * @title         : saveKeyInfo
	 * @return        : void
	***************************************************/
	public void saveKeyInfo(KeyInfoDTO dto){
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		CfVO vo = cfDao.selectCfInfoById(Integer.parseInt(dto.getId()));
		
		if( vo != null ){
			vo.setCountryCode(dto.getCountryCode());
			vo.setStateName(dto.getStateName());
			vo.setLocalityName(dto.getLocalityName());
			vo.setOrganizationName(dto.getOrganizationName());
			vo.setUnitName(dto.getUnitName());
			vo.setEmail(dto.getEmail());
			vo.setUpdateUserId(sessionInfo.getUserId());
			cfDao.updateCfInfo(vo);
		}else{
			throw new CommonException("cfsave.notfound.exception", "CF 정보가 조회되지 않습니다.", HttpStatus.NOT_FOUND);
		}
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 네트워크 정보 저장 
	 * @title         : saveNetworkInfo
	 * @return        : void
	***************************************************/
	@Transactional
	public void saveNetworkInfo(List<NetworkDTO> dto ){
		
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		List<NetworkVO> networkList = new ArrayList<NetworkVO>();
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		if(dto.size() > 0){
			for(NetworkDTO network: dto){
				NetworkVO vo = new NetworkVO();
				vo.setId(Integer.parseInt(network.getCfId()));
				vo.setDeployType(codeVo.getCodeName());
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
				vo.setAvailabilityZone(network.getAvailabilityZone());
				vo.setCreateUserId(sessionInfo.getUserId());
				vo.setUpdateUserId(sessionInfo.getUserId());
				
				networkList.add(vo);
			}
			int cnt = networkDao.selectNetworkList(Integer.parseInt(dto.get(0).getCfId()), codeVo.getCodeName()).size();
			if(cnt > 0 ){
				networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.get(0).getCfId()), codeVo.getCodeName());
			}
			networkDao.insertNetworkList(networkList);
		}
	}
	

	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : CF 리소스 정보 저장 및 배포 파일명 설정 
	 * @title         : saveResourceInfo
	 * @return        : Map<String,Object>
	***************************************************/
	@Transactional
	public Map<String, Object> saveResourceInfo(ResourceDTO dto, String test){
		
		String deploymentFile = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		Map<String, Object> map  = new HashMap<>();
		ResourceVO resourceVo = new ResourceVO();
		
		//1. select resource Info 
		CfVO vo = cfDao.selectCfResourceInfoById(Integer.parseInt(dto.getId()), CODE_NAME);
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		//2. set deploymentFIleName
		if(vo.getDeploymentFile() == null  || StringUtils.isEmpty(vo.getDeploymentFile()))
			deploymentFile = makeDeploymentName(vo);
		else
			deploymentFile = vo.getDeploymentFile();
		
		//3. set resourceVo(insert/update)
		if( vo.getResource().getId() != null ){
			resourceVo = vo.getResource();
		}else{
			resourceVo.setId(vo.getId());
			resourceVo.setDeployType(codeVo.getCodeName());
			resourceVo.setCreateUserId(sessionInfo.getUserId());
		}
		resourceVo.setUpdateUserId(sessionInfo.getUserId());
		resourceVo.setStemcellName(dto.getStemcellName());
		resourceVo.setStemcellVersion(dto.getStemcellVersion());
		resourceVo.setBoshPassword(dto.getBoshPassword());
		
		//vSphere Flavor setting 
		if( "vsphere".equals(vo.getIaasType().toLowerCase()) ){
			resourceVo.setSmallCpu(Integer.parseInt(dto.getSmallCpu()));
			resourceVo.setSmallDisk(Integer.parseInt(dto.getSmallDisk()));
			resourceVo.setSmallRam(Integer.parseInt(dto.getSmallRam()));
			resourceVo.setMediumCpu(Integer.parseInt(dto.getMediumCpu()));
			resourceVo.setMediumDisk(Integer.parseInt(dto.getMediumDisk()));
			resourceVo.setMediumRam(Integer.parseInt(dto.getMediumRam()));
			resourceVo.setLargeCpu(Integer.parseInt(dto.getLargeCpu()));
			resourceVo.setLargeDisk(Integer.parseInt(dto.getLargeDisk()));
			resourceVo.setLargeRam(Integer.parseInt(dto.getLargeRam()));
			resourceVo.setRunnerCpu(Integer.parseInt(dto.getRunnerCpu()));
			resourceVo.setRunnerDisk(Integer.parseInt(dto.getRunnerDisk()));
			resourceVo.setRunnerRam(Integer.parseInt(dto.getRunnerRam()));
		}else{
			//openstack/aws Flavor setting
			resourceVo.setSmallFlavor(dto.getSmallFlavor());
			resourceVo.setMediumFlavor(dto.getMediumFlavor());
			resourceVo.setLargeFlavor(dto.getLargeFlavor());
			resourceVo.setRunnerFlavor(dto.getRunnerFlavor());
		}
		
		vo.setDeploymentFile(deploymentFile);
		vo.setUpdateUserId(sessionInfo.getUserId());
		map.put("deploymentFile", deploymentFile);
		map.put("id", vo.getId());
		
		//4. update Cf Info
		cfDao.updateCfInfo(vo);
		//5. Insert OR Update Cf Resource Info
		if( vo.getResource().getId() == null || "Y".equals(test) )
			resourceDao.insertResourceInfo(resourceVo);
		else
			resourceDao.updateResourceInfo(resourceVo);
		
		return map;
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 배포 파일명 설정
	 * @title         : makeDeploymentName
	 * @return        : String
	***************************************************/
	public String makeDeploymentName(CfVO vo ){
		String settingFileName = "";
		if(vo.getIaasType() != null || vo.getId() != null){
			settingFileName = vo.getIaasType().toLowerCase() + "-cf-"+ vo.getId() +".yml";
		}else{
			throw new CommonException("cfInfoNotfound.cf.exception",
					"CF 배포 파일명을 생성할 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		return settingFileName;
	}
}
