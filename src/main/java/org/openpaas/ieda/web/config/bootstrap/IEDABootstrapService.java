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

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.ReplaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABootstrapService {

	@Autowired
	private IEDABootstrapAwsRepository awsRepository;
	
	@Autowired
	private IEDABootstrapOpenstackRepository openstackRepository;
	
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	final private String PRIVATE_KEY_PATH = System.getProperty("user.home") + System.getProperty("file.separator") + ".ssh" + System.getProperty("file.separator");
	
	public List<BootstrapListDto> listBootstrap() {
		List<IEDABootstrapAwsConfig> awsConfigsList = awsRepository.findAll();
		List<IEDABootstrapOpenstackConfig> openstackConfigsList = openstackRepository.findAll();
		List<BootstrapListDto> listDtos = new ArrayList<>();
		int recid =0;
		if(awsConfigsList.size() > 0){
			for(IEDABootstrapAwsConfig config :awsConfigsList){
				BootstrapListDto dto = new BootstrapListDto();
				dto.setRecid(recid++);
				dto.setId(config.getId());
				dto.setIaas("AWS");
				dto.setCreatedDate(config.getCreatedDate());
				dto.setUpdatedDate(config.getUpdatedDate());
				
				dto.setDeploymentName(config.getDeploymentName());;
				dto.setDirectorName(config.getDirectorName());;
				dto.setBoshRelease(config.getBoshRelease());;
				dto.setBoshCpiRelease(config.getBoshCpiRelease());;
				dto.setSubnetId(config.getSubnetId());;
				dto.setPrivateStaticIp(config.getPrivateStaticIp());
				dto.setPublicStaticIp(config.getPublicStaticIp());;
				
				listDtos.add(dto);
			}
		}
		
		if(openstackConfigsList.size() > 0){
			for(IEDABootstrapOpenstackConfig config :openstackConfigsList){
				BootstrapListDto dto = new BootstrapListDto();
				dto.setRecid(recid++);
				dto.setId(config.getId());
				dto.setIaas("OPENSTACK");
				dto.setCreatedDate(config.getCreatedDate());
				dto.setUpdatedDate(config.getUpdatedDate());
				
				dto.setDeploymentName(config.getDeploymentName());;
				dto.setDirectorName(config.getDirectorName());;
				dto.setBoshRelease(config.getBoshRelease());;
				dto.setBoshCpiRelease(config.getBoshCpiRelease());;
				dto.setSubnetId(config.getSubnetId());;
				dto.setPrivateStaticIp(config.getPrivateStaticIp());
				dto.setPublicStaticIp(config.getPublicStaticIp());;
				
				listDtos.add(dto);
			}
		}
		return listDtos;
	}

	public String createSettingFile(Integer id, String iaas) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-microbosh-setting.yml");
		URL stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-microbosh-stub.yml");
		File settingFile;
		File stubDeploy;
		
		String content = "";
		String stubContent = "";
		String settingFileName = (iaas == "AWS") ? "aws-microbosh-setting-"+id+".yml" 
									: "openstack-microbosh-setting-"+id+".yml";
		String deplymentFileName = ""; 
		
		try {
			settingFile = new File(classPath.toURI());//resource.getFile();
			stubDeploy = new File(stubPath.toURI());
			content = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
			stubContent = IOUtils.toString(new FileInputStream(stubDeploy), "UTF-8");
			
			List<ReplaceItem> ReplaceItems = makeReplaceItems(id, iaas);
			for (ReplaceItem item : ReplaceItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(LocalDirectoryConfiguration.getTempDir()  + System.getProperty("file.separator") + stubDeploy.getName()), "UTF-8");
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName), "UTF-8");
			deplymentFileName = setSpiffMerge(iaas, id, stubDeploy.getName(), settingFileName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return deplymentFileName;
	}

	public List<ReplaceItem> makeReplaceItems(Integer id, String iaas) {
		
		List<ReplaceItem> items = new ArrayList<ReplaceItem>();
		
		if(iaas == "AWS"){
			IEDABootstrapAwsConfig  awsConfig = awsRepository.findOne(id);
			items.add(new ReplaceItem("[accessKeyId]", awsConfig.getAccessKeyId()));
			items.add(new ReplaceItem("[secretAccessId]", awsConfig.getSecretAccessId()));
			items.add(new ReplaceItem("[defaultSecurityGroups]", awsConfig.getDefaultSecurityGroups()));
			items.add(new ReplaceItem("[region]", awsConfig.getRegion()));
			items.add(new ReplaceItem("[availabilityZone]", awsConfig.getAvailabilityZone()));
			items.add(new ReplaceItem("[privateKeyName]", awsConfig.getPrivateKeyName()));
			items.add(new ReplaceItem("[privateKeyPath]", System.getProperty("user.home") + System.getProperty("file.separator")
										+ ".ssh"+ System.getProperty("file.separator") + awsConfig.getPrivateKeyPath()));
			
			items.add(new ReplaceItem("[deploymentName]", awsConfig.getDeploymentName()));
			items.add(new ReplaceItem("[directorName]", awsConfig.getDirectorName()));
			items.add(new ReplaceItem("[boshRelease]", LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator") + awsConfig.getBoshRelease()));
			items.add(new ReplaceItem("[boshCpiRelease]", LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator") + awsConfig.getBoshCpiRelease()));
			
			items.add(new ReplaceItem("[subnetId]", awsConfig.getSubnetId()));
			items.add(new ReplaceItem("[privateStaticIp]", awsConfig.getPrivateStaticIp()));
			items.add(new ReplaceItem("[publicStaticIp]", awsConfig.getPublicStaticIp()));
			items.add(new ReplaceItem("[subnetRange]", awsConfig.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", awsConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", awsConfig.getSubnetDns()));
			items.add(new ReplaceItem("[ntp]", awsConfig.getNtp()));
			
			items.add(new ReplaceItem("[stemcell]", LocalDirectoryConfiguration.getStemcellDir() + System.getProperty("file.separator") + awsConfig.getStemcell()));
			items.add(new ReplaceItem("[cloudInstanceType]", awsConfig.getCloudInstanceType()));
			items.add(new ReplaceItem("[boshPassword]", awsConfig.getBoshPassword()));
//			
//			items.add(new ReplaceItem("[privateKey]"
//					, System.getProperty("user.home") + System.getProperty("file.separator") + ".ssh"+ System.getProperty("file.separator") +awsConfig.getPrivateKeyPath()));
		}
		else{
			IEDABootstrapOpenstackConfig openstackConfig = openstackRepository.findOne(id);
			items.add(new ReplaceItem("[authUrl]", openstackConfig.getAuthUrl()));
			items.add(new ReplaceItem("[tenant]", openstackConfig.getTenant()));
			items.add(new ReplaceItem("[userName]", openstackConfig.getUserName()));
			items.add(new ReplaceItem("[apiKey]", openstackConfig.getApiKey()));
			items.add(new ReplaceItem("[defaultSecurityGroup]", openstackConfig.getDefaultSecurityGroup()));
			items.add(new ReplaceItem("[privateKeyName]", openstackConfig.getPrivateKeyName()));
			items.add(new ReplaceItem("[privateKeyPath]", System.getProperty("user.home") + System.getProperty("file.separator")
												+ ".ssh"+ System.getProperty("file.separator") +openstackConfig.getPrivateKeyPath()));
			
			items.add(new ReplaceItem("[deploymentName]", openstackConfig.getDeploymentName()));
			items.add(new ReplaceItem("[directorName]", openstackConfig.getDirectorName()));
			items.add(new ReplaceItem("[boshRelease]", LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator") + openstackConfig.getBoshRelease()));
			items.add(new ReplaceItem("[boshCpiRelease]", LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator") + openstackConfig.getBoshCpiRelease()));
			
			items.add(new ReplaceItem("[subnetId]", openstackConfig.getSubnetId()));
			items.add(new ReplaceItem("[privateStaticIp]", openstackConfig.getPrivateStaticIp()));
			items.add(new ReplaceItem("[publicStaticIp]", openstackConfig.getPublicStaticIp()));
			items.add(new ReplaceItem("[subnetRange]", openstackConfig.getSubnetRange() ));
			items.add(new ReplaceItem("[subnetGateway]", openstackConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", openstackConfig.getSubnetDns()));
			items.add(new ReplaceItem("[ntp]", openstackConfig.getNtp()));
			
			items.add(new ReplaceItem("[stemcell]", LocalDirectoryConfiguration.getStemcellDir() + System.getProperty("file.separator") + openstackConfig.getStemcell()));
			items.add(new ReplaceItem("[cloudInstanceType]", openstackConfig.getCloudInstanceType()));
			items.add(new ReplaceItem("[boshPassword]", openstackConfig.getBoshPassword()));
			
		}
		return items;
	}

	public String getBootStrapSettingInfo(String deploymentFile) {
		String contents = "";
		File settingFile = null;
		try {
			settingFile = new File(LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFile);
			contents = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	public String setSpiffMerge(String iaas, Integer id, String stubFileName, String settingFileName) {
		String deploymentFileName = iaas.toLowerCase() +"-microbosh-merge-"+id+".yml";		
		String templateFile = LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubFileName;
		String parameterFile = LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName;
		String deploymentPath= LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
		
		File stubFile = null;
		File settingFile = null;
		String command = "";
		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			stubFile = new File(templateFile);
			settingFile = new File(parameterFile);
			
			deploymentFileName =  (iaas == "AWS") ? "aws-microbosh-merge-"+id+".yml"
					:"openstack-microbosh-merge-"+id+".yml";
			
			if(stubFile.exists() && settingFile.exists()){
				command = "spiff merge " + templateFile + " " + parameterFile;;
				
				Process process = r.exec(command);

				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				String deloymentContent = "";
				while ((info = bufferedReader.readLine()) != null){
					deloymentContent += info + "\n";
					log.info("=== Deployment File Merge \n"+ info );
				}
				
				IOUtils.write(deloymentContent, new FileOutputStream(deploymentPath), "UTF-8");
			}
			else{
				throw new IEDACommonException("illigalArgument.bootstrap.exception",
						"Merge할 File이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
		return deploymentFileName;
	}
	
	//aws-microbosh-delete.sh
	public void deleteDeploy(String fileName){
		
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		try{
			String command = LocalDirectoryConfiguration.getScriptDir() + System.getProperty("file.separator") + "aws-microbosh-delete.sh ";
			command += LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + fileName + " ";
					
			Process process = r.exec(command);
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			while ((info = bufferedReader.readLine()) != null){
				streamLogs += info;
				log.info(info);
				messagingTemplate.convertAndSend("/bootstrap/bootstrapDelete", info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
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
			messagingTemplate.convertAndSend("/bootstrap/bootstrapDelete", "complete");
		}
	}
	
	@Async
	public void installBootstrap(String deployFileName){
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		String command = "";
		try{
			command += LocalDirectoryConfiguration.getScriptDir() + System.getProperty("file.separator")  + "aws-microbosh-deploy.sh ";
			command += LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator")  + deployFileName ;
					
			Process process = r.exec(command);
			log.info("### PROCESS ::: " + process.toString());
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			while ((info = bufferedReader.readLine()) != null){
				streamLogs += info;
				messagingTemplate.convertAndSend("/bootstrap/bootstrapInstall", info);
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
				if (bufferedReader != null){
					bufferedReader.close();
					messagingTemplate.convertAndSend("/bootstrap/bootstrapInstall", "complete");
				}
			} catch (Exception e) {
			}
		}
	}


	public List<String> getKeyPathFileList() {
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("KeyFile only","pem");
		
		File keyPathFile = new File(PRIVATE_KEY_PATH);
		if ( !keyPathFile.isDirectory() ) return null;
		
		List<String> localFiles = null;
		
		File[] listFiles = keyPathFile.listFiles();
		for (File file : listFiles) {
			
			if(!file.getName().endsWith(".pem") && !file.getName().endsWith(".PEM"))
				continue;
			
			if ( localFiles == null )
				localFiles = new ArrayList<String>();

			localFiles.add(file.getName());
		}
		
		return localFiles;
	}

}