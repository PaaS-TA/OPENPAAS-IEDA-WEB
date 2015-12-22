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
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.common.ReplaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABoshAwsService {

	@Autowired
	private IEDABoshAwsRepository boshAwsRepository;
	
	@Autowired
	private IEDABoshService boshService;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

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
		config.setDefaultKeyName(dto.getDefaultKeyName());
		config.setDefaultSecurityGroups(dto.getDefaultSecurityGroups());
		config.setRegion(dto.getRegion());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);

		return boshAwsRepository.save(config);
	}
	
	public IEDABoshAwsConfig saveBoshInfo(BoshParam.AwsBosh dto){
		IEDABoshAwsConfig config = boshAwsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setBoshName(dto.getBoshName());
		config.setDirectorUuid(dto.getDirectorUuid());
		config.setPublicStaticIp(dto.getPublicStaticIp());
		config.setReleaseVersion(dto.getReleaseVersion());
		return boshAwsRepository.save(config);
	}
	
	public IEDABoshAwsConfig saveBoshNetworkInfo(BoshParam.AwsNetwork dto){
		IEDABoshAwsConfig config = boshAwsRepository.findOne(Integer.parseInt(dto.getId()));
		config.setSubnetStatic(dto.getSubnetStatic());
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		config.setCloudSubnet(dto.getCloudSubnet());
		config.setCloudSecurityGroups(dto.getCloudSecurityGroups());
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
		return boshAwsRepository.save(config);
	}
	
	public List<ReplaceItem> getBoshAwsReplaceItems(IEDABoshAwsConfig config){
		List<ReplaceItem> replaces = new ArrayList<>();
		//boshInfo
		replaces.add(new ReplaceItem("[boshName]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getBoshName()));
		replaces.add(new ReplaceItem("[directorUuid]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") +config.getDirectorUuid()));
		replaces.add(new ReplaceItem("[releaseVersion]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getReleaseVersion()));
		
		//network Info
		replaces.add(new ReplaceItem("[subnetStatic]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getSubnetStatic()));
		replaces.add(new ReplaceItem("[subnetRange]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getSubnetRange()));
		replaces.add(new ReplaceItem("[subnetGateway]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getSubnetGateway()));
		replaces.add(new ReplaceItem("[subnetDns]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getSubnetDns()));
		replaces.add(new ReplaceItem("[cloudSubnet]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getCloudSubnet()));
		replaces.add(new ReplaceItem("[cloudSecurityGroups]", iedaConfiguration.getStemcellDir() +  System.getProperty("file.separator") + config.getCloudSecurityGroups()));
		
		//resources Info
		replaces.add(new ReplaceItem("[stemcellName]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator") +config.getStemcellName()));
		replaces.add(new ReplaceItem("[stemcellVersion]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator") +config.getStemcellVersion()));
		replaces.add(new ReplaceItem("[boshPassword]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator") +config.getBoshPassword()));
		
		//Aws Info
		replaces.add(new ReplaceItem("[accessKeyId]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator")  + config.getAccessKeyId()));
		replaces.add(new ReplaceItem("[secretAccessKey]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator")  + config.getSecretAccessKey() ));
		replaces.add(new ReplaceItem("[defaultKeyName]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator")  + config.getDefaultKeyName()));
		replaces.add(new ReplaceItem("[defaultSecurityGroups]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator")  + config.getDefaultSecurityGroups()));
		replaces.add(new ReplaceItem("[region]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator") +config.getRegion()));
		return replaces;
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
			tempDeploy = new File(classPath.toURI());//resource.getFile();
			stubDeploy = new File(stubPath.toURI());
			
			tempFile = iedaConfiguration.getTempDir() +  System.getProperty("file.separator") + "aws-fullbosh-setting-"+config.getId()+".yml";
			stubFile = iedaConfiguration.getTempDir() + System.getProperty("file.separator") + stubDeploy.getName();
			deployFile = iedaConfiguration.getDeploymentDir() + System.getProperty("file.separator") +deployFileName;
			
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return config;
	}

	public void deleteAwsInfo(int id) {
		IEDABoshAwsConfig awsConfig = null; 
		try{
			awsConfig = boshAwsRepository.findOne(id);
			boshAwsRepository.delete(id);
			boshService.deleteDeploy(awsConfig.getDeploymentFile());
		} catch (EntityNotFoundException e) {
			throw new IEDACommonException("illigalArgument.bosh.exception",
					"삭제할 BOSH가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.bosh.exception",
					"BOSH 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
	}

	public IEDABoshAwsConfig getAwsInfo(int id) {
		IEDABoshAwsConfig config =  null;
		try{
			log.info("==="+id);
			config = boshAwsRepository.findOne(id);
			
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