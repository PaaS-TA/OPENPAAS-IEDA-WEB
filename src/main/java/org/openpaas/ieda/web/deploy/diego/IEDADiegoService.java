package org.openpaas.ieda.web.deploy.diego;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.CommonUtils;
import org.openpaas.ieda.web.common.ReplaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDADiegoService {

	@Autowired
	private IEDADiegoAwsRepository awsRepository;

	@Autowired
	private IEDADiegoOpenstackRepository openstackRepository;

	public List<DiegoListDto> getList() {
		List<DiegoListDto> diegoList = null;
		List<IEDADiegoAwsConfig> awsList = awsRepository.findAll();
		List<IEDADiegoOpenstackConfig> openstackList = openstackRepository.findAll();

		if( awsList != null && openstackList != null){
			diegoList = new ArrayList<>();

			int recid = 0;

			if( awsList != null ){
				for(IEDADiegoAwsConfig config:awsList){
					DiegoListDto dto = new DiegoListDto();
					dto.setRecid(recid++);
					dto.setId(config.getId());
					dto.setIaas("AWS");
					dto.setCreateDate(config.getCreatedDate());
					dto.setUpdateDate(config.getUpdatedDate());

					//1.1 기본정보	
					dto.setDeploymentName(config.getDeploymentName());
					dto.setDirectorUuid(config.getDirectorUuid());
					dto.setDiegoReleaseName(config.getDiegoReleaseName());
					dto.setDiegoReleaseVersion(config.getDiegoReleaseVersion());
					dto.setDiegoReleaseName(config.getDiegoReleaseName());
					dto.setDiegoReleaseVersion(config.getDiegoReleaseVersion());
					dto.setGardenLinuxReleaseName(config.getGardenLinuxReleaseName());
					dto.setGardenLinuxReleaseVersion(config.getGardenLinuxReleaseVersion());
					dto.setEtcdReleaseName(config.getEtcdReleaseName());
					dto.setEtcdReleaseVersion(config.getEtcdReleaseVersion());
					//1.2 DIEGO 정보	
					dto.setDomain(config.getDomain());
					dto.setDeployment(config.getDeployment());
					dto.setEtcdMachines(config.getEtcdMachines());
					dto.setNatsMachines(config.getNatsMachines());
					dto.setConsulServersLan(config.getConsulServersLan());

					//3.1 네트워크 정보	
					dto.setSubnetStaticFrom(config.getSubnetStaticFrom());
					dto.setSubnetStaticTo(config.getSubnetStaticTo());
					dto.setSubnetReservedFrom(config.getSubnetReservedFrom());
					dto.setSubnetReservedTo(config.getSubnetReservedTo());
					dto.setSubnetRange(config.getSubnetRange());
					dto.setSubnetGateway(config.getSubnetGateway());
					dto.setSubnetDns(config.getSubnetDns());
					dto.setSubnetId(config.getSubnetId());
					dto.setCloudSecurityGroups(config.getCloudSecurityGroups());	
					//3.2 프록시 정보
					dto.setDiegoServers(config.getDiegoServers());

					//4 리소스 정보	
					dto.setStemcellName(config.getStemcellName());
					dto.setStemcellVersion(config.getStemcellVersion());

					// Deploy 정보
					dto.setDeploymentFile(config.getDeploymentFile());
					dto.setDeployStatus(config.getDeployStatus());
					dto.setDeployLog(config.getDeployLog());
					
					diegoList.add(dto);
				}
			}

			if( openstackList != null){
				for(IEDADiegoOpenstackConfig config : openstackList ){
					DiegoListDto dto = new DiegoListDto();
					dto.setRecid(recid++);
					dto.setId(config.getId());
					dto.setIaas("OPENSTACK");
					dto.setCreateDate(config.getCreatedDate());
					dto.setUpdateDate(config.getUpdatedDate());

					//1.1 기본정보	
					dto.setDeploymentName(config.getDeploymentName());
					dto.setDirectorUuid(config.getDirectorUuid());
					dto.setDiegoReleaseName(config.getDiegoReleaseName());
					dto.setDiegoReleaseVersion(config.getDiegoReleaseVersion());
					dto.setDiegoReleaseName(config.getDiegoReleaseName());
					dto.setDiegoReleaseVersion(config.getDiegoReleaseVersion());
					dto.setGardenLinuxReleaseName(config.getGardenLinuxReleaseName());
					dto.setGardenLinuxReleaseVersion(config.getGardenLinuxReleaseVersion());
					dto.setEtcdReleaseName(config.getEtcdReleaseName());
					dto.setEtcdReleaseVersion(config.getEtcdReleaseVersion());
					//1.2 DIEGO 정보	
					dto.setDomain(config.getDomain());
					dto.setDeployment(config.getDeployment());
					dto.setEtcdMachines(config.getEtcdMachines());
					dto.setNatsMachines(config.getNatsMachines());
					dto.setConsulServersLan(config.getConsulServersLan());

					//3.1 네트워크 정보	
					dto.setSubnetStaticFrom(config.getSubnetStaticFrom());
					dto.setSubnetStaticTo(config.getSubnetStaticTo());
					dto.setSubnetReservedFrom(config.getSubnetReservedFrom());
					dto.setSubnetReservedTo(config.getSubnetReservedTo());
					dto.setSubnetRange(config.getSubnetRange());
					dto.setSubnetGateway(config.getSubnetGateway());
					dto.setSubnetDns(config.getSubnetDns());
					dto.setSubnetId(config.getCloudNetId());
					dto.setCloudSecurityGroups(config.getCloudSecurityGroups());	
					//3.2 프록시 정보
					dto.setDiegoServers(config.getDiegoServers());

					//4 리소스 정보	
					dto.setStemcellName(config.getStemcellName());
					dto.setStemcellVersion(config.getStemcellVersion());

					// Deploy 정보
					dto.setDeploymentFile(config.getDeploymentFile());
					dto.setDeployStatus(config.getDeployStatus());
					dto.setDeployLog(config.getDeployLog());
					diegoList.add(dto);
				}
			}
		}

		return diegoList;
	}

	public String createSettingFile(int id, String iaas) {
		// 파일 가져오기
		URL classPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-diego-param.yml");
		URL stubPath = this.getClass().getClassLoader().getResource("static/deploy_template/"+iaas.toLowerCase()+"-diego-stub.yml");

		File settingFile;
		File stubDeploy;

		String content = "";
		String stubContent = "";
		String settingFileName = iaas.toLowerCase()+"-diego-"+id+".yml";

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
			deplymentFileName = CommonUtils.setSpiffMerge(iaas, id, "diego", stubDeploy.getName(), settingFileName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
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
			items.add(new ReplaceItem("[diegoReleaseVersion]", config.getDiegoReleaseVersion()));
			items.add(new ReplaceItem("[cfReleaseName]", config.getCfReleaseName()));
			items.add(new ReplaceItem("[cfReleaseVersion]", config.getCfReleaseVersion()));
			items.add(new ReplaceItem("[gardenLinuxReleaseName]", config.getGardenLinuxReleaseName()));
			items.add(new ReplaceItem("[gardenLinuxReleaseVersion]", config.getGardenLinuxReleaseVersion()));
			items.add(new ReplaceItem("[etcdReleaseName]", config.getEtcdReleaseName()));
			items.add(new ReplaceItem("[etcdReleaseVersion]", config.getEtcdReleaseVersion()));
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
			items.add(new ReplaceItem("[diegoClientCert]", CommonUtils.lineAddSpace(config.getDiegoClientCert(),10)));
			items.add(new ReplaceItem("[diegoClientKey]", CommonUtils.lineAddSpace(config.getDiegoClientKey(),10)));
			items.add(new ReplaceItem("[diegoEncryptionKeys]", config.getDiegoEncryptionKeys()));
			items.add(new ReplaceItem("[diegoServerCert]", CommonUtils.lineAddSpace(config.getDiegoServerCert(),8)));
			items.add(new ReplaceItem("[diegoServerKey]", CommonUtils.lineAddSpace(config.getDiegoServerKey(),8)));
			//2.2 ETCD 정보
			items.add(new ReplaceItem("[etcdClientCert]", CommonUtils.lineAddSpace(config.getEtcdClientCert(),6)));
			items.add(new ReplaceItem("[etcdClientKey]", CommonUtils.lineAddSpace(config.getEtcdClientKey(),6)));
			items.add(new ReplaceItem("[etcdPeerCaCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCaCert(),6)));
			items.add(new ReplaceItem("[etcdPeerCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCert(),6)));
			items.add(new ReplaceItem("[etcdPeerKey]", CommonUtils.lineAddSpace(config.getEtcdPeerKey(),6)));
			items.add(new ReplaceItem("[etcdServerCert]", CommonUtils.lineAddSpace(config.getEtcdServerCert(),6)));
			items.add(new ReplaceItem("[etcdServerKey]", CommonUtils.lineAddSpace(config.getEtcdServerKey(),6)));
			//3.1 네트워크 정보
			items.add(new ReplaceItem("[subnetStatic]", config.getSubnetStaticFrom() + " - " + config.getSubnetStaticTo() ));
			items.add(new ReplaceItem("[subnetReserved]", config.getSubnetReservedFrom() + " - " + config.getSubnetReservedTo()));
			items.add(new ReplaceItem("[subnetRange]", config.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", config.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", config.getSubnetDns()));
			items.add(new ReplaceItem("[subnetId]", config.getSubnetId()));
			items.add(new ReplaceItem("[cloudSecurityGroups]", config.getCloudSecurityGroups()));
			//3.2 프록시 정보
			items.add(new ReplaceItem("[diegoHostKey]",  CommonUtils.lineAddSpace(config.getDiegoHostKey(),8)));
			items.add(new ReplaceItem("[diegoServers]", config.getDiegoServers()));
			items.add(new ReplaceItem("[diegoUaaSecret]", config.getDiegoUaaSecret()));
			//4 리소스 정보	
			items.add(new ReplaceItem("[boshPassword]", config.getBoshPassword()));
			items.add(new ReplaceItem("[stemcellName]", config.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", config.getStemcellVersion()));

		}
		else{
			IEDADiegoOpenstackConfig config  = openstackRepository.findOne(id);

			//1.1 기본정보	
			items.add(new ReplaceItem("[deploymentName]", config.getDeploymentName()));
			items.add(new ReplaceItem("[directorUuid]", config.getDirectorUuid()));
			items.add(new ReplaceItem("[diegoReleaseName]", config.getDiegoReleaseName()));
			items.add(new ReplaceItem("[diegoReleaseVersion]", config.getDiegoReleaseVersion()));
			items.add(new ReplaceItem("[cfReleaseName]", config.getCfReleaseName()));
			items.add(new ReplaceItem("[cfReleaseVersion]", config.getCfReleaseVersion()));
			items.add(new ReplaceItem("[gardenLinuxReleaseName]", config.getGardenLinuxReleaseName()));
			items.add(new ReplaceItem("[gardenLinuxReleaseVersion]", config.getGardenLinuxReleaseVersion()));
			items.add(new ReplaceItem("[etcdReleaseName]", config.getEtcdReleaseName()));
			items.add(new ReplaceItem("[etcdReleaseVersion]", config.getEtcdReleaseVersion()));
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
			items.add(new ReplaceItem("[diegoClientCert]", CommonUtils.lineAddSpace(config.getDiegoClientCert(),10)));
			items.add(new ReplaceItem("[diegoClientKey]", CommonUtils.lineAddSpace(config.getDiegoClientKey(),10)));
			items.add(new ReplaceItem("[diegoEncryptionKeys]", config.getDiegoEncryptionKeys()));
			items.add(new ReplaceItem("[diegoServerCert]", CommonUtils.lineAddSpace(config.getDiegoServerCert(),8)));
			items.add(new ReplaceItem("[diegoServerKey]", CommonUtils.lineAddSpace(config.getDiegoServerKey(),8)));
			//2.2 ETCD 정보
			items.add(new ReplaceItem("[etcdClientCert]", CommonUtils.lineAddSpace(config.getEtcdClientCert(),6)));
			items.add(new ReplaceItem("[etcdClientKey]", CommonUtils.lineAddSpace(config.getEtcdClientKey(),6)));
			items.add(new ReplaceItem("[etcdPeerCaCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCaCert(),6)));
			items.add(new ReplaceItem("[etcdPeerCert]", CommonUtils.lineAddSpace(config.getEtcdPeerCert(),6)));
			items.add(new ReplaceItem("[etcdPeerKey]", CommonUtils.lineAddSpace(config.getEtcdPeerKey(),6)));
			items.add(new ReplaceItem("[etcdServerCert]", CommonUtils.lineAddSpace(config.getEtcdServerCert(),6)));
			items.add(new ReplaceItem("[etcdServerKey]", CommonUtils.lineAddSpace(config.getEtcdServerKey(),6)));
			//3.1 네트워크 정보
			items.add(new ReplaceItem("[subnetStatic]", config.getSubnetStaticFrom() + " - " + config.getSubnetStaticTo() ));
			items.add(new ReplaceItem("[subnetReserved]", config.getSubnetReservedFrom() + " - " + config.getSubnetReservedTo()));
			items.add(new ReplaceItem("[subnetRange]", config.getSubnetRange()));
			items.add(new ReplaceItem("[subnetGateway]", config.getSubnetGateway()));
			items.add(new ReplaceItem("[subnetDns]", config.getSubnetDns()));
			items.add(new ReplaceItem("[cloudNetId]", config.getCloudNetId()));
			items.add(new ReplaceItem("[cloudSecurityGroups]", config.getCloudSecurityGroups()));
			//3.2 프록시 정보
			items.add(new ReplaceItem("[diegoHostKey]",  CommonUtils.lineAddSpace(config.getDiegoHostKey(),8)));
			items.add(new ReplaceItem("[diegoServers]", config.getDiegoServers()));
			items.add(new ReplaceItem("[diegoUaaSecret]", config.getDiegoUaaSecret()));
			//4 리소스 정보	
			items.add(new ReplaceItem("[boshPassword]", config.getBoshPassword()));
			items.add(new ReplaceItem("[stemcellName]", config.getStemcellName()));
			items.add(new ReplaceItem("[stemcellVersion]", config.getStemcellVersion()));
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
