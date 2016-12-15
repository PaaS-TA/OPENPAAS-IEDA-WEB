package org.openpaas.ieda.web.deploy.diego.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.common.dao.key.KeyDAO;
import org.openpaas.ieda.web.deploy.common.dao.key.KeyVO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceVO;
import org.openpaas.ieda.web.deploy.common.dto.network.NetworkDTO;
import org.openpaas.ieda.web.deploy.common.dto.resource.ResourceDTO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DiegoSaveService {
	@Autowired private DiegoDAO diegoDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private KeyDAO keyDao;
	@Autowired private CfDAO cfDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_DIEGO"; //배포 할 플랫폼명
	final static private int DIEGO_KEY=1410; 
	final static private int ETCD_KEY=1420; 
	final static private int ETCD_PEER_KEY=1430; 
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 기본정보 저장
	 * @title               : saveDefaultInfo
	 * @return            : DiegoVO
	***************************************************/
	public DiegoVO saveDefaultInfo(DiegoParamDTO.Default dto, String test) {
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		DiegoVO vo;
		if( StringUtils.isEmpty(dto.getId()) ||  "Y".equals(test) ){
			vo = new DiegoVO();
			if( "Y".equals(test) )vo.setId(Integer.parseInt(dto.getId()));
			vo.setCreateUserId(sessionInfo.getUserId());
			vo.setIaasType(dto.getIaas());
		}else{
			vo = diegoDao.selectDiegoInfo(Integer.parseInt(dto.getId()));
		}
		
		// 1.1 기본정보
		vo.setDeploymentName(dto.getDeploymentName());
		vo.setDirectorUuid(dto.getDirectorUuid());
		vo.setDiegoReleaseName(dto.getDiegoReleaseName());
		vo.setDiegoReleaseVersion(dto.getDiegoReleaseVersion());
		vo.setCflinuxfs2rootfsreleaseName(dto.getCflinuxfs2rootfsreleaseName());
		vo.setCflinuxfs2rootfsreleaseVersion(dto.getCflinuxfs2rootfsreleaseVersion());
		vo.setCfId(dto.getCfId());
		if(dto.getCfDeployment()!=null&&!dto.getCfDeployment().equals("")){
			vo.setCfDeployment(dto.getCfDeployment());
		}else if(!"Y".equals(test)){
			CfVO cfvo = cfDao.selectDeploymentFilebyDeploymentName(dto.getCfDeploymentName());
			//cf 야물 파일에 대한 경고 문 필요
			if(cfvo.getDeploymentFile()==null){
				throw new CommonException("notfound.deigo.exception",
						"CF 배포 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
			vo.setCfDeployment(cfvo.getDeploymentFile()); 
		}
		vo.setGardenReleaseName(dto.getGardenReleaseName());
		vo.setGardenReleaseVersion(dto.getGardenReleaseVersion());
		vo.setEtcdReleaseName(dto.getEtcdReleaseName());
		vo.setEtcdReleaseVersion(dto.getEtcdReleaseVersion());
		vo.setUpdateUserId(sessionInfo.getUserId());
		
		if( StringUtils.isEmpty(dto.getId()) || "Y".equals(test) ) { 
			diegoDao.insertDiegoDefaultInfo(vo);//저장
		}else{  
			diegoDao.updateDiegoDefaultInfo(vo);//수정 
		}
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 저장  
	 * @title               : saveDiegoInfo
	 * @return            : DiegoVO
	***************************************************/
	@Transactional
	public DiegoVO saveDiegoInfo(DiegoParamDTO.Diego dto) {
		//Diego Key 정보
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		DiegoVO vo = diegoDao.selectDiegoKeyInfoById(Integer.parseInt(dto.getId()), CODE_NAME, DIEGO_KEY);
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		KeyVO keyvo = new KeyVO();
		//2.1 Diego 정보	
		vo.setUpdateUserId(sessionInfo.getUserId());
		vo.setDiegoEncryptionKeys(dto.getDiegoEncryptionKeys());
		//Diego 정보 저장
		diegoDao.updateDiegoDefaultInfo(vo);
		keyvo.setId(Integer.parseInt(dto.getId()));
		keyvo.setDeployType(codeVo.getCodeName());
		keyvo.setKeyType(DIEGO_KEY);
		keyvo.setCaCert(dto.getDiegoCaCert());
		keyvo.setHostKey(dto.getDiegoHostKey());
		keyvo.setClientCert(dto.getDiegoClientCert());
		keyvo.setClientKey(dto.getDiegoClientKey());
		keyvo.setServerCert(dto.getDiegoServerCert());
		keyvo.setServerKey(dto.getDiegoServerKey());
		keyvo.setCreateUserId(sessionInfo.getUserId());
		keyvo.setUpdateUserId(sessionInfo.getUserId());
		if(vo.getKeys().size() > 0){
			keyDao.updateKeyInfo(keyvo);
		}else{
			keyDao.insertKeyInfo(keyvo);
		}
		List<KeyVO> keyList = new ArrayList<KeyVO>();
		keyList.add(keyvo);
		vo.setKeys(keyList);
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego ETCD 정보 저장  
	 * @title               : saveEtcdInfo
	 * @return            : DiegoVO
	***************************************************/
	@Transactional
	public DiegoVO saveEtcdInfo(DiegoParamDTO.Etcd dto) {
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		DiegoVO vo = diegoDao.selectDiegoKeyInfoById(Integer.parseInt(dto.getId()), CODE_NAME, ETCD_KEY);
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		KeyVO etcdKeyVo = new KeyVO();
		//2.2 ETCD 정보
		vo.setUpdateUserId(sessionInfo.getUserId());
		diegoDao.updateDiegoDefaultInfo(vo);
		etcdKeyVo.setId(Integer.parseInt(dto.getId()));
		etcdKeyVo.setKeyType(ETCD_KEY);
		etcdKeyVo.setDeployType(codeVo.getCodeName());
		etcdKeyVo.setClientCert(dto.getEtcdClientCert());
		etcdKeyVo.setClientKey(dto.getEtcdClientKey());
		etcdKeyVo.setServerCert(dto.getEtcdServerCert());
		etcdKeyVo.setServerKey(dto.getEtcdServerKey());
		etcdKeyVo.setCreateUserId(sessionInfo.getUserId());
		etcdKeyVo.setUpdateUserId(sessionInfo.getUserId());
		if(vo.getKeys().size() > 0 ){
			keyDao.updateKeyInfo(etcdKeyVo);
		}else{
			keyDao.insertKeyInfo(etcdKeyVo);
		}
		KeyVO PeerKeyvo = new KeyVO();
		PeerKeyvo.setId(Integer.parseInt(dto.getId()));
		PeerKeyvo.setKeyType(ETCD_PEER_KEY);
		PeerKeyvo.setDeployType(codeVo.getCodeName());
		PeerKeyvo.setCaCert(dto.getEtcdPeerCaCert());
		PeerKeyvo.setServerKey(dto.getEtcdPeerKey());
		PeerKeyvo.setServerCert(dto.getEtcdPeerCert());
		PeerKeyvo.setCreateUserId(sessionInfo.getUserId());
		PeerKeyvo.setUpdateUserId(sessionInfo.getUserId());
		if(vo.getKeys().size() > 0){
			keyDao.updateKeyInfo(PeerKeyvo);
		}else{
			keyDao.insertKeyInfo(PeerKeyvo);
		}
		List<KeyVO> keyList = new ArrayList<KeyVO>();
		keyList.add(etcdKeyVo);
		keyList.add(PeerKeyvo);
		vo.setKeys(keyList);
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 네트워크 정보 저장   
	 * @title               : saveNetworkInfo
	 * @return            : DiegoVO
	***************************************************/
	@Transactional
	public DiegoVO saveNetworkInfo(List<NetworkDTO> dto){
		
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		List<NetworkVO> networkList = new ArrayList<NetworkVO>();
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		if(dto.size() > 0){
			for(NetworkDTO network: dto){
				NetworkVO vo = new NetworkVO();
				vo.setId(Integer.parseInt(network.getDiegoId()));
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
				vo.setCreateUserId(sessionInfo.getUserId());
				vo.setUpdateUserId(sessionInfo.getUserId());
				networkList.add(vo);
			}
			int cnt = networkDao.selectNetworkList(Integer.parseInt(dto.get(0).getDiegoId()), codeVo.getCodeName()).size();
			if(cnt > 0){
				networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.get(0).getDiegoId()), codeVo.getCodeName());
			}
			networkDao.insertNetworkList(networkList);
		}
		
		DiegoVO vo = new DiegoVO();
		vo.setNetworks(networkList);
		return vo;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 리소스 저장
	 * @title               : saveResourceInfo
	 * @return            : Map<String,Object>
	***************************************************/
	@Transactional
	public Map<String, Object> saveResourceInfo(ResourceDTO dto, String test){
		String deploymentFile = null;
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		Map<String, Object> map  = new HashMap<>();
		ResourceVO resourceVo = new ResourceVO();
		
		DiegoVO vo = diegoDao.selectResourceInfoById(Integer.parseInt(dto.getId()), CODE_NAME);
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		if(vo.getDeploymentFile() == null  || StringUtils.isEmpty(vo.getDeploymentFile()))
			deploymentFile = makeDeploymentName(vo);
		else
			deploymentFile = vo.getDeploymentFile();
		
		//3. set resourceVo(insert/update)
		if( vo.getResource().getId() != null ){
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
		//Flavor setting
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
		
		//update Diego Info
		diegoDao.updateDiegoDefaultInfo(vo);
		//Insert OR Update Diego Resource Info
		if( vo.getResource().getId() == null  || "Y".equals(test))
			resourceDao.insertResourceInfo(resourceVo);
		else
			resourceDao.updateResourceInfo(resourceVo);
		return map;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 배포 파일 명 생성
	 * @title               : makeDeploymentName
	 * @return            : String
	***************************************************/
	public String makeDeploymentName(DiegoVO vo){
		String settingFileName = "";
		if(vo.getIaasType() != null || vo.getId() != null){
			settingFileName = vo.getIaasType().toLowerCase() + "-diego-"+ vo.getId() +".yml";
		}else{
			throw new CommonException("notfound.deigo.exception",
					"Diego 배포 파일명을 생성할 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		return settingFileName;
	}
}
