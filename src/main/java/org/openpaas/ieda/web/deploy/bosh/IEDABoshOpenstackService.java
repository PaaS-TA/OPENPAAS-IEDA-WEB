package org.openpaas.ieda.web.deploy.bosh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.CommonUtils;
import org.openpaas.ieda.web.common.ReplaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABoshOpenstackService {

	@Autowired
	private IEDABoshOpenstackRepository opentstackRepository;
	
	@Autowired
	private IEDABoshService boshService;
	
	public IEDABoshOpenstackConfig getOpenstackInfo(int id){
		return opentstackRepository.findOne(id);
	}

	
	public IEDABoshOpenstackConfig saveOpenstackInfo(BoshParam.Openstack dto){
		IEDABoshOpenstackConfig config = null;
		Date now = new Date();
		if( StringUtils.isEmpty(dto.getId()) ){
			config = new IEDABoshOpenstackConfig();
		}
		else{
			config = opentstackRepository.findOne(Integer.parseInt(dto.getId()));
		}

		config.setAuthUrl(dto.getAuthUrl());
		config.setTenant(dto.getTenant());
		config.setUserName(dto.getUserName());
		config.setApiKey(dto.getApiKey());
		config.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		config.setPrivateKeyName(dto.getPrivateKeyName());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);

		return opentstackRepository.save(config);
	}
	
	public IEDABoshOpenstackConfig saveBoshInfo(BoshParam.OsBosh dto){
		IEDABoshOpenstackConfig config = opentstackRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorUuid(dto.getDirectorUuid());
		config.setReleaseVersion(dto.getReleaseVersion());	
		
		Date now = new Date();
		config.setUpdatedDate(now);
		
		return opentstackRepository.save(config);
	}
	
	public IEDABoshOpenstackConfig saveOsNetworkInfo(BoshParam.OsNetwork dto){
		IEDABoshOpenstackConfig config = opentstackRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setPublicStaticIp(dto.getPublicStaticIp());
		config.setSubnetId(dto.getSubnetId());
		config.setSubnetStaticFrom(dto.getSubnetStaticFrom());
		config.setSubnetStaticTo(dto.getSubnetStaticTo());
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		
		Date now = new Date();
		config.setUpdatedDate(now);
		
		return opentstackRepository.save(config);
	}
	
	public IEDABoshOpenstackConfig saveOsResourceInfo(BoshParam.OsResource dto){
		
		IEDABoshOpenstackConfig config = opentstackRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setStemcellName(dto.getStemcellName());
		config.setStemcellVersion(dto.getStemcellVersion());
		config.setCloudInstanceType(dto.getCloudInstanceType());
		config.setBoshPassword(dto.getBoshPassword());
		
		String deplymentFileName = boshService.createSettingFile(Integer.parseInt(dto.getId()), "OPENSTACK");
		config.setDeploymentFile(deplymentFileName);
		return opentstackRepository.save(config);
	
	}
	
	public IEDABoshOpenstackConfig getOpentstackInfo(int id) {
		IEDABoshOpenstackConfig config =  null;
		try{
			config = opentstackRepository.findOne(id);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"해당하는 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}
	
}