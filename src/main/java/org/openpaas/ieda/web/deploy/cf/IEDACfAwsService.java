package org.openpaas.ieda.web.deploy.cf;

import java.util.Date;

import org.openpaas.ieda.web.deploy.cf.CfParam.Cf;
import org.openpaas.ieda.web.deploy.cf.CfParam.Network;
import org.openpaas.ieda.web.deploy.cf.CfParam.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.cfg.ConfigFeature;

@Service
public class IEDACfAwsService {
	
	@Autowired
	IEDACfAwsRepository awsRepository;

	public IEDACfAwsConfig saveAwsCfInfo(Cf dto) {
		IEDACfAwsConfig config;
		Date now = new Date();
		if( StringUtils.isEmpty(dto.getId()) ){
			config = new IEDACfAwsConfig();
			config.setCreatedDate(now);		
		}else{
			config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		}
		
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorUuid(dto.getDirectorUuid());
		config.setDomain(dto.getDomain());
		config.setUpdatedDate(now);
		
		return awsRepository.save(config);
	}
	
	public IEDACfAwsConfig saveAwsNetworkInfo(Network dto){
		IEDACfAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		config.setSubnetReserved(dto.getSubnetDns());;
		config.setSubnetStatic(dto.getSubnetStatic());
		config.setCloudSubnet(dto.getCloudSubnet());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDACfAwsConfig saveAwsResourceInfo( Resource dto){
		IEDACfAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setStemcellName(dto.getStemcellName());
		config.setStemcellVersion(dto.getStemcellVersion());
		config.setAvailabilityZone(dto.getAvailabilityZone());
		config.setInstanceType(dto.getInstanceType());
		config.setBoshPassword(dto.getBoshPassword());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}

}
