package org.openpaas.ieda.web.config.bootstrap;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

import org.openpaas.ieda.common.IEDACommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
	
	public void deleteAwsInfo(int id){
		IEDABootstrapAwsConfig awsConfig = null; 
		try{
			awsConfig = awsRepository.findOne(id);
			awsRepository.delete(id);
			bootstrapService.deleteDeploy(awsConfig.getDeploymentFile());
		} catch (EntityNotFoundException e) {
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"삭제할 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"BOOTSTRAP 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
	}
	
	public IEDABootstrapAwsConfig saveAwsInfo(BootStrapDto.Aws dto){
		Date now = new Date();
		IEDABootstrapAwsConfig config = null;
		if(dto.getId() == null || "".equals(dto.getId())){
			config = new IEDABootstrapAwsConfig();
		}
		else {
			config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		}
		
		config.setAccessKey(dto.getAwsKey());
		config.setSecretAccessKey(dto.getAwsPw());
		config.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		config.setDefaultKeyName(dto.getPrivateKeyName());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDABootstrapAwsConfig saveAwsNetworkInfos(BootStrapDto.Network dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setSubnetRange(dto.getSubnetRange());
		config.setDns(dto.getDns());
		config.setSubnetId(dto.getSubnetId());
		config.setGateway(dto.getGateway());
		config.setDirectorPrivateIp(dto.getDirectorPrivateIp());
		config.setDirectorPublicIp(dto.getDirectorPublicIp());
		Date now = new Date();
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDABootstrapAwsConfig saveAwsResourcesInfos(BootStrapDto.Resources dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setStemcellName(dto.getTargetStemcell());
		config.setInstanceType(dto.getInstanceType());
		config.setRegion(dto.getRegion());
		config.setAvailabilityZone(dto.getAvailabilityZone());
		config.setMicroBoshPw(dto.getMicroBoshPw());
		config.setNtp(dto.getNtp());
		Date now = new Date();
		config.setUpdatedDate(now);
		awsRepository.save(config);
		//Sample/Stub File Create & Merge create Deploy File
		String deplymentFileName = bootstrapService.createSettingFile(Integer.parseInt(dto.getId()), "AWS");
		config.setDeploymentFile(deplymentFileName);
		return awsRepository.save(config);
	}
	
}