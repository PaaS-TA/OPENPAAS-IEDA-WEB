package org.openpaas.ieda.web.deploy.bosh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.common.ReplaceItem;
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
	
	public IEDABoshOpenstackConfig getBoshOpenstackInfo(int id){
		return opentstackRepository.findOne(id);
	}

	public IEDABoshOpenstackConfig saveBoshInfo(BoshParam.OsBosh dto){
		IEDABoshOpenstackConfig config = null;
		Date now = new Date();
		if( dto.getId() == null || "".equals(dto.getId())){
			config = new IEDABoshOpenstackConfig();
		}
		else{
			config = opentstackRepository.findOne(Integer.parseInt(dto.getId()));
		}
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorUuid(dto.getDirectorUuid());
		config.setReleaseVersion(dto.getReleaseVersion());	
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		return opentstackRepository.save(config);
	}
	
	public IEDABoshOpenstackConfig saveOpenstackInfo(BoshParam.Openstack dto){
		IEDABoshOpenstackConfig config = opentstackRepository.findOne(Integer.parseInt(dto.getId()));

		config.setDirectorName(dto.getDirectorName());
		config.setDirectorStaticIp(dto.getDirectorStaticIp());
		config.setAuthUrl(dto.getAuthUrl());
		config.setTenant(dto.getTenant());
		config.setUserName(dto.getUserName());
		config.setApiKey(dto.getApiKey());
		config.setDefaultKeyName(dto.getDefaultKeyName());
		config.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());//? 배열?
		config.setNtp(dto.getNtp());
		Date now = new Date();
		config.setUpdatedDate(now);
		return opentstackRepository.save(config);
	}
	
	
	public IEDABoshOpenstackConfig saveOsNetworkInfo(BoshParam.OsNetwork dto){
		IEDABoshOpenstackConfig config = opentstackRepository.findOne(Integer.parseInt(dto.getId()));
		
		config.setSubnetStatic(dto.getSubnetStatic());
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		config.setCloudNetId(dto.getCloudNetId());
		config.setCloudSecurityGroups(dto.getCloudSecurityGroups());
		config.setCloudSubnet(dto.getCloudSubnet());
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
	
	public IEDABoshOpenstackConfig createTempFile(IEDABoshOpenstackConfig config, List<ReplaceItem> replaceItems){
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-fullbosh-setting.yml");
		URL stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-fullbosh-stub.yml");
		
		File tempDeploy;
		File stubDeploy;
		
		String content = "";
		String stubContent = "";
		String tempFile = "";
		String stubFile = "";
		String deployFileName = "aws-fullbosh-deploy-"+config.getId()+".yml";
		String deployFile = ""; 
		
		try {
			tempDeploy = new File(classPath.toURI());//resource.getFile();
			stubDeploy = new File(stubPath.toURI());
			
			tempFile = LocalDirectoryConfiguration.getTempDir() +  System.getProperty("file.separator") + "aws-fullbosh-setting-"+config.getId()+".yml";
			stubFile = LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubDeploy.getName();
			deployFile = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") +deployFileName;
			
			content = IOUtils.toString(new FileInputStream(tempDeploy), "UTF-8");
			stubContent = IOUtils.toString(new FileInputStream(stubDeploy), "UTF-8");
			
			for (ReplaceItem item : replaceItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(stubFile), "UTF-8");
			IOUtils.write(content, new FileOutputStream(tempFile), "UTF-8");
			
			if( boshService.setSpiffMerge(tempFile, stubFile, deployFile) ){
				config.setDeploymentFile(deployFileName);
				opentstackRepository.save(config);
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return config;
	}

/*	public void deleteOpentstackInfo(int id) {
		IEDABoshOpenstackConfig awsConfig = null; 
		try{
			awsConfig = opentstackRepository.findOne(id);
			opentstackRepository.delete(id);
			boshService.deleteDeploy(awsConfig.getDeploymentFile());
		} catch (EntityNotFoundException e) {
			throw new IEDACommonException("illigalArgument.bosh.exception",
					"삭제할 BOSH가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.bosh.exception",
					"BOSH 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
	}*/

	public IEDABoshOpenstackConfig getOpentstackInfo(int id) {
		IEDABoshOpenstackConfig config =  null;
		try{
			log.info("==="+id);
			config = opentstackRepository.findOne(id);
			
			log.info("==="+config.toString());
		}catch(Exception e){
			e.printStackTrace();
			log.info("ERROR MESSAGE ::: " + e.getMessage());
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"해당하는 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}
	
}