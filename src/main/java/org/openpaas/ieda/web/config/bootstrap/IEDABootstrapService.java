package org.openpaas.ieda.web.config.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABootstrapService {

	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private IEDABootstrapAwsRepository awsRepository;
	
	@Autowired
	private IEDABootstrapOpenstackRepository openstackRepository;
	
	public List<BootstrapListDto> listBootstrap() {
		List<IEDABootstrapAwsConfig> awsConfigsList = awsRepository.findAll();
		log.info("### AWS List Size ::: " +  awsConfigsList.size() );
		//List<IEDABootstrapOpenstackConfig> openstackConfigsList = openstackRepository.findAll();
		//log.info("### AWS List Size ::: " +  awsConfigsList.size() );
		log.info("### AWSLIST :::" + awsConfigsList.toString());
		List<BootstrapListDto> listDtos = new ArrayList<>();
		
		if(awsConfigsList.size() > 0){
			for(int i=0; i < awsConfigsList.size();i++){
				IEDABootstrapAwsConfig awsConfig = awsConfigsList.get(i);
				BootstrapListDto dto = new BootstrapListDto();
				dto.setRecid(i);
				dto.setId(awsConfig.getId());
				dto.setIaas("AWS");
				dto.setCreatedDate(awsConfig.getCreatedDate());
				dto.setUpdatedDate(awsConfig.getUpdatedDate());
				dto.setDirectorPrivateIp(awsConfig.getDirectorPrivateIp());
				dto.setDirectorPublicIp(awsConfig.getDirectorPublicIp());
				listDtos.add(dto);
			}
		}
		log.info("### LIST :::" + listDtos.toString());
		
		/*if(openstackConfigsList.siBootstrapListDtoze() > 0 ){
			for(IEDABootstrapOpenstackConfig openstackConfig : openstackConfigsList){
				BootstrapListDto dto = new BootstrapListDto();
				dto.setId(openstackConfig.getId()); 
				dto.setIaas("OPENSTACK");
				dto.setCreatedDate(dto.getCreatedDate());
				dto.setUpdatedDate(dto.getUpdatedDate());
				dto.setDirectorPrivateIp(dto.getDirectorPrivateIp());
				dto.setDirectorPublicIp(dto.getDirectorPublicIp());
				listDtos.add(dto);
			}
		}*/
		
		if (listDtos.size() == 0) {
			throw new IEDACommonException("nocontent.bootstrap.exception", "BOOTSTRAP 정보가 존재하지 않습니다.",
					HttpStatus.NO_CONTENT);
		}

		return listDtos;
	}
	

	public void downloadSettingFile(Integer id, String iaas) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-microbosh-setting.yml");
		URL stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-microbosh-stub.yml");
		File tempDeploy;
		File stubDeploy;
		
		String content = "";
		String stubContent = "";
		String targetFileName = "aws-microbosh-setting-"+id+".yml";
		FileOutputStream fos = null;
		try {
			tempDeploy = new File(classPath.toURI());//resource.getFile();
			stubDeploy = new File(stubPath.toURI());
			content = IOUtils.toString(new FileInputStream(tempDeploy), "UTF-8");
			stubContent = IOUtils.toString(new FileInputStream(stubDeploy), "UTF-8");
			
			List<BootstrapItem> bootstrapItems = makeBootstrapItems(id, iaas);
			log.info(":::SIZE::: "+bootstrapItems.size());
			for (BootstrapItem item : bootstrapItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			log.info("*******************************************************");
			log.info("\n"+content+"\n");
			log.info("*******************************************************");
			
			IOUtils.write(stubContent, new FileOutputStream(iedaConfiguration.getTempDir() + stubDeploy.getName()), "UTF-8");
			IOUtils.write(content, new FileOutputStream(iedaConfiguration.getTempDir() + targetFileName), "UTF-8");
			if(iaas == "AWS"){
				IEDABootstrapAwsConfig config = awsRepository.findById(id);
				config.setDeploymentFile("aws-microbosh-setting-"+id+".yml");
				awsRepository.save(config);
			}else{
				IEDABootstrapOpenstackConfig config = openstackRepository.findById(id);
				config.setDeploymentFile("openstack-microbosh-setting-"+id+".yml");
				openstackRepository.save(config);
			}
			setSpiffMerge(stubDeploy.getName(), targetFileName);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public List<BootstrapItem> makeBootstrapItems(Integer id, String iaas) {
		
		List<BootstrapItem> items = new ArrayList<BootstrapItem>();
		
		if(iaas == "AWS"){
			IEDABootstrapAwsConfig  awsConfig = awsRepository.findOne(id);
			items.add(new BootstrapItem("[stemcell]", iedaConfiguration.getStemcellDir()+awsConfig.getStemcellName()));
			items.add(new BootstrapItem("[microboshPw]", awsConfig.getMicroBoshPw()));
			items.add(new BootstrapItem("[subnetRange]", awsConfig.getSubnetRange()));
			items.add(new BootstrapItem("[dns]", awsConfig.getDns()));
			items.add(new BootstrapItem("[subnetId]", awsConfig.getSubnetId()));
			items.add(new BootstrapItem("[gateway]", awsConfig.getGateway()));
			items.add(new BootstrapItem("[directorPrivateIp]", awsConfig.getDirectorPrivateIp()));
			items.add(new BootstrapItem("[directorPublicIp]", awsConfig.getDirectorPublicIp()));
			items.add(new BootstrapItem("[awsKey]", awsConfig.getAccessKey()));
			items.add(new BootstrapItem("[secretAccessKey]", awsConfig.getSecretAccessKey()));
			items.add(new BootstrapItem("[securGroupName]", awsConfig.getDefaultSecurityGroups()));
			items.add(new BootstrapItem("[privateKey]", awsConfig.getPrivateKeyPath()));
			items.add(new BootstrapItem("[region]", awsConfig.getAvailabilityZone()));
			items.add(new BootstrapItem("[flavor]", awsConfig.getInstanceType()));	
			
			
		}
		else{
			IEDABootstrapOpenstackConfig openstackConfig = openstackRepository.findOne(id);
			items.add(new BootstrapItem("[stemcell]", openstackConfig.getStemcellName()));
			items.add(new BootstrapItem("[microboshPw]", openstackConfig.getMicroBoshPw()));
			items.add(new BootstrapItem("[subnetRange]", openstackConfig.getSubnetRange()));
			items.add(new BootstrapItem("[dns]", openstackConfig.getDns()));
			items.add(new BootstrapItem("[subnetId]", openstackConfig.getSubnetId()));
			items.add(new BootstrapItem("[gateway]", openstackConfig.getGateway()));
			items.add(new BootstrapItem("[directorPrivateIp]", openstackConfig.getDirectorPrivateIp()));
			items.add(new BootstrapItem("[directorPublicIp]", openstackConfig.getDirectorPublicIp()));
			items.add(new BootstrapItem("[awsKey]", openstackConfig.getAccessKey()));
			items.add(new BootstrapItem("[secretAccessKey]", openstackConfig.getSecretAccessKey()));
			items.add(new BootstrapItem("[securGroupName]", openstackConfig.getDefaultSecurityGroups()));
			items.add(new BootstrapItem("[privateKey]", openstackConfig.getPrivateKeyPath()));
			items.add(new BootstrapItem("[region]", openstackConfig.getAvailabilityZone()));
			items.add(new BootstrapItem("[flavor]", openstackConfig.getInstanceType()));
		}
		return items;
	}

	public String getBootStrapSettingInfo() {
		String contents = "";
		File settingFile = null;
		String targetFileName = "aws-microbosh-merge.yml";
		try {
			settingFile = new File(iedaConfiguration.getDeploymentDir() + targetFileName);
			contents = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	public void setSpiffMerge(String stubFileName, String tempFileName) {
		File stubFile = null;
		File tempFile = null;
		String command = "";
		Runtime r = Runtime.getRuntime();
		String deployFileName = "aws-microbosh-merge.yml";

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			stubFile = new File(iedaConfiguration.getTempDir()+ stubFileName);
			tempFile = new File(iedaConfiguration.getTempDir()+tempFileName);
			
			if(stubFile.exists() && tempFile.exists()){
				command = iedaConfiguration.getScriptDir() + "merge-deploy.sh ";
				command += iedaConfiguration.getTempDir()+stubFileName + " ";
				command += iedaConfiguration.getTempDir()+tempFileName + " ";
				command += iedaConfiguration.getDeploymentDir()+deployFileName;
								
				Process process = r.exec(command);
				log.info("### PROCESS ::: " + process.toString());
				process.getInputStream();
				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				String streamLogs = "";
				while ((info = bufferedReader.readLine()) != null){
					streamLogs += info;
					log.info("=== Deployment File Merge \n"+ info );
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (Exception e) {
			}
		}
	}
	
	//aws-microbosh-delete.sh
	public void deleteDeploy(String fileName){
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		try{
			String command = iedaConfiguration.getScriptDir() + "aws-microbosh-delete.sh ";
			command += iedaConfiguration.getDeploymentDir()+fileName + " ";
					
			Process process = r.exec(command);
			log.info("### PROCESS ::: " + process.toString());
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			while ((info = bufferedReader.readLine()) != null){
				streamLogs += info;
				log.info("=== Delete Deploy File \n"+ info );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (Exception e) {
			}
		}
	}
	
}
