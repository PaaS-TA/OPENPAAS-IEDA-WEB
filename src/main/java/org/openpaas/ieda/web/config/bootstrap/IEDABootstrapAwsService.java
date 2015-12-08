package org.openpaas.ieda.web.config.bootstrap;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
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
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;
	
	public IEDABootstrapAwsConfig getAwsInfo(int id){
		IEDABootstrapAwsConfig config =  null;
		try{
			log.info("==="+id);
			config = awsRepository.findOne(id);
			
			log.info("==="+config.toString());
		}catch(Exception e){
			e.printStackTrace();
			log.info("ERROR MESSAGE ::: " + e.getMessage());
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
	
	public Integer saveAwsInfo(IDEABootStrapInfoDto.Aws dto){
		Date now = new Date();
		IEDABootstrapAwsConfig config = null;
		log.info( "====== ID : " + dto.getId() );
		if(dto.getId() == null || "".equals(dto.getId())){
			log.info("====NEW=====");;
			config = new IEDABootstrapAwsConfig();
		}
		else {
			log.info("====UPDATE=====");;
			config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		}
		
		config.setAccessKey(dto.getAwsKey());
		config.setSecretAccessKey(dto.getAwsPw());
		config.setDefaultSecurityGroups(dto.getSecretGroupName());
		config.setDefaultKeyName(dto.getPrivateKeyName());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		config = awsRepository.save(config);
		return config.getId();
	}
	
	public void saveAwsNetworkInfos(IDEABootStrapInfoDto.Network dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setSubnetRange(dto.getSubnetRange());
		config.setDns(dto.getDns());
		config.setSubnetId(dto.getSubnetId());
		config.setGateway(dto.getGateway());
		config.setDirectorPrivateIp(dto.getDirectorPrivateIp());
		config.setDirectorPublicIp(dto.getDirectorPublicIp());
		Date now = new Date();
		config.setUpdatedDate(now);
		awsRepository.save(config);
	}
	
	public String saveAwsReleaseInfos(IDEABootStrapInfoDto.Resources dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setStemcellName(dto.getTargetStemcell());
		config.setInstanceType(dto.getInstanceType());
		config.setRegion(dto.getRegion());
		config.setAvailabilityZone(dto.getAvailabilityZone());
		config.setMicroBoshPw(dto.getMicroBoshPw());
		config.setDeploymentFile("aws-microbosh-setting-"+dto.getId()+".yml");
		Date now = new Date();
		config.setUpdatedDate(now);
		awsRepository.save(config);
		return bootstrapService.downloadSettingFile(Integer.parseInt(dto.getId()), "AWS");
	}
	
}
