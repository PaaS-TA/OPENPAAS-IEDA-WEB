package org.openpaas.ieda.web.deploy.diego;

import java.util.Date;

import org.openpaas.ieda.common.IEDACommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDADiegoOpenstackService {
	
	
	@Autowired
	private IEDADiegoOpenstackRepository openstackRepository;
	@Autowired
	private IEDADiegoService diegoService;

	public IEDADiegoOpenstackConfig getOpenstackInfo(int id) {
		IEDADiegoOpenstackConfig config = null;
		try{
			config = openstackRepository.findOne(id);
		}
		catch (Exception e){
			e.printStackTrace();
			throw new IEDACommonException("illigalArgument.diego.openstack.exception",
					"해당하는 OPENSTACK DIEGO가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}
	
	public IEDADiegoOpenstackConfig saveDefaultInfo(DiegoParam.Default dto) {
		IEDADiegoOpenstackConfig config;
		Date now = new Date();
		if( StringUtils.isEmpty(dto.getId()) ){
			config = new IEDADiegoOpenstackConfig();
			config.setCreatedDate(now);		
		}else{
			config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		}
		// 1.1 기본정보
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorUuid(dto.getDirectorUuid());
		config.setDiegoReleaseName(dto.getDiegoReleaseName());
		config.setDiegoReleaseVersion(dto.getDiegoReleaseVersion());
		config.setCfReleaseName(dto.getCfReleaseName());
		config.setCfReleaseVersion(dto.getCfReleaseVersion());
		config.setGardenLinuxReleaseName(dto.getGardenLinuxReleaseName());
		config.setGardenLinuxReleaseVersion(dto.getGardenLinuxReleaseVersion());
		config.setEtcdReleaseName(dto.getEtcdReleaseName());
		config.setEtcdReleaseVersion(dto.getEtcdReleaseVersion());
		
		//1.2 CF 정보
		config.setDomain(dto.getDomain());
		config.setDeployment(dto.getDeployment());
		config.setSecret(dto.getSecret());
		config.setEtcdMachines(dto.getEtcdMachines());
		config.setNatsMachines(dto.getNatsMachines());
		config.setConsulServersLan(dto.getConsulServersLan());
		config.setConsulAgentCert(dto.getConsulAgentCert());
		config.setConsulAgentKey(dto.getConsulAgentKey());
		config.setConsulCaCert(dto.getConsulCaCert());
		config.setConsulEncryptKeys(dto.getConsulEncryptKeys());
		config.setConsulServerCert(dto.getConsulServerCert());
		config.setConsulServerKey(dto.getConsulServerKey());
		
		config.setUpdatedDate(now);
		
		return openstackRepository.save(config);
	}
	
	public IEDADiegoOpenstackConfig saveDiegoInfo(DiegoParam.Diego dto) {
		IEDADiegoOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		//2.1 Diego 정보	
		config.setDiegoCaCert(dto.getDiegoCaCert());
		config.setDiegoClientCert(dto.getDiegoClientCert());
		config.setDiegoClientKey(dto.getDiegoClientKey());
		config.setDiegoEncryptionKeys(dto.getDiegoEncryptionKeys());
		config.setDiegoServerCert(dto.getDiegoServerCert());
		config.setDiegoServerKey(dto.getDiegoServerKey());
		
		//2.2 ETCD 정보
		config.setEtcdClientCert(dto.getEtcdClientCert());
		config.setEtcdClientKey(dto.getEtcdClientKey());
		config.setEtcdPeerCaCert(dto.getEtcdPeerCaCert());
		config.setEtcdPeerCert(dto.getEtcdPeerCert());
		config.setEtcdPeerKey(dto.getEtcdPeerKey());
		config.setEtcdServerCert(dto.getEtcdServerCert());
		config.setEtcdServerKey(dto.getEtcdServerKey());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}
	
	public IEDADiegoOpenstackConfig saveNetworkInfo(DiegoParam.OpenstackNetwork dto){
		IEDADiegoOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		
		config.setSubnetReservedFrom(dto.getSubnetReservedFrom());;
		config.setSubnetReservedTo(dto.getSubnetReservedTo());;
		config.setSubnetStaticFrom(dto.getSubnetStaticFrom());
		config.setSubnetStaticTo(dto.getSubnetStaticTo());
		
		config.setCloudNetId(dto.getCloudNetId());
		config.setCloudSecurityGroups(dto.getCloudSecurityGroups());
		
		config.setDiegoHostKey(dto.getDiegoHostKey());
		config.setDiegoServers(dto.getDiegoServers());
		config.setDiegoUaaSecret(dto.getDiegoUaaSecret());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}
	
	public IEDADiegoOpenstackConfig saveResourceInfo(DiegoParam.Resource dto){
		IEDADiegoOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		config.setStemcellName(dto.getStemcellName());
		config.setStemcellVersion(dto.getStemcellVersion());
		config.setBoshPassword(dto.getBoshPassword());
		
		String deplymentFileName = diegoService.createSettingFile(Integer.parseInt(dto.getId()), "OPENSTACK");
		config.setDeploymentFile(deplymentFileName);
		Date now = new Date();
		config.setUpdatedDate(now);
		
		return openstackRepository.save(config);
	}

}
