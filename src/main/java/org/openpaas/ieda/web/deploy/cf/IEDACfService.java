package org.openpaas.ieda.web.deploy.cf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDACfService {

	@Autowired
	private IEDACfAwsRepository awsRepository;
	@Autowired
	private IEDACfOpenstackRepository openstackRepository;
	
	public List<CfInfo> listCfs() {
		List<CfInfo> cfList = null;

		List<IEDACfAwsConfig> listrAws = awsRepository.findAll();
		List<IEDACfOpenstackConfig> listOpenstack  = openstackRepository.findAll();

		if( listrAws != null || listOpenstack != null ){
			cfList = new ArrayList<>();
			int recid = 0;
			if( listrAws != null ){
				for(IEDACfAwsConfig config : listrAws){
					CfInfo cfInfo = new CfInfo();
					cfInfo.setRecid(recid++);
					cfInfo.setId(config.getId());
					cfInfo.setIaas("AWS");
					cfInfo.setCreateDate(config.getCreatedDate());
					cfInfo.setUpdateDate(config.getUpdatedDate());
					
					cfInfo.setDeploymentName(config.getDeploymentName());
					cfInfo.setDirectorUuid(config.getDirectorUuid());
					cfInfo.setReleaseName(config.getReleaseName());
					cfInfo.setReleaseVersion(config.getReleaseVersion());
					cfInfo.setAppSshFingerprint(config.getAppSshFingerprint());

					cfInfo.setDomain(config.getDomain());
					cfInfo.setDescription(config.getDescription());
					cfInfo.setDomainOrganization(config.getDomainOrganization());
					
					cfInfo.setProxyStaticIps(config.getProxyStaticIps());
					
					cfInfo.setSubnetRange(config.getSubnetRange());
					cfInfo.setSubnetGateway(config.getSubnetGateway());
					cfInfo.setSubnetDns(config.getSubnetDns());
					cfInfo.setSubnetReservedFrom(config.getSubnetReservedFrom());
					cfInfo.setSubnetReservedTo(config.getSubnetReservedTo());
					cfInfo.setSubnetStaticFrom(config.getSubnetStaticFrom());
					cfInfo.setSubnetStaticTo(config.getSubnetStaticTo());
					cfInfo.setSubnetId(config.getSubnetId());
					
					cfInfo.setStemcellName(config.getStemcellName());
					cfInfo.setStemcellVersion(config.getStemcellVersion());
					cfInfo.setBoshPassword(config.getBoshPassword());
					
					cfInfo.setDeployStatus(config.getDeployStatus());
					cfInfo.setDeployStatus(config.getDeployStatus());
					cfList.add(cfInfo);
				}
			}

			if ( listOpenstack != null ){
				for(IEDACfOpenstackConfig config : listOpenstack){
					CfInfo cfInfo = new CfInfo();
					cfInfo.setRecid(recid++);
					cfInfo.setId(config.getId());
					cfInfo.setIaas("OPENSTACK");
					cfInfo.setCreateDate(config.getCreatedDate());
					cfInfo.setUpdateDate(config.getUpdatedDate());
					
					cfInfo.setDeploymentName(config.getDeploymentName());
					cfInfo.setDirectorUuid(config.getDirectorUuid());
					cfInfo.setReleaseName(config.getReleaseName());
					cfInfo.setReleaseVersion(config.getReleaseVersion());
					cfInfo.setAppSshFingerprint(config.getAppSshFingerprint());

					cfInfo.setDomain(config.getDomain());
					cfInfo.setDescription(config.getDescription());
					cfInfo.setDomainOrganization(config.getDomainOrganization());
					
					cfInfo.setProxyStaticIps(config.getProxyStaticIps());
					
					cfInfo.setSubnetRange(config.getSubnetRange());
					cfInfo.setSubnetGateway(config.getSubnetGateway());
					cfInfo.setSubnetDns(config.getSubnetDns());
					cfInfo.setSubnetReservedFrom(config.getSubnetReservedFrom());
					cfInfo.setSubnetReservedTo(config.getSubnetReservedTo());
					cfInfo.setSubnetStaticFrom(config.getSubnetStaticFrom());
					cfInfo.setSubnetStaticTo(config.getSubnetStaticTo());
					cfInfo.setSubnetId(config.getCloudNetId());
					
					cfInfo.setStemcellName(config.getStemcellName());
					cfInfo.setStemcellVersion(config.getStemcellVersion());
					cfInfo.setBoshPassword(config.getBoshPassword());
					
					cfInfo.setDeployStatus(config.getDeployStatus());
					cfInfo.setDeployStatus(config.getDeployStatus());
					cfList.add(cfInfo);
				}
			}
		}
		return cfList;
	}

	public String createSettingFile(Integer id, String iaas) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-cf-param.yml");
		URL stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-cf-stub.yml");

		File settingFile;
		File stubDeploy;

		String content = "";
		String stubContent = "";
		String settingFileName = iaas.toLowerCase()+"-cf-"+id+".yml";

		String deplymentFileName = ""; 

		try {
			settingFile = new File(classPath.toURI());//resource.getFile();
			stubDeploy = new File(stubPath.toURI());
			content = IOUtils.toString(new FileInputStream(settingFile), "UTF-8");
			stubContent = IOUtils.toString(new FileInputStream(stubDeploy), "UTF-8");

			List<ReplaceItem> replaceItems = setReplaceItems(id, iaas);
			for (ReplaceItem item : replaceItems) {
				log.info(item.getTargetItem() +" / "+  item.getSourceItem());
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + stubDeploy.getName()), "UTF-8");
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName), "UTF-8");
			deplymentFileName = CommonUtils.setSpiffMerge(iaas, id, "cf", stubDeploy.getName(), settingFileName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return deplymentFileName;
	}

	public List<ReplaceItem> setReplaceItems(Integer id, String iaas) {

		List<ReplaceItem> items = new ArrayList<ReplaceItem>();

		if("AWS".equals(iaas.toUpperCase()) ){
			IEDACfAwsConfig awsConfig = awsRepository.findOne(id);

			// 1.1 Deployment 정보
			items.add(new ReplaceItem("[deploymentName]", awsConfig.getDeploymentName()));
			items.add(new ReplaceItem("[directorUuid]", awsConfig.getDirectorUuid()));
			items.add(new ReplaceItem("[releaseName]", awsConfig.getReleaseName()));
			items.add(new ReplaceItem("[releaseVersion]", awsConfig.getReleaseVersion()));
			items.add(new ReplaceItem("[appSshFingerprint]", awsConfig.getAppSshFingerprint()));
			
			// 1.2 기본정보
			items.add(new ReplaceItem("[domain]", awsConfig.getDomain()));
			items.add(new ReplaceItem("[description]", awsConfig.getDescription()));
			items.add(new ReplaceItem("[domainOrganization]", awsConfig.getDomainOrganization()));

			// 1.3 프록시 정보
			items.add(new ReplaceItem("[proxyStaticIps]", awsConfig.getProxyStaticIps()));
			items.add(new ReplaceItem("[sslPemPub]", CommonUtils.lineAddSpace(awsConfig.getSslPemPub(),8)));
			items.add(new ReplaceItem("[sslPemRsa]", CommonUtils.lineAddSpace(awsConfig.getSslPemRsa(),8)));

			// 2. UAA 정보
			items.add(new ReplaceItem("[loginSecret]", awsConfig.getLoginSecret()));
			items.add(new ReplaceItem("[signingKey]", CommonUtils.lineAddSpace(awsConfig.getSigningKey(),8)));
			items.add(new ReplaceItem("[verificationKey]", CommonUtils.lineAddSpace(awsConfig.getVerificationKey(),8)));

			// 3. Consul 정보
			items.add(new ReplaceItem("[agentCert]", CommonUtils.lineAddSpace(awsConfig.getAgentCert(),6)));
			items.add(new ReplaceItem("[agentKey]", CommonUtils.lineAddSpace(awsConfig.getAgentKey(),6)));
			items.add(new ReplaceItem("[caCert]", CommonUtils.lineAddSpace(awsConfig.getCaCert(),6)));
			items.add(new ReplaceItem("[encryptKeys]", awsConfig.getEncryptKeys()));
			items.add(new ReplaceItem("[serverCert]", CommonUtils.lineAddSpace(awsConfig.getServerCert(),6)));
			items.add(new ReplaceItem("[serverKey]", CommonUtils.lineAddSpace(awsConfig.getServerKey(),6)));

			// 4. 네트워크 정보
			items.add(new ReplaceItem("[subnetRange]", awsConfig.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", awsConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", awsConfig.getSubnetDns()));
			items.add(new ReplaceItem("[subnetReserved]", awsConfig.getSubnetReservedFrom() + " - " + awsConfig.getSubnetReservedTo()));
			items.add(new ReplaceItem("[subnetStatic]", awsConfig.getSubnetStaticFrom() + " - " + awsConfig.getSubnetStaticTo()));
			items.add(new ReplaceItem("[subnetId]", awsConfig.getSubnetId()));			
			items.add(new ReplaceItem("[cloudSecurityGroups]", awsConfig.getCloudSecurityGroups()));

			// 5. 리소스 정보
			items.add(new ReplaceItem("[stemcellName]", awsConfig.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", awsConfig.getStemcellVersion()));
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(awsConfig.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		}
		else{
			IEDACfOpenstackConfig openstackConfig = openstackRepository.findOne(id);

			// 1.1 Deployment 정보
			items.add(new ReplaceItem("[deploymentName]", openstackConfig.getDeploymentName()));
			items.add(new ReplaceItem("[directorUuid]", openstackConfig.getDirectorUuid()));
			items.add(new ReplaceItem("[releaseName]", openstackConfig.getReleaseName()));
			items.add(new ReplaceItem("[releaseVersion]", openstackConfig.getReleaseVersion()));

			// 1.2 기본정보
			items.add(new ReplaceItem("[domain]", openstackConfig.getDomain()));
			items.add(new ReplaceItem("[description]", openstackConfig.getDescription()));
			items.add(new ReplaceItem("[domainOrganization]", openstackConfig.getDomainOrganization()));
			items.add(new ReplaceItem("[appSshFingerprint]", openstackConfig.getAppSshFingerprint()));
			
			// 1.3 프록시 정보
			items.add(new ReplaceItem("[proxyStaticIps]", openstackConfig.getProxyStaticIps()));
			items.add(new ReplaceItem("[sslPemPub]", CommonUtils.lineAddSpace(openstackConfig.getSslPemPub(),8)));
			items.add(new ReplaceItem("[sslPemRsa]", CommonUtils.lineAddSpace(openstackConfig.getSslPemRsa(),8)));

			// 2. UAA 정보
			items.add(new ReplaceItem("[loginSecret]", openstackConfig.getLoginSecret()));
			items.add(new ReplaceItem("[signingKey]", CommonUtils.lineAddSpace(openstackConfig.getSigningKey(),8)));
			items.add(new ReplaceItem("[verificationKey]", CommonUtils.lineAddSpace(openstackConfig.getVerificationKey(),8)));

			// 3. Consul 정보
			items.add(new ReplaceItem("[agentCert]", CommonUtils.lineAddSpace(openstackConfig.getAgentCert(),6)));
			items.add(new ReplaceItem("[agentKey]", CommonUtils.lineAddSpace(openstackConfig.getAgentKey(),6)));
			items.add(new ReplaceItem("[caCert]", CommonUtils.lineAddSpace(openstackConfig.getCaCert(),6)));
			items.add(new ReplaceItem("[encryptKeys]", openstackConfig.getEncryptKeys()));
			items.add(new ReplaceItem("[serverCert]", CommonUtils.lineAddSpace(openstackConfig.getServerCert(),6)));
			items.add(new ReplaceItem("[serverKey]", CommonUtils.lineAddSpace(openstackConfig.getServerKey(),6)));

			// 4. 네트워크 정보
			items.add(new ReplaceItem("[subnetRange]", openstackConfig.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", openstackConfig.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", openstackConfig.getSubnetDns()));
			items.add(new ReplaceItem("[subnetReserved]", openstackConfig.getSubnetReservedFrom() + " - " + openstackConfig.getSubnetReservedTo()));
			items.add(new ReplaceItem("[subnetStatic]", openstackConfig.getSubnetStaticFrom() + " - " + openstackConfig.getSubnetStaticTo()));
			items.add(new ReplaceItem("[cloudNetId]", openstackConfig.getCloudNetId()));			
			items.add(new ReplaceItem("[cloudSecurityGroups]", openstackConfig.getCloudSecurityGroups()));
			
			// 5. 리소스 정보
			items.add(new ReplaceItem("[stemcellName]", openstackConfig.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", openstackConfig.getStemcellVersion()));
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(openstackConfig.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		}
		return items;
	}

	public void deleteCfInfoRecord(CfParam.Delete dto) {
		try{
			if( "AWS".equals(dto.getIaas())){ 
				IEDACfAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
				awsRepository.delete(Integer.parseInt(dto.getId()));

			} else {
				IEDACfOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
				openstackRepository.delete(Integer.parseInt(dto.getId()));
			}
			
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.cfdelete.exception",
					"삭제중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
	}

}