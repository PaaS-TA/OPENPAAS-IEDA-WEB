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
import org.openpaas.ieda.web.common.ReplaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABoshAwsService {

	@Autowired
	private IEDABoshAwsRepository boshAwsRepository;
	
	@Autowired
	private IEDABoshService boshService;
	
	public IEDABoshAwsConfig saveBoshAwsInfo(BoshParam.AWS dto){
		IEDABoshAwsConfig config = null ;
		Date now = new Date();
		if(dto.getId() == null || "".equals(dto.getId())){
			config = new IEDABoshAwsConfig();
		}
		else {
			config = boshAwsRepository.findOne(Integer.parseInt(dto.getId()));
		}
		config.setAccessKeyId(dto.getAccessKeyId());
		config.setSecretAccessKey(dto.getSecretAccessKey());
		config.setPrivateKeyName(dto.getPrivateKeyName());
		config.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		config.setRegion(dto.getRegion());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);

		return boshAwsRepository.save(config);
	}
	
	public IEDABoshAwsConfig saveBoshInfo(BoshParam.AwsBosh dto){
		IEDABoshAwsConfig config = boshAwsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setDeploymentName(dto.getDeploymentName());
		config.setDirectorUuid(dto.getDirectorUuid());
		config.setReleaseVersion(dto.getReleaseVersion());
		
		Date now = new Date();
		config.setUpdatedDate(now);

		return boshAwsRepository.save(config);
	}
	
	public IEDABoshAwsConfig saveBoshNetworkInfo(BoshParam.AwsNetwork dto){
		IEDABoshAwsConfig config = boshAwsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setPublicStaticIp(dto.getPublicStaticIp());
		config.setSubnetStaticFrom(dto.getSubnetStaticFrom());
		config.setSubnetStaticTo(dto.getSubnetStaticTo());
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		config.setSubnetId(dto.getSubnetId());
		
		Date now = new Date();
		config.setUpdatedDate(now);

		return boshAwsRepository.save(config);
	}
	
	public IEDABoshAwsConfig saveBoshResourceInfo(BoshParam.AwsResource dto){
		IEDABoshAwsConfig config = boshAwsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setStemcellName(dto.getStemcellName());
		config.setStemcellVersion(dto.getStemcellVersion());
		config.setBoshPassword(dto.getBoshPassword());
		config.setCloudInstanceType(dto.getCloudInstanceType());
		String deplymentFileName = boshService.createSettingFile(Integer.parseInt(dto.getId()), "AWS");
		config.setDeploymentFile(deplymentFileName);
		
		Date now = new Date();
		config.setUpdatedDate(now);

		return boshAwsRepository.save(config);
	}
	
	public IEDABoshAwsConfig createTempFile(IEDABoshAwsConfig config, List<ReplaceItem> replaceItems){
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
			tempDeploy = new File(classPath.toURI());
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
				boshAwsRepository.save(config);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return config;
	}

	public IEDABoshAwsConfig getAwsInfo(int id) {
		IEDABoshAwsConfig config =  null;
		try{
			config = boshAwsRepository.findOne(id);
		}catch(Exception e){
			e.printStackTrace();
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"해당하는 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}
	
}