package org.openpaas.ieda.web.deploy.diego;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDADiegoService {

	@Autowired
	private IEDADiegoAwsRepository awsRepository;

	@Autowired
	private IEDADiegoOpenstackRepository openstackRepository;

	public List<DiegoInfo> getList() {
		List<DiegoInfo> diegoList = null;
		List<IEDADiegoAwsConfig> awsList = awsRepository.findAll();
		List<IEDADiegoOpenstackConfig> openstackList = openstackRepository.findAll();

		if( awsList != null && openstackList != null){
			diegoList = new ArrayList<>();

			int recid = 0;

			if( awsList != null ){
				for(IEDADiegoAwsConfig config:awsList){
					DiegoInfo diegoInfo = new DiegoInfo();
					diegoInfo.setRecid(recid++);
					diegoInfo.setId(config.getId());
					diegoInfo.setIaas("AWS");
					diegoInfo.setCreateDate(config.getCreatedDate());
					diegoInfo.setUpdateDate(config.getUpdatedDate());

					//1.1 기본정보	
					diegoInfo.setDeploymentName(config.getDeploymentName());
					diegoInfo.setDirectorUuid(config.getDirectorUuid());
					diegoInfo.setDiegoReleaseName(config.getDiegoReleaseName());
					diegoInfo.setDiegoReleaseVersion(config.getDiegoReleaseVersion());
					diegoInfo.setCfReleaseName(config.getCfReleaseName());
					diegoInfo.setCfReleaseVersion(config.getCfReleaseVersion());
					diegoInfo.setGardenLinuxReleaseName(config.getGardenLinuxReleaseName());
					diegoInfo.setGardenLinuxReleaseVersion(config.getGardenLinuxReleaseVersion());
					diegoInfo.setEtcdReleaseName(config.getEtcdReleaseName());
					diegoInfo.setEtcdReleaseVersion(config.getEtcdReleaseVersion());
					//1.2 DIEGO 정보	
					diegoInfo.setDomain(config.getDomain());
					diegoInfo.setDeployment(config.getDeployment());
					diegoInfo.setEtcdMachines(config.getEtcdMachines());
					diegoInfo.setNatsMachines(config.getNatsMachines());
					diegoInfo.setConsulServersLan(config.getConsulServersLan());

					//3.1 네트워크 정보	
					diegoInfo.setSubnetStaticFrom(config.getSubnetStaticFrom());
					diegoInfo.setSubnetStaticTo(config.getSubnetStaticTo());
					diegoInfo.setSubnetReservedFrom(config.getSubnetReservedFrom());
					diegoInfo.setSubnetReservedTo(config.getSubnetReservedTo());
					diegoInfo.setSubnetRange(config.getSubnetRange());
					diegoInfo.setSubnetGateway(config.getSubnetGateway());
					diegoInfo.setSubnetDns(config.getSubnetDns());
					diegoInfo.setSubnetId(config.getSubnetId());
					diegoInfo.setCloudSecurityGroups(config.getCloudSecurityGroups());	

					//4 리소스 정보	
					diegoInfo.setStemcellName(config.getStemcellName());
					diegoInfo.setStemcellVersion(config.getStemcellVersion());

					// Deploy 정보
					diegoInfo.setDeploymentFile(config.getDeploymentFile());
					diegoInfo.setDeployStatus(config.getDeployStatus());
					if( !StringUtils.isEmpty( config.getTaskId() ) ) diegoInfo.setTaskId(config.getTaskId());
					
					diegoList.add(diegoInfo);
				}
			}

			if( openstackList != null){
				for(IEDADiegoOpenstackConfig config : openstackList ){
					DiegoInfo diegoInfo = new DiegoInfo();
					diegoInfo.setRecid(recid++);
					diegoInfo.setId(config.getId());
					diegoInfo.setIaas("OPENSTACK");
					diegoInfo.setCreateDate(config.getCreatedDate());
					diegoInfo.setUpdateDate(config.getUpdatedDate());

					//1.1 기본정보	
					diegoInfo.setDeploymentName(config.getDeploymentName());
					diegoInfo.setDirectorUuid(config.getDirectorUuid());
					diegoInfo.setDiegoReleaseName(config.getDiegoReleaseName());
					diegoInfo.setDiegoReleaseVersion(config.getDiegoReleaseVersion());
					diegoInfo.setCfReleaseName(config.getCfReleaseName());
					diegoInfo.setCfReleaseVersion(config.getCfReleaseVersion());
					diegoInfo.setGardenLinuxReleaseName(config.getGardenLinuxReleaseName());
					diegoInfo.setGardenLinuxReleaseVersion(config.getGardenLinuxReleaseVersion());
					diegoInfo.setEtcdReleaseName(config.getEtcdReleaseName());
					diegoInfo.setEtcdReleaseVersion(config.getEtcdReleaseVersion());
					//1.2 DIEGO 정보	
					diegoInfo.setDomain(config.getDomain());
					diegoInfo.setDeployment(config.getDeployment());
					diegoInfo.setEtcdMachines(config.getEtcdMachines());
					diegoInfo.setNatsMachines(config.getNatsMachines());
					diegoInfo.setConsulServersLan(config.getConsulServersLan());

					//3.1 네트워크 정보	
					diegoInfo.setSubnetStaticFrom(config.getSubnetStaticFrom());
					diegoInfo.setSubnetStaticTo(config.getSubnetStaticTo());
					diegoInfo.setSubnetReservedFrom(config.getSubnetReservedFrom());
					diegoInfo.setSubnetReservedTo(config.getSubnetReservedTo());
					diegoInfo.setSubnetRange(config.getSubnetRange());
					diegoInfo.setSubnetGateway(config.getSubnetGateway());
					diegoInfo.setSubnetDns(config.getSubnetDns());
					diegoInfo.setSubnetId(config.getCloudNetId());
					diegoInfo.setCloudSecurityGroups(config.getCloudSecurityGroups());	

					//4 리소스 정보	
					diegoInfo.setStemcellName(config.getStemcellName());
					diegoInfo.setStemcellVersion(config.getStemcellVersion());

					// Deploy 정보
					diegoInfo.setDeploymentFile(config.getDeploymentFile());
					diegoInfo.setDeployStatus(config.getDeployStatus());
					if( !StringUtils.isEmpty( config.getTaskId() ) ) diegoInfo.setTaskId(config.getTaskId());
					
					diegoList.add(diegoInfo);
				}
			}
		}

		return diegoList;
	}

	public String createSettingFile(int id, String iaas) {
		String content = "";
		String stubContent = "";
		String settingFileName = iaas.toLowerCase()+"-diego-"+id+".yml";

		String deplymentFileName = ""; 

		try {
			InputStream paramIs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/"+iaas.toLowerCase()+"-diego-param.yml");
			InputStream stubIs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/"+iaas.toLowerCase()+"-diego-stub.yml");
			
			content = IOUtils.toString(paramIs, "UTF-8");
			stubContent = IOUtils.toString(stubIs, "UTF-8");

			List<ReplaceItem> replaceItems = setReplaceItems(id, iaas);
			for (ReplaceItem item : replaceItems) {
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(stubContent, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + iaas.toLowerCase()+"-diego-stub.yml"), "UTF-8");
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + System.getProperty("file.separator") + settingFileName), "UTF-8");
			deplymentFileName = CommonUtils.setSpiffMerge(iaas, id, "diego", iaas.toLowerCase()+"-diego-stub.yml", settingFileName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return deplymentFileName;
	}

	private List<ReplaceItem> setReplaceItems(int id, String iaas) {
		List<ReplaceItem> items = new ArrayList<ReplaceItem>();

		if( "AWS".equals(iaas.toUpperCase()) ){
			IEDADiegoAwsConfig config  = awsRepository.findOne(id);
			//1.1 기본정보	
			items.add(new ReplaceItem("[deploymentName]", config.getDeploymentName()));
			items.add(new ReplaceItem("[directorUuid]", config.getDirectorUuid()));
			items.add(new ReplaceItem("[diegoReleaseName]", config.getDiegoReleaseName()));
			items.add(new ReplaceItem("[diegoReleaseVersion]", "\"" + config.getDiegoReleaseVersion() + "\""));
			items.add(new ReplaceItem("[cfReleaseName]", config.getCfReleaseName()));
			items.add(new ReplaceItem("[cfReleaseVersion]", "\"" + config.getCfReleaseVersion() + "\""));
			items.add(new ReplaceItem("[gardenLinuxReleaseName]", config.getGardenLinuxReleaseName()));
			items.add(new ReplaceItem("[gardenLinuxReleaseVersion]", "\"" + config.getGardenLinuxReleaseVersion() + "\""));
			items.add(new ReplaceItem("[etcdReleaseName]", config.getEtcdReleaseName()));
			items.add(new ReplaceItem("[etcdReleaseVersion]", "\"" + config.getEtcdReleaseVersion() + "\""));
			//1.2 CF 정보
			items.add(new ReplaceItem("[domain]", config.getDomain()));
			items.add(new ReplaceItem("[deployment]", config.getDeployment()));
			items.add(new ReplaceItem("[secret]", config.getSecret()));
			items.add(new ReplaceItem("[etcdMachines]", config.getEtcdMachines()));
			items.add(new ReplaceItem("[natsMachines]", config.getNatsMachines()));
			items.add(new ReplaceItem("[consulServersLan]", config.getConsulServersLan()));
			items.add(new ReplaceItem("[consulAgentCert]", CommonUtils.lineAddSpace(config.getConsulAgentCert(),6)));
			items.add(new ReplaceItem("[consulAgentKey]", CommonUtils.lineAddSpace(config.getConsulAgentKey(),6)));
			items.add(new ReplaceItem("[consulCaCert]", CommonUtils.lineAddSpace(config.getConsulCaCert(),6)));
			items.add(new ReplaceItem("[consulEncryptKeys]", config.getConsulEncryptKeys()));
			items.add(new ReplaceItem("[consulServerCert]", CommonUtils.lineAddSpace(config.getConsulServerCert(),6)));
			items.add(new ReplaceItem("[consulServerKey]", CommonUtils.lineAddSpace(config.getConsulServerKey(),6)));
			//2.1 Diego 정보				
			items.add(new ReplaceItem("[diegoCaCert]", CommonUtils.lineAddSpace(config.getDiegoCaCert(),10)));
			//2.2 프록시 정보
			items.add(new ReplaceItem("[diegoHostKey]",  CommonUtils.lineAddSpace(config.getDiegoHostKey(),8)));
			//2.3 BBS 인증정보
			items.add(new ReplaceItem("[diegoClientCert]", CommonUtils.lineAddSpace(config.getDiegoClientCert(),10)));
			items.add(new ReplaceItem("[diegoClientKey]", CommonUtils.lineAddSpace(config.getDiegoClientKey(),10)));
			items.add(new ReplaceItem("[diegoEncryptionKeys]", config.getDiegoEncryptionKeys()));
			items.add(new ReplaceItem("[diegoServerCert]", CommonUtils.lineAddSpace(config.getDiegoServerCert(),8)));
			items.add(new ReplaceItem("[diegoServerKey]", CommonUtils.lineAddSpace(config.getDiegoServerKey(),8)));
			//3. ETCD 정보
			items.add(new ReplaceItem("[etcdClientCert]", CommonUtils.lineAddSpace(config.getEtcdClientCert(),6)));
			items.add(new ReplaceItem("[etcdClientKey]", CommonUtils.lineAddSpace(config.getEtcdClientKey(),6)));
			items.add(new ReplaceItem("[etcdPeerCaCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCaCert(),6)));
			items.add(new ReplaceItem("[etcdPeerCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCert(),6)));
			items.add(new ReplaceItem("[etcdPeerKey]", CommonUtils.lineAddSpace(config.getEtcdPeerKey(),6)));
			items.add(new ReplaceItem("[etcdServerCert]", CommonUtils.lineAddSpace(config.getEtcdServerCert(),6)));
			items.add(new ReplaceItem("[etcdServerKey]", CommonUtils.lineAddSpace(config.getEtcdServerKey(),6)));
			//4. 네트워크 정보
			items.add(new ReplaceItem("[subnetStatic]", config.getSubnetStaticFrom() + " - " + config.getSubnetStaticTo() ));
			items.add(new ReplaceItem("[subnetReserved]", config.getSubnetReservedFrom() + " - " + config.getSubnetReservedTo()));
			items.add(new ReplaceItem("[subnetRange]", config.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", config.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", config.getSubnetDns()));
			items.add(new ReplaceItem("[subnetId]", config.getSubnetId()));
			items.add(new ReplaceItem("[cloudSecurityGroups]", config.getCloudSecurityGroups()));
			//5. 리소스 정보	
			items.add(new ReplaceItem("[stemcellName]", config.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", config.getStemcellVersion()));
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(config.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		}
		else{
			IEDADiegoOpenstackConfig config  = openstackRepository.findOne(id);

			//1.1 기본정보	
			items.add(new ReplaceItem("[deploymentName]", config.getDeploymentName()));
			items.add(new ReplaceItem("[directorUuid]", config.getDirectorUuid()));
			items.add(new ReplaceItem("[diegoReleaseName]", config.getDiegoReleaseName()));
			items.add(new ReplaceItem("[diegoReleaseVersion]", "\"" + config.getDiegoReleaseVersion() + "\""));
			items.add(new ReplaceItem("[cfReleaseName]", config.getCfReleaseName()));
			items.add(new ReplaceItem("[cfReleaseVersion]", "\"" + config.getCfReleaseVersion() + "\""));
			items.add(new ReplaceItem("[gardenLinuxReleaseName]", config.getGardenLinuxReleaseName()));
			items.add(new ReplaceItem("[gardenLinuxReleaseVersion]", "\"" + config.getGardenLinuxReleaseVersion() + "\""));
			items.add(new ReplaceItem("[etcdReleaseName]", config.getEtcdReleaseName()));
			items.add(new ReplaceItem("[etcdReleaseVersion]", "\"" + config.getEtcdReleaseVersion() + "\""));
			//1.2 CF 정보
			items.add(new ReplaceItem("[domain]", config.getDomain()));
			items.add(new ReplaceItem("[deployment]", config.getDeployment()));
			items.add(new ReplaceItem("[secret]", config.getSecret()));
			items.add(new ReplaceItem("[etcdMachines]", config.getEtcdMachines()));
			items.add(new ReplaceItem("[natsMachines]", config.getNatsMachines()));
			items.add(new ReplaceItem("[consulServersLan]", config.getConsulServersLan()));
			items.add(new ReplaceItem("[consulAgentCert]", CommonUtils.lineAddSpace(config.getConsulAgentCert(),6)));
			items.add(new ReplaceItem("[consulAgentKey]", CommonUtils.lineAddSpace(config.getConsulAgentKey(),6)));
			items.add(new ReplaceItem("[consulCaCert]", CommonUtils.lineAddSpace(config.getConsulCaCert(),6)));
			items.add(new ReplaceItem("[consulEncryptKeys]", config.getConsulEncryptKeys()));
			items.add(new ReplaceItem("[consulServerCert]", CommonUtils.lineAddSpace(config.getConsulServerCert(),6)));
			items.add(new ReplaceItem("[consulServerKey]", CommonUtils.lineAddSpace(config.getConsulServerKey(),6)));
			//2.1 Diego 정보				
			items.add(new ReplaceItem("[diegoCaCert]", CommonUtils.lineAddSpace(config.getDiegoCaCert(),10)));
			//2.2 프록시 정보
			items.add(new ReplaceItem("[diegoHostKey]",  CommonUtils.lineAddSpace(config.getDiegoHostKey(),8)));
			//2.3 BBS 인증정보
			items.add(new ReplaceItem("[diegoClientCert]", CommonUtils.lineAddSpace(config.getDiegoClientCert(),10)));
			items.add(new ReplaceItem("[diegoClientKey]", CommonUtils.lineAddSpace(config.getDiegoClientKey(),10)));
			items.add(new ReplaceItem("[diegoEncryptionKeys]", config.getDiegoEncryptionKeys()));
			items.add(new ReplaceItem("[diegoServerCert]", CommonUtils.lineAddSpace(config.getDiegoServerCert(),8)));
			items.add(new ReplaceItem("[diegoServerKey]", CommonUtils.lineAddSpace(config.getDiegoServerKey(),8)));
			//3. ETCD 정보
			items.add(new ReplaceItem("[etcdClientCert]", CommonUtils.lineAddSpace(config.getEtcdClientCert(),6)));
			items.add(new ReplaceItem("[etcdClientKey]", CommonUtils.lineAddSpace(config.getEtcdClientKey(),6)));
			items.add(new ReplaceItem("[etcdPeerCaCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCaCert(),6)));
			items.add(new ReplaceItem("[etcdPeerCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCert(),6)));
			items.add(new ReplaceItem("[etcdPeerKey]", CommonUtils.lineAddSpace(config.getEtcdPeerKey(),6)));
			items.add(new ReplaceItem("[etcdServerCert]", CommonUtils.lineAddSpace(config.getEtcdServerCert(),6)));
			items.add(new ReplaceItem("[etcdServerKey]", CommonUtils.lineAddSpace(config.getEtcdServerKey(),6)));
			//4. 네트워크 정보
			items.add(new ReplaceItem("[subnetStatic]", config.getSubnetStaticFrom() + " - " + config.getSubnetStaticTo() ));
			items.add(new ReplaceItem("[subnetReserved]", config.getSubnetReservedFrom() + " - " + config.getSubnetReservedTo()));
			items.add(new ReplaceItem("[subnetRange]", config.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", config.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", config.getSubnetDns()));
			items.add(new ReplaceItem("[cloudNetId]", config.getCloudNetId()));
			items.add(new ReplaceItem("[cloudSecurityGroups]", config.getCloudSecurityGroups()));
			//5. 리소스 정보	
			items.add(new ReplaceItem("[stemcellName]", config.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", config.getStemcellVersion()));
			items.add(new ReplaceItem("[boshPassword]", Sha512Crypt.Sha512_crypt(config.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		}

		return items;
	}

	public void deleteDiegoInfoRecord(DiegoParam.Delete dto) {
		try{
			if( "AWS".equals(dto.getIaas())){ 
				IEDADiegoAwsConfig config = awsRepository.findOne(Integer.parseInt(dto.getId()));
				awsRepository.delete(Integer.parseInt(dto.getId()));

			} else {
				IEDADiegoOpenstackConfig config = openstackRepository.findOne(Integer.parseInt(dto.getId()));
				openstackRepository.delete(Integer.parseInt(dto.getId()));
			}

		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.diegodelete.exception",
					"삭제중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
	}

}
