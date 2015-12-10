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
public class IEDABootstrapOpenstackService {

	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private IEDABootstrapAwsRepository awsRepository;
	
	@Autowired
	private IEDABootstrapOpenstackRepository OpenstackRepository;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	public List<IEDABootstrapAwsConfig> listBootstrap() {
		List<IEDABootstrapAwsConfig> bootstrapAwsConfigsList = awsRepository.findAll();
		if (bootstrapAwsConfigsList.size() == 0) {
			throw new IEDACommonException("nocontent.bootstrap.exception", "BOOTSTRAP 정보가 존재하지 않습니다.",
					HttpStatus.NO_CONTENT);
		}

		return bootstrapAwsConfigsList;
	}

	public Integer saveAwsInfo(IDEABootStrapInfoDto.Aws dto){
		Date now = new Date();
		IEDABootstrapAwsConfig config = new IEDABootstrapAwsConfig();
		config.setAccessKey(dto.getAwsKey());
		config.setSecretAccessKey(dto.getAwsPw());
		config.setDefaultSecurityGroups(dto.getSecretGroupName());
		config.setDefaultKeyName(dto.getPrivateKeyName());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		config = awsRepository.save(config);
		return config.getId();
	}
	
	public Integer saveOpenstackInfo(IDEABootStrapInfoDto.Aws dto){
		Date now = new Date();
		IEDABootstrapOpenstackConfig config = new IEDABootstrapOpenstackConfig();
		config.setAccessKey(dto.getAwsKey());
		config.setSecretAccessKey(dto.getAwsPw());
		config.setDefaultSecurityGroups(dto.getSecretGroupName());
		config.setDefaultKeyName(dto.getPrivateKeyName());
		config.setPrivateKeyPath(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		config = OpenstackRepository.save(config);
		return config.getId();
	}

	public void saveAwsNetworkInfos(IDEABootStrapInfoDto.Network dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setSubnetRange(dto.getSubnetRange());
		config.setDns(dto.getDns());
		config.setSubnetId(dto.getSubnetId());
		config.setGateway(dto.getGateway());
		config.setDirectorPrivateIp(dto.getDirectorPrivateIp());
		config.setDirectorPublicIp(dto.getDirectorPublicIp());
		Date now = new Date();
		config.setUpdatedDate(now);
		awsRepository.save(config);
	}
	
	public void saveOpenStackNetworkInfos(IDEABootStrapInfoDto.Network dto) {
		IEDABootstrapOpenstackConfig config = OpenstackRepository.findById(Integer.parseInt(dto.getId()));
		config.setSubnetRange(dto.getSubnetRange());
		config.setDns(dto.getDns());
		config.setSubnetId(dto.getSubnetId());
		config.setGateway(dto.getGateway());
		config.setDirectorPrivateIp(dto.getDirectorPrivateIp());
		config.setDirectorPublicIp(dto.getDirectorPublicIp());
		Date now = new Date();
		config.setUpdatedDate(now);
		OpenstackRepository.save(config);
	}
 
	public void setReleaseInfos(IDEABootStrapInfoDto.Resources dto) {
		IEDABootstrapAwsConfig config = awsRepository.findById(Integer.parseInt(dto.getId()));
		config.setStemcellName(dto.getTargetStemcell());
		config.setInstanceType(dto.getInstanceType());
		config.setAvailabilityZone(dto.getAvailabilityZone());
		config.setMicroBoshPw(dto.getMicroBoshPw());
		Date now = new Date();
		config.setUpdatedDate(now);
		awsRepository.save(config);
		downloadSettingFile(Integer.parseInt(dto.getId()));
	}

	public void downloadSettingFile(Integer bootstrapId) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/bosh-init-aws-template.yml");
		File sampleDeploy;
		//log.info("==== ::: " + classPath.toString());
		// GET BootStrap Info(DB 정보)
		IEDABootstrapAwsConfig awsConfig = awsRepository.findById(bootstrapId);
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

	public List<BootstrapItem> makeBootstrapItems(IEDABootstrapAwsConfig config) {
		List<BootstrapItem> items = new ArrayList<BootstrapItem>();
		items.add(new BootstrapItem("[stemcell]", config.getStemcellName()));
		items.add(new BootstrapItem("[microboshPw]", config.getMicroBoshPw()));
		items.add(new BootstrapItem("[subnetRange]", config.getSubnetRange()));
		items.add(new BootstrapItem("[dns]", config.getDns()));
		items.add(new BootstrapItem("[subnetId]", config.getSubnetId()));
		items.add(new BootstrapItem("[gateway]", config.getGateway()));
		items.add(new BootstrapItem("[directorPrivateIp]", config.getDirectorPrivateIp()));
		items.add(new BootstrapItem("[directorPublicIp]", config.getDirectorPublicIp()));
		items.add(new BootstrapItem("[awsKey]", config.getAccessKey()));
		items.add(new BootstrapItem("[secretAccessKey]", config.getSecretAccessKey()));
		items.add(new BootstrapItem("[securGroupName]", config.getDefaultSecurityGroups()));
		items.add(new BootstrapItem("[privateKey]", iedaConfiguration.getKeyPathDir()+config.getPrivateKeyPath()));
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
