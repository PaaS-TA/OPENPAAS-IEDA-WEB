package org.openpaas.ieda.web.config.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABootstrapService {

	@Autowired
	private IEDABootstrapRepository bootstrapRepository;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private IEDABootstrapAwsRepository bootstrapAwsRepository;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	public List<IEDABootstrapAwsConfig> listBootstrap() {
		List<IEDABootstrapAwsConfig> bootstrapAwsConfigsList = bootstrapAwsRepository.findAll();
		if (bootstrapAwsConfigsList.size() == 0) {
			throw new IEDACommonException("nocontent.bootstrap.exception", "BOOTSTRAP 정보가 존재하지 않습니다.",
					HttpStatus.NO_CONTENT);
		}

		return bootstrapAwsConfigsList;
	}

	public Integer setAwsInfos(IDEABootStrapInfoDto.Aws dto) {
		IEDABootstrapConfig config = new IEDABootstrapConfig();
		config.setIaas(dto.getIaas());
		Date now = new Date();
		config.setCreatedDate(now);
		config.setUpdatedDate(now);

		// IEDABoot
		config = bootstrapRepository.save(config);
		int bootstraSeq = config.getId();

		IEDABootstrapAwsConfig awsConfig = new IEDABootstrapAwsConfig();
		awsConfig.setBootstrapId(bootstraSeq);
		awsConfig.setAccessKey(dto.getAwsKey());
		awsConfig.setSecretAccessKey(dto.getAwsPw());
		awsConfig.setDefaultSecurityGroups(dto.getSecretGroupName());
		awsConfig.setDefaultKeyName(dto.getPrivateKeyName());
		awsConfig.setPrivateKeyPath(dto.getPrivateKeyPath());
		awsConfig.setCreatedDate(now);
		awsConfig.setUpdatedDate(now);
		awsConfig = bootstrapAwsRepository.save(awsConfig);
		
		int bootstrapAwsSeq = awsConfig.getId();
		return bootstrapAwsSeq;
	}

	public void setNetworkInfos(IDEABootStrapInfoDto.Network dto) {
		IEDABootstrapAwsConfig awsConfig = bootstrapAwsRepository.findById(Integer.parseInt(dto.getBootstrapId()));
		awsConfig.setSubnetRange(dto.getSubnetRange());
		awsConfig.setDns(dto.getDns());
		awsConfig.setSubnetId(dto.getSubnetId());
		awsConfig.setGateway(dto.getGateway());
		awsConfig.setDirectorPrivateIp(dto.getDirectorPrivateIp());
		awsConfig.setDirectorPublicIp(dto.getDirectorPublicIp());
		Date now = new Date();
		awsConfig.setUpdatedDate(now);
		bootstrapAwsRepository.save(awsConfig);
	}
 
	public void setReleaseInfos(IDEABootStrapInfoDto.Resources dto) {
		IEDABootstrapAwsConfig awsConfig = bootstrapAwsRepository.findById(Integer.parseInt(dto.getBootstrapId()));
		awsConfig.setStemcellName(dto.getTargetStemcell());
		awsConfig.setInstanceType(dto.getInstanceType());
		awsConfig.setAvailabilityZone(dto.getAvailabilityZone());
		awsConfig.setMicroBoshPw(dto.getMicroBoshPw());
		Date now = new Date();
		awsConfig.setUpdatedDate(now);
		bootstrapAwsRepository.save(awsConfig);
		downloadSettingFile(Integer.parseInt(dto.getBootstrapId()));
	}

	public void downloadSettingFile(Integer bootstrapId) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/bosh-init-aws-template.yml");
		File sampleDeploy;
		//log.info("==== ::: " + classPath.toString());
		// GET BootStrap Info(DB 정보)
		IEDABootstrapAwsConfig awsConfig = bootstrapAwsRepository.findById(bootstrapId);
		String content = "";
		String tempContent = "";
		String targetFileName = "bosh-init-aws-micro-input-tample.yml";
		FileOutputStream fos = null;
		try {
			sampleDeploy = new File(classPath.toURI());//resource.getFile();
			log.info("## FileName ::: "  + sampleDeploy.getName());
			content = IOUtils.toString(new FileInputStream(sampleDeploy), "UTF-8");
			List<BootstrapItem> bootstrapItems = makeBootstrapItems(awsConfig);
			log.info(":::SIZE::: "+bootstrapItems.size());
			tempContent = content;
			for (BootstrapItem item : bootstrapItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				tempContent = tempContent.replace(item.getTargetItem(), item.getSourceItem());
			}

			log.info("*******************************************************");
			log.info("\n"+tempContent+"\n");
			log.info("*******************************************************");
			
			IOUtils.write(content, new FileOutputStream(iedaConfiguration.getTempDir() + sampleDeploy.getName()), "UTF-8");
			IOUtils.write(tempContent, new FileOutputStream(iedaConfiguration.getTempDir() + targetFileName), "UTF-8");
			
			setSiffMerge(sampleDeploy.getName(), targetFileName);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public List<BootstrapItem> makeBootstrapItems(IEDABootstrapAwsConfig awsConfig) {
		List<BootstrapItem> items = new ArrayList<BootstrapItem>();
		items.add(new BootstrapItem("[stemcell]", awsConfig.getStemcellName()));
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
		return items;
	}

	public String getBootStrapSettingInfo() {
		String contents = "";
		File settingFile = null;
		String targetFileName = "bosh-init-aws-micro-input-tample.yml";
		try {
			settingFile = new File(iedaConfiguration.getTempDir() + targetFileName);
			contents = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	public void setSiffMerge(String sampleFileName, String tempFileName) {
		File sampleFile = null;
		File tempFile = null;
		String command = "";
		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			sampleFile = new File(iedaConfiguration.getTempDir()+ sampleFileName);
			tempFile = new File(iedaConfiguration.getTempDir()+tempFileName);
			
			if(sampleFile.exists() && tempFile.exists()){
				String deployFileName = "bosh-init-aws-micro-input-deployment.yml";
				command = iedaConfiguration.getScriptDir() + "merge-deploy.sh ";
				command += iedaConfiguration.getTempDir()+sampleFileName + " ";
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
}
