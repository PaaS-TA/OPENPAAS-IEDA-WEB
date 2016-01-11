package org.openpaas.ieda.web.deploy.bootstrap;

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
public class IEDABootstrapAwsService {

	@Autowired
	private IEDABootstrapAwsRepository awsRepository;
	
	@Autowired
	private IEDABootstrapService bootstrapService;
	
	public IEDABootstrapAwsConfig getAwsInfo(int id){
		IEDABootstrapAwsConfig config =  null;
		try{
			config = awsRepository.findOne(id);
		}catch(Exception e){
			e.printStackTrace();
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"해당하는 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}
	
	public IEDABootstrapAwsConfig saveAwsInfo(BootStrapDto.Aws dto){
		IEDABootstrapAwsConfig config;
		
		if(StringUtils.isEmpty(dto.getId())){
			config = new IEDABootstrapAwsConfig();
		}
		else {
			config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		}
		
		config.setAccessKeyId(dto.getAccessKeyId());
		config.setSecretAccessId(dto.getSecretAccessId());
		config.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		config.setRegion(dto.getRegion());
		config.setAvailabilityZone(dto.getAvailabilityZone());
		config.setPrivateKeyName(dto.getPrivateKeyName());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		
		Date now = new Date();
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDABootstrapAwsConfig saveAwsDefaultInfo(BootStrapDto.AwsDefault dto){
		IEDABootstrapAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorName(dto.getDirectorName());
		config.setBoshRelease(dto.getBoshRelease());
		config.setBoshCpiRelease(dto.getBoshCpiRelease());
		
		Date now = new Date();
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDABootstrapAwsConfig saveAwsNetworkInfos(BootStrapDto.AwsNetwork dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setSubnetId(dto.getSubnetId());
		config.setPrivateStaticIp(dto.getPrivateStaticIp());
		config.setPublicStaticIp(dto.getPublicStaticIp());
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		config.setNtp(dto.getNtp());
		Date now = new Date();
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDABootstrapAwsConfig saveAwsResourcesInfos(BootStrapDto.AwsResource dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setStemcell(dto.getStemcell());
		config.setCloudInstanceType(dto.getCloudInstanceType());
		config.setBoshPassword(dto.getBoshPassword());
		Date now = new Date();
		config.setUpdatedDate(now);
		
		//Sample/Stub File Create & Merge create Deploy File
		String deplymentFileName = bootstrapService.createSettingFile(Integer.parseInt(dto.getId()), "AWS");
		config.setDeploymentFile(deplymentFileName);
		return awsRepository.save(config);
	}
	
}