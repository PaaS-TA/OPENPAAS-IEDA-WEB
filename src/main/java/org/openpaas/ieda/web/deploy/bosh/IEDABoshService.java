package org.openpaas.ieda.web.deploy.bosh;

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
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.api.ReleaseInfo;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.common.ReplaceItem;
import org.openpaas.ieda.web.config.bootstrap.IEDABootstrapAwsConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.stemcell.StemcellContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABoshService {

	@Autowired
	private IEDABoshAwsRepository awsRepository;

	@Autowired
	private IEDABoshOpenstackRepository openstackRepository;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private IEDABoshAwsService awsService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public List<BoshInfo> getBoshList(){

		List<BoshInfo> boshList = new ArrayList();
		List<IEDABoshAwsConfig> boshAwsList = awsRepository.findAll();
		List<IEDABoshOpenstackConfig> boshOpenstackList = openstackRepository.findAll();
		if ( boshAwsList == null ) {
			throw new IEDACommonException("notfound.bosh.exception",
					"Bosh 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}

		int recid = 0;
		if( boshAwsList.size() >0 ){
			for(IEDABoshAwsConfig awsConfig : boshAwsList){
				BoshInfo boshInfo = new BoshInfo();
				boshInfo.setRecid(recid++);
				boshInfo.setId(awsConfig.getId());
				boshInfo.setIaas("AWS");
				boshInfo.setSubnetRange(awsConfig.getSubnetRange());
				boshInfo.setCreatedDate(awsConfig.getCreatedDate());
				boshList.add(boshInfo);
			}
		}

		if( boshOpenstackList.size() >0 ){
			for(IEDABoshOpenstackConfig openstackConfig : boshOpenstackList){
				BoshInfo boshInfo = new BoshInfo();
				boshInfo.setRecid(recid++);
				boshInfo.setId(openstackConfig.getId());
				boshInfo.setIaas("OPENSTACK");
				boshInfo.setSubnetRange(openstackConfig.getSubnetRange());
				boshInfo.setCreatedDate(openstackConfig.getCreatedDate());
				boshList.add(boshInfo);
			}
		}
		boshList.stream().sorted((BoshInfo o1, BoshInfo o2) -> o1.getCreatedDate().compareTo(o2.getCreatedDate()));
		return boshList;
	}


	public Boolean setSpiffMerge(String tempFilePath, String stubFilePath, String deplyFilePath){
		File stubFile = null;
		File tempFile = null;
		String command = "";
		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Boolean status = Boolean.FALSE;
		try {
			tempFile = new File(tempFilePath);
			stubFile = new File(stubFilePath);

			if(stubFile.exists() && tempFile.exists()){
				command = iedaConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "merge-deploy.sh ";
				command += stubFilePath + " ";
				command += tempFilePath + " ";
				command += deplyFilePath;

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
				status = Boolean.TRUE;
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
		return status;
	}

	public void deleteDeploy(String deploymentFile) {

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		try{
			String command = iedaConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "bosh-delete.sh ";
			command += iedaConfiguration.getDeploymentDir()+ System.getProperty("file.separator") +deploymentFile + " ";

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
				messagingTemplate.convertAndSend("/bosh/boshDelete", info);
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
			messagingTemplate.convertAndSend("/bosh/boshDelete", "complete");
		}

	}

	public void installBootstrap(String deployFileName) {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		String command = "";
		log.info("%%%% Deploy File Name : " + deployFileName);
		try{
			command += iedaConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "bosh-deploy.sh ";
			command += iedaConfiguration.getDeploymentDir()+ System.getProperty("file.separator")  + deployFileName ;

			Process process = r.exec(command);
			log.info("### PROCESS ::: " + process.toString());
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			while ((info = bufferedReader.readLine()) != null){
				streamLogs += info;
				log.info("=== InstallBootstrap Logs \n"+ info );
				messagingTemplate.convertAndSend("/bosh/boshInstall", info);
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
					messagingTemplate.convertAndSend("/bosh/boshInstall", "complete");
				}
			} catch (Exception e) {
			}
		}

	}

	public String createSettingFile(Integer id, String iaas) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-fullbosh-setting.yml");
		URL stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-fullbosh-stub.yml");
		
		File settingFile;
		File stubDeploy;

		String content = "";
		String stubContent = "";
		String settingFileName = iaas.toLowerCase()+"-fullbosh-setting-"+id+".yml";
		
		String deplymentFileName = ""; 
		
		try {
			settingFile = new File(classPath.toURI());//resource.getFile();
			stubDeploy = new File(stubPath.toURI());
			content = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
			stubContent = IOUtils.toString(new FileInputStream(stubDeploy), "UTF-8");

			List<ReplaceItem> bootstrapItems = makeBootstrapItems(id, iaas);
			for (ReplaceItem item : bootstrapItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(iedaConfiguration.getTempDir() + System.getProperty("file.separator") + stubDeploy.getName()), "UTF-8");
			IOUtils.write(content, new FileOutputStream(iedaConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName), "UTF-8");
			deplymentFileName = setSpiffMerge(iaas, id, stubDeploy.getName(), settingFileName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return deplymentFileName;
	}

	public List<ReplaceItem> makeBootstrapItems(Integer id, String iaas) {

		List<ReplaceItem> items = new ArrayList<ReplaceItem>();

		if(iaas == "AWS"){
			IEDABoshAwsConfig awsConfig = awsRepository.findOne(id);
//			items.add(new ReplaceItem("[stemcell]", iedaConfiguration.getStemcellDir() + System.getProperty("file.separator") + awsConfig.getStemcellName()));
			items.add(new ReplaceItem("[accessKeyId]", awsConfig.getAccessKeyId()));
			items.add(new ReplaceItem("[secretAccessKey]", awsConfig.getSecretAccessKey()));
			items.add(new ReplaceItem("[defaultKeyName]", awsConfig.getDefaultKeyName()));
			items.add(new ReplaceItem("[defaultSecurityGroups]", awsConfig.getDefaultSecurityGroups()));
			items.add(new ReplaceItem("[region]", awsConfig.getRegion()));
			items.add(new ReplaceItem("[privateKeyPath]", awsConfig.getPrivateKeyPath()));
			items.add(new ReplaceItem("[boshName]", awsConfig.getBoshName()));
			items.add(new ReplaceItem("[directorUuid]", awsConfig.getDirectorUuid()));
			items.add(new ReplaceItem("[publicStaticIp]", awsConfig.getPublicStaticIp()));
			
			items.add(new ReplaceItem("[releaseVersion]", awsConfig.getReleaseVersion()));
			items.add(new ReplaceItem("[subnetStatic]", awsConfig.getSubnetStatic()));
			items.add(new ReplaceItem("[subnetRange]", awsConfig.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", awsConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", awsConfig.getSubnetDns()));
			items.add(new ReplaceItem("[cloudSubnet]", awsConfig.getCloudSubnet()));
			items.add(new ReplaceItem("[cloudSecurityGroups]", awsConfig.getCloudSecurityGroups()));
			items.add(new ReplaceItem("[cloudInstanceType]", awsConfig.getCloudInstanceType()));
			items.add(new ReplaceItem("[stemcellName]", awsConfig.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", awsConfig.getStemcellVersion()));
			items.add(new ReplaceItem("[boshPassword]", awsConfig.getBoshPassword()));
			
		}
		else{
			IEDABoshOpenstackConfig openstackConfig = openstackRepository.findOne(id);
			items.add(new ReplaceItem("[boshName]", openstackConfig.getBoshName()));
			items.add(new ReplaceItem("[directorUuid]", openstackConfig.getDirectorUuid()));
			items.add(new ReplaceItem("[releaseVersion]", openstackConfig.getReleaseVersion()));
			items.add(new ReplaceItem("[cloudSecurityGroups]", openstackConfig.getCloudSecurityGroups()));
			items.add(new ReplaceItem("[cloudSubnet]", openstackConfig.getCloudSubnet()));
			items.add(new ReplaceItem("[subnetStatic]", openstackConfig.getSubnetStatic()));
			items.add(new ReplaceItem("[subnetRange]", openstackConfig.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", openstackConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", openstackConfig.getSubnetDns()));
			items.add(new ReplaceItem("[cloudNetId]", openstackConfig.getCloudNetId()));
			items.add(new ReplaceItem("[stemcellName]", openstackConfig.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", openstackConfig.getStemcellVersion()));
			items.add(new ReplaceItem("[cloudInstanceType]", openstackConfig.getCloudInstanceType()));
			items.add(new ReplaceItem("[boshPassword]", openstackConfig.getBoshPassword()));
			items.add(new ReplaceItem("[directorName]", openstackConfig.getDirectorName()));
			items.add(new ReplaceItem("[directorStaticIp]", openstackConfig.getDirectorStaticIp()));
			items.add(new ReplaceItem("[authUrl]", openstackConfig.getAuthUrl()));
			items.add(new ReplaceItem("[tenant]", openstackConfig.getTenant()));
			items.add(new ReplaceItem("[userName]", openstackConfig.getUserName()));
			items.add(new ReplaceItem("[apiKey]", openstackConfig.getApiKey()));
			items.add(new ReplaceItem("[defaultKeyName]", openstackConfig.getDefaultKeyName()));
			items.add(new ReplaceItem("[defaultSecurityGroups]", openstackConfig.getDefaultSecurityGroups()));
			items.add(new ReplaceItem("[ntp]", openstackConfig.getNtp()));
			items.add(new ReplaceItem("[privateKeyPath]", openstackConfig.getPrivateKeyPath()));
			
		}
		return items;
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
			stubFile = new File(iedaConfiguration.getTempDir() + System.getProperty("file.separator") + stubFileName);
			settingFile = new File(iedaConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName);

			deploymentFileName =  iaas.toLowerCase() +"-fullbosh-merge-"+id+".yml";
			
			if(stubFile.exists() && settingFile.exists()){
				command = iedaConfiguration.getScriptDir() + System.getProperty("file.separator") + "merge-deploy.sh ";
				command += iedaConfiguration.getTempDir() + System.getProperty("file.separator") + stubFileName + " ";
				command += iedaConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName + " ";
				command += iedaConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;

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
	
	public String getDeploymentInfos(String deploymentFile){
		String contents = "";
		File settingFile = null;
		try {
			settingFile = new File(iedaConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFile);
			contents = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}


	public void deleteBoshInfo(BoshParam.Delete dto) {
		String deploymentFileName = "";
		try{
			//awsConfig = awsRepository.findOne(id);
			if( "AWS".equals(dto.getIaas())){ 
				IEDABoshAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
				awsRepository.delete(Integer.parseInt(dto.getId()));
				deploymentFileName = config.getDeploymentFile();
			}
			else{
				IEDABoshOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
				openstackRepository.delete(Integer.parseInt(dto.getId()));
				deploymentFileName = config.getDeploymentFile();
			}
			if( StringUtils.isEmpty(deploymentFileName)) deleteDeploy(deploymentFileName);
		} catch (EntityNotFoundException e) {
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"삭제할 BOOTSTRAP이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.bootstrap.exception",
					"BOOTSTRAP 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
	}


	public List<ReleaseInfo> getLocalBoshList() {
		// IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();
		
//		// 디럭터의 CPI에 맞는 로컬 스템셀 목록만 출력
//		if ( defaultDirector.getDirectorCpi().toUpperCase().contains("AWS") ) filterString = "AWS";
//		if ( defaultDirector.getDirectorCpi().toUpperCase().contains("OPENSTACK") ) filterString = "OPENSTACK";
//
//		List<StemcellContent> returnList = null;
//		List<String> localStemcellList = stemcellManagementService.getLocalStemcellList();
//
//		if ( filterString != null && filterString.length() > 0 )
//			returnList = stemcellContentRepository.findByStemcellFileNameInOrderByStemcellVersionDesc(localStemcellList).stream()
//			.filter(t -> t.getIaas().equalsIgnoreCase(filterString))
//			.collect(Collectors.toList());
//		else
//			returnList = stemcellContentRepository.findByStemcellFileNameInOrderByStemcellVersionDesc(localStemcellList);
//
//		filterString = null;
//					
//		return returnList;
		return null;
	}
}
