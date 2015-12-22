package org.openpaas.ieda.web.config.bootstrap;

import java.io.BufferedOutputStream;
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
import java.util.Iterator;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
			for(IEDABootstrapAwsConfig awsConfig :awsConfigsList){
				BootstrapListDto dto = new BootstrapListDto();
				dto.setRecid(recid++);
				dto.setId(awsConfig.getId());
				dto.setIaas("AWS");
				dto.setCreatedDate(awsConfig.getCreatedDate());
				dto.setUpdatedDate(awsConfig.getUpdatedDate());
				dto.setDirectorPrivateIp(awsConfig.getDirectorPrivateIp());
				dto.setDirectorPublicIp(awsConfig.getDirectorPublicIp());
				listDtos.add(dto);
			}
		}
		
		if(openstackConfigsList.size() > 0){
			for(IEDABootstrapOpenstackConfig openstackConfig :openstackConfigsList){
				BootstrapListDto dto = new BootstrapListDto();
				dto.setRecid(recid++);
				dto.setId(openstackConfig.getId());
				dto.setIaas("OPENSTACK");
				dto.setCreatedDate(openstackConfig.getCreatedDate());
				dto.setUpdatedDate(openstackConfig.getUpdatedDate());
				dto.setDirectorPrivateIp(openstackConfig.getPrivateStaticIp());
				dto.setDirectorPublicIp(openstackConfig.getPublicStaticIp());
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
			
			List<BootstrapItem> bootstrapItems = makeBootstrapItems(id, iaas);
			for (BootstrapItem item : bootstrapItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubDeploy.getName()), "UTF-8");
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName), "UTF-8");
			deplymentFileName = setSpiffMerge(iaas, id, stubDeploy.getName(), settingFileName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return deplymentFileName;
	}

	public List<BootstrapItem> makeBootstrapItems(Integer id, String iaas) {
		
		List<BootstrapItem> items = new ArrayList<BootstrapItem>();
		
		if(iaas == "AWS"){
			IEDABootstrapAwsConfig  awsConfig = awsRepository.findOne(id);
			items.add(new BootstrapItem("[stemcell]", LocalDirectoryConfiguration.getStemcellDir() + System.getProperty("file.separator") + awsConfig.getStemcellName()));
			items.add(new BootstrapItem("[microboshPw]", awsConfig.getMicroBoshPw()));
			items.add(new BootstrapItem("[subnetRange]", awsConfig.getSubnetRange()));
			items.add(new BootstrapItem("[dns]", awsConfig.getDns()));
			items.add(new BootstrapItem("[subnetId]", awsConfig.getSubnetId()));
			items.add(new BootstrapItem("[gateway]", awsConfig.getGateway()));
			items.add(new BootstrapItem("[directorPrivateIp]", awsConfig.getDirectorPrivateIp()));
			items.add(new BootstrapItem("[directorPublicIp]", awsConfig.getDirectorPublicIp()));
			items.add(new BootstrapItem("[awsKey]", awsConfig.getAccessKey()));
			items.add(new BootstrapItem("[secretAccessKey]", awsConfig.getSecretAccessKey()));
			items.add(new BootstrapItem("[defaultSecurityGroups]", awsConfig.getDefaultSecurityGroups()));
			items.add(new BootstrapItem("[privateKey]"
					, System.getProperty("user.home") + System.getProperty("file.separator") + ".ssh"+ System.getProperty("file.separator") +awsConfig.getPrivateKeyPath()));
			items.add(new BootstrapItem("[region]", awsConfig.getAvailabilityZone()));
			items.add(new BootstrapItem("[flavor]", awsConfig.getInstanceType()));
			items.add(new BootstrapItem("[availabilityZone]", awsConfig.getAvailabilityZone()));
			items.add(new BootstrapItem("[ntp]", awsConfig.getNtp()));
		}
		else{
			IEDABootstrapOpenstackConfig openstackConfig = openstackRepository.findOne(id);
			items.add(new BootstrapItem("[boshName]", openstackConfig.getBoshName()));
			items.add(new BootstrapItem("[boshUrl]", openstackConfig.getBoshUrl()));
			items.add(new BootstrapItem("[boshCpiUrl]", openstackConfig.getBoshCpiUrl()));
			items.add(new BootstrapItem("[cloudPrivateKey]", openstackConfig.getCloudPrivateKey()));
			
			items.add(new BootstrapItem("[privateStaticIp]", openstackConfig.getPrivateStaticIp()));
			items.add(new BootstrapItem("[publicStaticIp]", openstackConfig.getPublicStaticIp()));
			items.add(new BootstrapItem("[directorName]", openstackConfig.getDirectorName()));
			items.add(new BootstrapItem("[boshUrl]", openstackConfig.getBoshUrl()));
			items.add(new BootstrapItem("[authUrl]", openstackConfig.getAuthUrl()));
			items.add(new BootstrapItem("[tenant]", openstackConfig.getTenant()));
			items.add(new BootstrapItem("[userName]", openstackConfig.getUserName()));
			items.add(new BootstrapItem("[apiKey]", openstackConfig.getApiKey()));
			items.add(new BootstrapItem("[defaultKeyName]", openstackConfig.getDefaultKeyName()));
			items.add(new BootstrapItem("[defaultSecurityGroup]", openstackConfig.getDefaultSecurityGroup()));
			items.add(new BootstrapItem("[ntp]", openstackConfig.getNtp()));
			
			items.add(new BootstrapItem("[subnetRange]", openstackConfig.getSubnetRange()));
			items.add(new BootstrapItem("[subnetGateway]", openstackConfig.getSubnetGateway()));
			items.add(new BootstrapItem("[subnetDns]", openstackConfig.getSubnetDns()));
			items.add(new BootstrapItem("[cloudNetId]", openstackConfig.getCloudNetId()));
			
			items.add(new BootstrapItem("[stemcellUrl]", openstackConfig.getStemcellUrl()));
			items.add(new BootstrapItem("[envPassword]", openstackConfig.getEnvPassword()));
			items.add(new BootstrapItem("[cloudInstanceType]", openstackConfig.getCloudInstanceType()));
			
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
		File stubFile = null;
		File settingFile = null;
		String command = "";
		Runtime r = Runtime.getRuntime();
		String deploymentFileName = "";

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			stubFile = new File(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubFileName);
			settingFile = new File(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName);
			
			deploymentFileName =  (iaas == "AWS") ? "aws-microbosh-merge-"+id+".yml"
					:"openstack-microbosh-merge-"+id+".yml";
			
			if(stubFile.exists() && settingFile.exists()){
				command = LocalDirectoryConfiguration.getScriptDir() + System.getProperty("file.separator") + "merge-deploy.sh ";
				command += LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubFileName + " ";
				command += LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName + " ";
				command += LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
								
				Process process = r.exec(command);
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
			command += LocalDirectoryConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "microbosh-deploy.sh ";
			command += LocalDirectoryConfiguration.getDeploymentDir()+ System.getProperty("file.separator")  + deployFileName ;
					
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

	public void uploadKeyPath(MultipartHttpServletRequest request) {
		Iterator<String> itr =  request.getFileNames();
		File keyPathFile = new File(PRIVATE_KEY_PATH);
		if (!keyPathFile.isDirectory()){
			keyPathFile.mkdir();
		}
			
        if(itr.hasNext()) {
            MultipartFile mpf = request.getFile(itr.next());
            try {
                byte[] bytes = mpf.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(PRIVATE_KEY_PATH + mpf.getOriginalFilename())));
                stream.write(bytes);
                stream.close();
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();
            }
        } 
		
	}
	
}