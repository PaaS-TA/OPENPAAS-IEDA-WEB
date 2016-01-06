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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.CommonUtils;
import org.openpaas.ieda.web.common.ReplaceItem;
import org.openpaas.ieda.web.common.Sha512Crypt;
import org.openpaas.ieda.web.information.deploy.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDABoshService {

	@Autowired
	private IEDABoshAwsRepository awsRepository;

	@Autowired
	private IEDABoshOpenstackRepository openstackRepository;
	
	@Autowired
	private DeploymentService deploymentService;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public List<BoshInfo> getBoshList(){
		
		List<BoshInfo> boshList = new ArrayList();
		List<IEDABoshAwsConfig> boshAwsList = awsRepository.findAll();
		List<IEDABoshOpenstackConfig> boshOpenstackList = openstackRepository.findAll();
		
		if ( boshAwsList == null && boshOpenstackList == null ) {
			throw new IEDACommonException("notfound.bosh.exception",
					"Bosh 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		//List<DeploymentInfo> deployedList = deploymentService.listDeployment();

		int recid = 0;
		if( boshAwsList.size() > 0 ){
			for(IEDABoshAwsConfig aws : boshAwsList){
				
/*				// 기본관리자 UUID와 다른 경우 목록에서 제외
				if ( !defaultDirector.getDirectorUuid().equals(aws.getDirectorUuid()) )
					continue;*/
				
				BoshInfo boshInfo = new BoshInfo();
				boshInfo.setRecid(recid++);
				boshInfo.setId(aws.getId());
				boshInfo.setDeploymentName(aws.getDeploymentName());
				boshInfo.setIaas("AWS");
				boshInfo.setDirectorUuid(aws.getDirectorUuid());
				boshInfo.setReleaseVersion(aws.getReleaseVersion());
				boshInfo.setStemcell(aws.getStemcellName() + "/" + aws.getStemcellVersion());
				boshInfo.setPublicIp(aws.getPublicStaticIp());
				boshInfo.setSubnetRange(aws.getSubnetRange());
				boshInfo.setGateway(aws.getSubnetGateway());
				boshInfo.setDns(aws.getSubnetDns());
				boshInfo.setDeployStatus(aws.getDeployStatus());
				
/* 				if ( deployedList != null && deployedList.size() > 0 ) {
					for ( DeploymentInfo deployment : deployedList ) {
						if ( deployment.getName().equals(aws.getDeploymentName()) ) {
							boshInfo.setDeployed(true);
							break;
						}
					}
				}*/
				
				boshInfo.setCreatedDate(aws.getCreatedDate());
				boshList.add(boshInfo);
			}
		}

		if( boshOpenstackList.size() >0 ){
			for(IEDABoshOpenstackConfig openstack : boshOpenstackList){
				
/*				// 기본관리자 UUID와 다른 경우 목록에서 제외
				if ( !defaultDirector.getDirectorUuid().equals(openstack.getDirectorUuid()) )
					continue;*/
				
				BoshInfo boshInfo = new BoshInfo();
				boshInfo.setRecid(recid++);
				boshInfo.setId(openstack.getId());
				boshInfo.setDeploymentName(openstack.getDeploymentName());
				boshInfo.setIaas("OPENSTACK");
				boshInfo.setDirectorUuid(openstack.getDirectorUuid());
				boshInfo.setReleaseVersion(openstack.getReleaseVersion());
				boshInfo.setStemcell(openstack.getStemcellName() + "/" + openstack.getStemcellVersion());
				boshInfo.setPublicIp(openstack.getPublicStaticIp());
				boshInfo.setSubnetRange(openstack.getSubnetRange());
				boshInfo.setGateway(openstack.getSubnetGateway());
				boshInfo.setDns(openstack.getSubnetDns());
				boshInfo.setDeployStatus(openstack.getDeployStatus());
				
/* 				if ( deployedList != null && deployedList.size() > 0 ) {
					for ( DeploymentInfo deployment : deployedList ) {
						if ( deployment.getName().equals(openstack.getDeploymentName()) ) {
							boshInfo.setDeployed(true);
							break;
						}
					}
				} */
				
				boshList.add(boshInfo);
			}
		}
		boshList.stream().sorted((BoshInfo o1, BoshInfo o2) -> o1.getCreatedDate().compareTo(o2.getCreatedDate()));
		return boshList;
	}

	public void boshInstall(String deployFileName) {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		Runtime r = Runtime.getRuntime();
		String command = "";
		
		try{
			command += LocalDirectoryConfiguration.getScriptDir()+ System.getProperty("file.separator")  + "bosh-deploy.sh ";
			command += LocalDirectoryConfiguration.getDeploymentDir()+ System.getProperty("file.separator")  + deployFileName ;

			Process process = r.exec(command);
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			while ((info = bufferedReader.readLine()) != null){
				streamLogs += info;
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
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-bosh-param.yml");
		URL stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-bosh-stub.yml");
		
		File settingFile;
		File stubDeploy;

		String content = "";
		String stubContent = "";
		String settingFileName = iaas.toLowerCase()+"-bosh-param-"+id+".yml";
		
		String deplymentFileName = ""; 
		
		try {
			settingFile = new File(classPath.toURI());//resource.getFile();
			stubDeploy = new File(stubPath.toURI());
			content = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
			stubContent = IOUtils.toString(new FileInputStream(stubDeploy), "UTF-8");

			List<ReplaceItem> replaceItems = setReplaceBoshItems(id, iaas);
			for (ReplaceItem item : replaceItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubDeploy.getName()), "UTF-8");
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName), "UTF-8");
			deplymentFileName = CommonUtils.setSpiffMerge(iaas, id, "bosh" ,stubDeploy.getName(), settingFileName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return deplymentFileName;
	}

	public List<ReplaceItem> setReplaceBoshItems(Integer id, String iaas) {

		List<ReplaceItem> items = new ArrayList<ReplaceItem>();

		if("AWS".equals(iaas.toUpperCase()) ){
			IEDABoshAwsConfig awsConfig = awsRepository.findOne(id);
			
			// AWS
			items.add(new ReplaceItem("[accessKeyId]", awsConfig.getAccessKeyId()));
			items.add(new ReplaceItem("[secretAccessKey]", awsConfig.getSecretAccessKey()));
			items.add(new ReplaceItem("[defaultSecurityGroups]", awsConfig.getDefaultSecurityGroups()));
			items.add(new ReplaceItem("[region]", awsConfig.getRegion()));
			items.add(new ReplaceItem("[privateKeyName]", awsConfig.getPrivateKeyName()));
			items.add(new ReplaceItem("[privateKeyPath]", awsConfig.getPrivateKeyPath()));
			
			// Basic
			items.add(new ReplaceItem("[deploymentName]", awsConfig.getDeploymentName()));
			items.add(new ReplaceItem("[directorUuid]", awsConfig.getDirectorUuid()));
			items.add(new ReplaceItem("[publicStaticIp]", awsConfig.getPublicStaticIp()));
			items.add(new ReplaceItem("[releaseVersion]", awsConfig.getReleaseVersion().split("/")[1]));
			
			// Network
			items.add(new ReplaceItem("[subnetId]", awsConfig.getSubnetId()));			
			items.add(new ReplaceItem("[subnetStatic]", awsConfig.getSubnetStaticFrom() + " - " + awsConfig.getSubnetStaticTo()));
			items.add(new ReplaceItem("[subnetRange]", awsConfig.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", awsConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", awsConfig.getSubnetDns()));
			
			// Resource
			items.add(new ReplaceItem("[stemcellName]", awsConfig.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", awsConfig.getStemcellVersion()));
			items.add(new ReplaceItem("[cloudInstanceType]", awsConfig.getCloudInstanceType()));
			//items.add(new ReplaceItem("[boshPassword]", Encryption.encryption(awsConfig.getBoshPassword())));
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(awsConfig.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		}
		else{
			IEDABoshOpenstackConfig openstackConfig = openstackRepository.findOne(id);

			// Openstack
			items.add(new ReplaceItem("[authUrl]", openstackConfig.getAuthUrl()));
			items.add(new ReplaceItem("[tenant]", openstackConfig.getTenant()));
			items.add(new ReplaceItem("[userName]", openstackConfig.getUserName()));
			items.add(new ReplaceItem("[apiKey]", openstackConfig.getApiKey()));
			items.add(new ReplaceItem("[defaultSecurityGroups]", openstackConfig.getDefaultSecurityGroups()));
			items.add(new ReplaceItem("[privateKeyName]", openstackConfig.getPrivateKeyName()));
			items.add(new ReplaceItem("[privateKeyPath]", openstackConfig.getPrivateKeyPath()));
			
			// BOSH
			items.add(new ReplaceItem("[deploymentName]", openstackConfig.getDeploymentName()));
			items.add(new ReplaceItem("[directorUuid]", openstackConfig.getDirectorUuid()));
			items.add(new ReplaceItem("[releaseVersion]", openstackConfig.getReleaseVersion().split("/")[1]));
			
			// Network
			items.add(new ReplaceItem("[publicStaticIp]", openstackConfig.getPublicStaticIp()));
			items.add(new ReplaceItem("[subnetId]", openstackConfig.getSubnetId()));			
			items.add(new ReplaceItem("[subnetStatic]", openstackConfig.getSubnetStaticFrom() + " - " + openstackConfig.getSubnetStaticTo()));
			items.add(new ReplaceItem("[subnetRange]", openstackConfig.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", openstackConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", openstackConfig.getSubnetDns()));
			
			// Resource
			items.add(new ReplaceItem("[stemcellName]", openstackConfig.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", openstackConfig.getStemcellVersion()));
			items.add(new ReplaceItem("[cloudInstanceType]", openstackConfig.getCloudInstanceType()));
			//items.add(new ReplaceItem("[boshPassword]", Encryption.encryption(openstackConfig.getBoshPassword())));
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(openstackConfig.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		}
		return items;
	}

	public void deleteBoshInfoRecord(BoshParam.Delete dto) {
		try{
			if( "AWS".equals(dto.getIaas())){ 
				IEDABoshAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
				awsRepository.delete(Integer.parseInt(dto.getId()));

			} else {
				IEDABoshOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
				openstackRepository.delete(Integer.parseInt(dto.getId()));
			}
			
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.boshdelete.exception",
					"삭제중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
	}
	

}
