package org.openpaas.ieda.web.deploy.cf;

import java.util.Date;

import org.openpaas.ieda.common.IEDACommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDACfAwsService {
	
	@Autowired
	private IEDACfAwsRepository awsRepository;
	@Autowired
	private IEDACfService cfService;

	public IEDACfAwsConfig getAwsCfInfo(int id) {
		IEDACfAwsConfig config = null;
		try{
			config = awsRepository.findOne(id);
		}
		catch (Exception e){
			e.printStackTrace();
			throw new IEDACommonException("illigalArgument.cf.aws.exception",
					"해당하는 AWS CF가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}
	
	public IEDACfAwsConfig saveAwsCfInfo(CfParam.Default dto) {
		IEDACfAwsConfig config;
		Date now = new Date();
		if( StringUtils.isEmpty(dto.getId()) ){
			config = new IEDACfAwsConfig();
			config.setCreatedDate(now);		
		}else{
			config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		}
		// 1.1 Deployment 정보
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorUuid(dto.getDirectorUuid());
		config.setReleaseName(dto.getReleaseName());
		config.setReleaseVersion(dto.getReleaseVersion());
		config.setAppSshFingerprint(dto.getAppSshFingerprint());
		
		// 1.2 기본정보
		config.setDomain(dto.getDomain());
		config.setDescription(dto.getDescription());
		config.setDomainOrganization(dto.getDomainOrganization());
		// 1.3 프록시 정보
		config.setProxyStaticIps(dto.getProxyStaticIps());
		config.setSslPemPub(dto.getSslPemPub());
		config.setSslPemRsa(dto.getSslPemRsa());
		
		config.setUpdatedDate(now);
		
		return awsRepository.save(config);
	}
	
	public IEDACfAwsConfig saveAwsUaaCfInfo(CfParam.Uaa dto) {
		IEDACfAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setLoginSecret(dto.getLoginSecret());
		config.setSigningKey(dto.getSigningKey());
		config.setVerificationKey(dto.getVerificationKey());
		Date now = new Date();
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDACfAwsConfig saveAwsConsulCfInfo(CfParam.Consul dto) {
		IEDACfAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setAgentCert(dto.getAgentCert());
		config.setAgentKey(dto.getAgentKey());
		config.setCaCert(dto.getCaCert());
		config.setEncryptKeys(dto.getEncryptKeys());
		config.setServerCert(dto.getServerCert());
		config.setServerKey(dto.getServerKey());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDACfAwsConfig saveAwsNetworkInfo(CfParam.AwsNetwork dto){
		IEDACfAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		
		config.setSubnetReservedFrom(dto.getSubnetReservedFrom());;
		config.setSubnetReservedTo(dto.getSubnetReservedTo());;
		config.setSubnetStaticFrom(dto.getSubnetStaticFrom());
		config.setSubnetStaticTo(dto.getSubnetStaticTo());
		
		config.setSubnetId(dto.getSubnetId());
		config.setCloudSecurityGroups(dto.getCloudSecurityGroups());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		return awsRepository.save(config);
	}
	
	public IEDACfAwsConfig saveAwsResourceInfo(CfParam.Resource dto){
		IEDACfAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setStemcellName(dto.getStemcellName());
		config.setStemcellVersion(dto.getStemcellVersion());
		config.setBoshPassword(dto.getBoshPassword());
		
		String deplymentFileName = cfService.createSettingFile(Integer.parseInt(dto.getId()), "AWS");
		config.setDeploymentFile(deplymentFileName);
		Date now = new Date();
		config.setUpdatedDate(now);
		
		return awsRepository.save(config);
	}
}