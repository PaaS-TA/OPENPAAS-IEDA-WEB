package org.openpaas.ieda.web.config.bootstrap;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

import org.openpaas.ieda.common.IEDACommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABootstrapOpenstackService {

	@Autowired
	private IEDABootstrapOpenstackRepository openstackRepository;
	
	@Autowired
	private IEDABootstrapService bootstrapService;
	
	public IEDABootstrapOpenstackConfig getOpenstackInfo(int id) {
		IEDABootstrapOpenstackConfig config =  null;
		try{
			config = openstackRepository.findOne(id);
		}catch(Exception e){
			e.printStackTrace();
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"해당하는 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}

	public void deleteOpenstackInfo(int id) {
		IEDABootstrapOpenstackConfig config = null; 
		try{
			config = openstackRepository.findOne(id);
			openstackRepository.delete(id);
			bootstrapService.deleteDeploy(config.getDeploymentFile());
		} catch (EntityNotFoundException e) {
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"삭제할 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"BOOTSTRAP 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
	}

	public IEDABootstrapOpenstackConfig saveOpenstackInfoSave(BootStrapDto.OpenStack dto) {
		IEDABootstrapOpenstackConfig config;
		
		if( StringUtils.isEmpty(dto.getId())){
			config = new IEDABootstrapOpenstackConfig();
		} else { 
			config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		}
		
		config.setAuthUrl(dto.getAuthUrl());
		config.setTenant(dto.getTenant());
		config.setUserName(dto.getUserName());
		config.setApiKey(dto.getApiKey());
		config.setDefaultSecurityGroup(dto.getDefaultSecurityGroup());
		config.setPrivateKeyName(dto.getDefaultKeyName());
		config.setPrivateKeyPath(dto.getCloudPrivateKey());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}
	
	public IEDABootstrapOpenstackConfig saveOpenstackDefaultInfoSave(BootStrapDto.OpenstackDefault dto) {
		IEDABootstrapOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorName(dto.getDirectorName());
		config.setBoshRelease(dto.getBoshRelease());
		config.setBoshCpiRelease(dto.getBoshCpiRelease());
		
		Date now = new Date();
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}

	public IEDABootstrapOpenstackConfig saveOpenstackNetworkInfoSave(BootStrapDto.OpenstackNetwork dto) {
		IEDABootstrapOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		config.setSubnetId(dto.getSubnetId());
		config.setPrivateStaticIp(dto.getPrivateStaticIp());
		config.setPublicStaticIp(dto.getPublicStaticIp());
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		config.setNtp(dto.getNtp());
		Date now = new Date();
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}

	public IEDABootstrapOpenstackConfig saveOpenstackResourcesInfoSave(BootStrapDto.OpenstackResource dto) {
		IEDABootstrapOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		config.setStemcell(dto.getStemcell());
		config.setCloudInstanceType(dto.getCloudInstanceType());
		config.setBoshPassword(dto.getBoshPassword());

		Date now = new Date();
		config.setUpdatedDate(now);
		//Sample/Stub File Create & Merge create Deploy File
		String deplymentFileName = bootstrapService.createSettingFile(Integer.parseInt(dto.getId()), "OPENSTACK");
		config.setDeploymentFile(deplymentFileName);
		return openstackRepository.save(config);
	}	
}