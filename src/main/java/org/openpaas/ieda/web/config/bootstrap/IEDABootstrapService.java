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
import org.apache.commons.lang.RandomStringUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.ReplaceItem;
import org.openpaas.ieda.web.common.Sha512Crypt;
import org.openpaas.ieda.web.config.bootstrap.BootStrapDto.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
				
				dto.setDeployStatus(config.getDeployStatus());
				dto.setDeploymentName(config.getDeploymentName());
				dto.setDirectorName(config.getDirectorName());
				dto.setIaas("AWS");
				dto.setBoshRelease(config.getBoshRelease());
				dto.setBoshCpiRelease(config.getBoshCpiRelease());
				dto.setSubnetId(config.getSubnetId());
				dto.setSubnetRange(config.getSubnetRange());
				dto.setPublicStaticIp(config.getPublicStaticIp());
				dto.setPrivateStaticIp(config.getPrivateStaticIp());
				dto.setSubnetGateway(config.getSubnetGateway());
				dto.setSubnetDns(config.getSubnetDns());
				dto.setNtp(config.getNtp());
				dto.setStemcell(config.getStemcell());
				dto.setInstanceType(config.getCloudInstanceType());
				dto.setBoshPassword(config.getBoshPassword());
				dto.setDeploymentFile(config.getDeploymentFile());
				dto.setDeployLog(config.getDeployLog());
				
				dto.setCreatedDate(config.getCreatedDate());
				dto.setUpdatedDate(config.getUpdatedDate());
				
				listDtos.add(dto);
			}
		}
		
		if(openstackConfigsList.size() > 0){
			for(IEDABootstrapOpenstackConfig config :openstackConfigsList){
				BootstrapListDto dto = new BootstrapListDto();
				dto.setRecid(recid++);
				dto.setId(config.getId());
				
				dto.setDeployStatus(config.getDeployStatus());
				dto.setDeploymentName(config.getDeploymentName());
				dto.setDirectorName(config.getDirectorName());
				dto.setIaas("OPENSTACK");
				dto.setBoshRelease(config.getBoshRelease());
				dto.setBoshCpiRelease(config.getBoshCpiRelease());
				dto.setSubnetId(config.getSubnetId());
				dto.setSubnetRange(config.getSubnetRange());
				dto.setPublicStaticIp(config.getPublicStaticIp());
				dto.setPrivateStaticIp(config.getPrivateStaticIp());
				dto.setSubnetGateway(config.getSubnetGateway());
				dto.setSubnetDns(config.getSubnetDns());
				dto.setNtp(config.getNtp());
				dto.setStemcell(config.getStemcell());
				dto.setInstanceType(config.getCloudInstanceType());
				dto.setBoshPassword(config.getBoshPassword());
				dto.setDeploymentFile(config.getDeploymentFile());
				dto.setDeployLog(config.getDeployLog());
				
				dto.setCreatedDate(config.getCreatedDate());
				dto.setUpdatedDate(config.getUpdatedDate());

				
				listDtos.add(dto);
			}
		}
		return listDtos;
	}

	public String createSettingFile(Integer id, String iaas) {
		URL classPath = null;
		URL stubPath = null;
		String settingFileName = null;
		File settingFile = null;
		File stubFile = null;
		
		String content = "";
		String stubContent = "";
		String deplymentFileName = ""; 
		
		// 파일 가져오기
		if("AWS".equals(iaas.toUpperCase())){
			classPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-microbosh-setting.yml");
			stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/aws-microbosh-stub.yml");
			settingFileName = "aws-microbosh-setting-"+id+".yml";
		}
		else if("OPENSTACK".equals(iaas.toUpperCase())){
			classPath = this.getClass().getClassLoader().getResource("static/deploy_template/openstack-microbosh-setting.yml");
			stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/openstack-microbosh-stub.yml");
			settingFileName = "openstack-microbosh-setting-"+id+".yml";
		}
		
		try {
			settingFile = new File(classPath.toURI());//resource.getFile();
			stubFile = new File(stubPath.toURI());
			content = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
			stubContent = IOUtils.toString(new FileInputStream(stubFile), "UTF-8");
			
			List<ReplaceItem> ReplaceItems = makeReplaceItems(id, iaas);
			for (ReplaceItem item : ReplaceItems) {
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(LocalDirectoryConfiguration.getTempDir()  + System.getProperty("file.separator") + stubFile.getName()), "UTF-8");
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName), "UTF-8");
			deplymentFileName = setSpiffMerge(iaas, id, stubFile.getName(), settingFileName);
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
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(awsConfig.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		}
		else{
			IEDABootstrapOpenstackConfig openstackConfig = openstackRepository.findOne(id);
			items.add(new ReplaceItem("[authUrl]", openstackConfig.getAuthUrl()));
			items.add(new ReplaceItem("[tenant]", openstackConfig.getTenant()));
			items.add(new ReplaceItem("[userName]", openstackConfig.getUserName()));
			items.add(new ReplaceItem("[apiKey]", openstackConfig.getApiKey()));
			items.add(new ReplaceItem("[defaultSecurityGroup]", openstackConfig.getDefaultSecurityGroups()));
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
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(openstackConfig.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
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
		String deploymentFileName = iaas.toLowerCase() +"-microbosh-"+id+".yml";		
		String templateFile = LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubFileName;
		String parameterFile = LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName;
		String targetFile= LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + deploymentFileName;
		
		File stubFile = null;
		File settingFile = null;
		String command = "";
		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			stubFile = new File(templateFile);
			settingFile = new File(parameterFile);
			
			if(stubFile.exists() && settingFile.exists()){
				command = "spiff merge " + templateFile + " " + parameterFile;
				
				Process process = r.exec(command);

				inputStream = process.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				String info = null;
				String deloymentContent = "";
				while ((info = bufferedReader.readLine()) != null){
					deloymentContent += info + "\n";
				}
				
				IOUtils.write(deloymentContent, new FileOutputStream(targetFile), "UTF-8");
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

	public void deleteBootstrapInfoRecord(Delete dto) {
		try {
			if( "AWS".equals(dto.getIaas())){
				IEDABootstrapAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
				awsRepository.delete(Integer.parseInt(dto.getId())); 
			}
			else{
				IEDABootstrapOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
				openstackRepository.delete(Integer.parseInt(dto.getId())); 
			}
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.bootstrap.delete.exception",
					"삭제중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
	}

}