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
import org.openpaas.ieda.common.ReplaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABootstrapOpenstackService {

	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private IEDABootstrapOpenstackRepository openstackRepository;
	
	public IEDABootstrapOpenstackConfig saveOpenstackBoshInfoSave(IDEABootStrapInfoDto.OsBosh dto) {
		IEDABootstrapOpenstackConfig config = null;
		Date now = new Date();
		if(StringUtils.isEmpty(dto.getId())) {
			config = new IEDABootstrapOpenstackConfig();		
		}
		else {
			config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		}
		
		config.setBoshName(dto.getBoshName());
		config.setBoshUrl(dto.getBoshUrl());
		config.setBoshCpiUrl(dto.getBoshCpiUrl());
		config.setCloudPrivateKey(dto.getPrivateKeyPath());
		config.setCreatedDate(now);
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}

	public IEDABootstrapOpenstackConfig saveOpenstackInfoSave(IDEABootStrapInfoDto.OpenStack dto) {
		IEDABootstrapOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		config.setPrivateStaticIp(dto.getPrivateStaticIp());
		config.setPublicStaticIp(dto.getPublicStaticIp());
		config.setDirectorName(dto.getDirectorName());
		config.setAuthUrl(dto.getAuthUrl());
		config.setTenant(dto.getTenant());
		config.setUserName(dto.getUserName());
		config.setApiKey(dto.getApiKey());
		config.setDefaultKeyName(dto.getDefaultKeyName());
		config.setDefaultSecurityGroup(dto.getDefaultSecurityGroups());
		config.setNtp(dto.getNtp());
		Date now = new Date();
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}

	public IEDABootstrapOpenstackConfig saveOpenstackNetworkInfoSave(IDEABootStrapInfoDto.OsNetwork dto) {
		IEDABootstrapOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		config.setSubnetRange(dto.getSubnetRange());
		config.setSubnetGateway(dto.getSubnetGateway());
		config.setSubnetDns(dto.getSubnetDns());
		config.setCloudNetId(dto.getCloudNetId());
		Date now = new Date();
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}

	public IEDABootstrapOpenstackConfig saveOpenstackResourcesInfoSave(IDEABootStrapInfoDto.OsResource dto) {
		IEDABootstrapOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
		config.setStemcellUrl(dto.getStemcellUrl());
		config.setEnvPassword(dto.getEnvPassword());
		config.setCloudInstanceType(dto.getCloudInstanceType());
		Date now = new Date();
		config.setUpdatedDate(now);
		return openstackRepository.save(config);
	}
	

	public void downloadSettingFile(Integer bootstrapId) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/bosh-init-aws-template.yml");
		File sampleDeploy;
		//log.info("==== ::: " + classPath.toString());
		// GET BootStrap Info(DB 정보)
		IEDABootstrapOpenstackConfig config = openstackRepository.findOne(bootstrapId);
		String content = "";
		String tempContent = "";
		String targetFileName = "bosh-init-aws-micro-input-tample.yml";
		FileOutputStream fos = null;
		try {
			sampleDeploy = new File(classPath.toURI());//resource.getFile();
			content = IOUtils.toString(new FileInputStream(sampleDeploy), "UTF-8");
			List<ReplaceItem> bootstrapItems = makeBootstrapItems(config);
			tempContent = content;
			for (ReplaceItem item : bootstrapItems) {
				tempContent = tempContent.replace(item.getTargetItem(), item.getSourceItem());
			}

			log.info("*******************************************************");
			log.info("\n"+tempContent+"\n");
			log.info("*******************************************************");
			
			IOUtils.write(content, new FileOutputStream(iedaConfiguration.getTempDir() + System.getProperty("file.separator") + sampleDeploy.getName()), "UTF-8");
			IOUtils.write(tempContent, new FileOutputStream(iedaConfiguration.getTempDir() + System.getProperty("file.separator") + targetFileName), "UTF-8");
			
			setSiffMerge(sampleDeploy.getName(), targetFileName);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public List<ReplaceItem> makeBootstrapItems(IEDABootstrapOpenstackConfig config) {
		List<ReplaceItem> items = new ArrayList<ReplaceItem>();
//		items.add(new BootstrapItem("[stemcell]", config.getStemcellName()));
//		items.add(new BootstrapItem("[microboshPw]", config.getMicroBoshPw()));
//		items.add(new BootstrapItem("[subnetRange]", config.getSubnetRange()));
//		items.add(new BootstrapItem("[dns]", config.getDns()));
//		items.add(new BootstrapItem("[subnetId]", config.getSubnetId()));
//		items.add(new BootstrapItem("[gateway]", config.getGateway()));
//		items.add(new BootstrapItem("[directorPrivateIp]", config.getDirectorPrivateIp()));
//		items.add(new BootstrapItem("[directorPublicIp]", config.getDirectorPublicIp()));
//		items.add(new BootstrapItem("[awsKey]", config.getAccessKey()));
//		items.add(new BootstrapItem("[secretAccessKey]", config.getSecretAccessKey()));
//		items.add(new BootstrapItem("[securGroupName]", config.getDefaultSecurityGroups()));
//		items.add(new BootstrapItem("[privateKey]", iedaConfiguration.getKeyPathDir()+ System.getProperty("file.separator") +config.getPrivateKeyPath()));
		return items;
	}

	public String getBootStrapSettingInfo() {
		String contents = "";
		File settingFile = null;
		String targetFileName = "bosh-init-aws-micro-input-tample.yml";
		try {
			settingFile = new File(iedaConfiguration.getTempDir()+ System.getProperty("file.separator")  + targetFileName);
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
			sampleFile = new File(iedaConfiguration.getTempDir()+ System.getProperty("file.separator") + sampleFileName);
			tempFile = new File(iedaConfiguration.getTempDir()+ System.getProperty("file.separator") +tempFileName);
			
			if(sampleFile.exists() && tempFile.exists()){
				String deployFileName = "bosh-init-aws-micro-input-deployment.yml";
				command = iedaConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "merge-deploy.sh ";
				command += iedaConfiguration.getTempDir()+ System.getProperty("file.separator") +sampleFileName + " ";
				command += iedaConfiguration.getTempDir()+ System.getProperty("file.separator") +tempFileName + " ";
				command += iedaConfiguration.getDeploymentDir()+ System.getProperty("file.separator") +deployFileName;
								
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

	public IEDABootstrapOpenstackConfig getOpenstackInfo(int id) {
		IEDABootstrapOpenstackConfig config =  null;
		try{
			config = openstackRepository.findOne(id);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"해당하는 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return config;
	}
}
