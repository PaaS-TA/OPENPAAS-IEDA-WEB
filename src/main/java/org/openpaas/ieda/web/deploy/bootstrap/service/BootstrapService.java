package org.openpaas.ieda.web.deploy.bootstrap.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dao.CommonDAO;
import org.openpaas.ieda.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.web.common.dto.ReplaceItemDTO;
import org.openpaas.ieda.web.common.service.CommonUtils;
import org.openpaas.ieda.web.common.service.Sha512Crypt;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapDAO;
import org.openpaas.ieda.web.deploy.bootstrap.dao.BootstrapVO;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootStrapParamDTO.Delete;
import org.openpaas.ieda.web.deploy.bootstrap.dto.BootstrapListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BootstrapService {

	@Autowired private BootstrapDAO bootStrapDao;
	@Autowired private CommonDAO commonDao;
	@Autowired private DirectorConfigDAO directorDao;
	
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String PRIVATE_KEY_PATH = System.getProperty("user.home") + SEPARATOR + ".ssh" + SEPARATOR;
	final private static String MANIFEST_TEMPLATE_PATH = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"bootstrap"  + SEPARATOR;
	final private static Logger LOGGER = LoggerFactory.getLogger(BootstrapService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 목록 조회
	 * @title               : bootstrapList
	 * @return            : List<BootstrapListDTO>
	***************************************************/
	public List<BootstrapListDTO> bootstrapList() {
		List<BootstrapVO> bootstrapConfigsList = bootStrapDao.selectBootstrapList();
		List<BootstrapListDTO> listDtos = new ArrayList<>();
		int recid =0;
		
		if(!bootstrapConfigsList.isEmpty()){
			
			for(BootstrapVO vo :bootstrapConfigsList){
				BootstrapListDTO dto = new BootstrapListDTO();
				dto.setRecid(recid++);
				dto.setId(vo.getId());
				
				dto.setDeployStatus(vo.getDeployStatus());
				dto.setDeploymentName(vo.getDeploymentName());
				dto.setDirectorName(vo.getDirectorName());
				dto.setIaas(vo.getIaasType());
				dto.setBoshRelease(vo.getBoshRelease());
				dto.setBoshCpiRelease(vo.getBoshCpiRelease());
				dto.setSubnetId(vo.getSubnetId());
				dto.setSubnetRange(vo.getSubnetRange());
				dto.setPublicStaticIp(vo.getPublicStaticIp());
				dto.setPrivateStaticIp(vo.getPrivateStaticIp());
				dto.setSubnetGateway(vo.getSubnetGateway());
				dto.setSubnetDns(vo.getSubnetDns());
				dto.setNtp(vo.getNtp());
				dto.setStemcell(vo.getStemcell());
				dto.setInstanceType(vo.getCloudInstanceType());
				dto.setBoshPassword(vo.getBoshPassword());
				dto.setDeploymentFile(vo.getDeploymentFile());
				dto.setDeployLog(vo.getDeployLog());
				
				dto.setCreateDate(vo.getCreateDate());
				dto.setUpdateDate(vo.getUpdateDate());

				
				listDtos.add(dto);
			}
		}
		return listDtos;
	}


	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 수정 시 정보 상세 조회
	 * @title               : getBootstrapInfo
	 * @return            : BootstrapVO
	***************************************************/
	public BootstrapVO getBootstrapInfo(int id) {
		BootstrapVO vo =  null;
		vo = bootStrapDao.selectBootstrapInfo(id);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("RESULT [\n"+vo+ "\n]" );
		}
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 사용 여부
	 * @title               : getSnapshotInfo
	 * @return            : int
	***************************************************/
	public int getSnapshotInfo(){
		DirectorConfigVO vo = directorDao.selectDirectorConfigByDefaultYn("Y");
		if ( vo == null ) {
			throw new CommonException("notfound.bootstrap.exception",
					"기본 설치관리자가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}		
		return bootStrapDao.selectSnapshotInfo(vo);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 입력 정보를 바탕으로 manifest 생성
	 * @title               : createSettingFile
	 * @return            : void
	***************************************************/
	public void createSettingFile(BootstrapVO CreateSettingFilevo, String testFlag) {
		BootstrapVO vo = CreateSettingFilevo;
		String content = "";
		ManifestTemplateVO result = null;
		InputStream inputs  = null;
		try {
			if(!("test".equals(testFlag))){
				vo = bootStrapDao.selectBootstrapInfo(vo.getId());
			}
			String boshRelease = vo.getBoshRelease();
			if( boshRelease.contains(".tgz") ){
				boshRelease = boshRelease.replace(".tgz", "");
			}
			String releaseVersion = vo.getBoshRelease().replaceAll("[^0-9]", "");
			String releaseName = boshRelease.replaceAll("[^A-Za-z]", "");
			result = commonDao.selectManifetTemplate(vo.getIaasType(), releaseVersion, "BOOTSTRAP", releaseName );

			ManifestTemplateVO manifestTemplate = new ManifestTemplateVO();
			if(result != null){
				inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/bootstrap/" + result.getTemplateVersion() + "/"+ vo.getIaasType().toLowerCase() + "/" +result.getInputTemplate());
				content = IOUtils.toString(inputs, "UTF-8");
				manifestTemplate = setOptionManifestTemplateInfo(result, manifestTemplate, vo, releaseVersion );
			}else {
				throw new CommonException("notSupportedRelease.bootstrap.exception",
						"지원하지 않는 릴리즈 또는 릴리즈 버전입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			List<ReplaceItemDTO> replaceItems = makeReplaceItems(vo, vo.getIaasType());
			for (ReplaceItemDTO item : replaceItems) {
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}
			
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
			CommonUtils.setSpiffMerge(vo.getIaasType().toLowerCase(), vo.getId(), "microbosh", vo.getDeploymentFile(),  manifestTemplate);
		} catch (IOException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		} catch(NullPointerException e){
			if( vo == null){
				throw new CommonException("notfound.bootstrap.exception",
						"해당하는 배포 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest template 체크
	 * @title               : setOptionManifestTemplateInfo
	 * @return            : ManifestTemplateVO
	***************************************************/
	public ManifestTemplateVO setOptionManifestTemplateInfo(ManifestTemplateVO result, ManifestTemplateVO  manifestTemplate, BootstrapVO vo, String releaseVersion){
		//base
		if(result.getCommonBaseTemplate() != null  && !(StringUtils.isEmpty( result.getCommonBaseTemplate()) )){
			manifestTemplate.setCommonBaseTemplate( MANIFEST_TEMPLATE_PATH +  result.getTemplateVersion()  + SEPARATOR  + "common" + SEPARATOR  + result.getCommonBaseTemplate());
		}else{
			manifestTemplate.setCommonBaseTemplate("");
		}
		//job
		if(result.getCommonJobTemplate() != null && !(StringUtils.isEmpty( result.getCommonJobTemplate()) )){
			manifestTemplate.setCommonJobTemplate(  MANIFEST_TEMPLATE_PATH +  result.getTemplateVersion()  + SEPARATOR + "common"+ SEPARATOR  +  result.getCommonJobTemplate() );
		}else{
			manifestTemplate.setCommonJobTemplate("");
		}
		//iaasProperty
		if(result.getIaasPropertyTemplate() != null && !(StringUtils.isEmpty( result.getIaasPropertyTemplate()) )){
			manifestTemplate.setIaasPropertyTemplate(  MANIFEST_TEMPLATE_PATH +  result.getTemplateVersion()  + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR  +  result.getIaasPropertyTemplate() );
		}else{
			manifestTemplate.setIaasPropertyTemplate("");
		}
		//meta
		if(result.getMetaTemplate() != null && !(StringUtils.isEmpty( result.getMetaTemplate()) )){
			manifestTemplate.setMetaTemplate(MANIFEST_TEMPLATE_PATH +  result.getTemplateVersion()  + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR  +  result.getMetaTemplate());
		}else{
			manifestTemplate.setMetaTemplate("");
		}
		return manifestTemplate;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 화면에 입력한 값을 template 파일과 Replace 하기위해 iaas별 각 항목 설정
	 * @title               : makeReplaceItems
	 * @return            : List<ReplaceItemDTO>
	***************************************************/
	public List<ReplaceItemDTO> makeReplaceItems(BootstrapVO vo, String iaas) {
		
		List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();
		
		//AWS
		if("AWS".equals(iaas.toUpperCase()) ){
			items.add(new ReplaceItemDTO("[accessKeyId]", vo.getAwsAccessKeyId()));
			items.add(new ReplaceItemDTO("[secretAccessId]", vo.getAwsSecretAccessId()));
			items.add(new ReplaceItemDTO("[region]", vo.getAwsRegion()));
			items.add(new ReplaceItemDTO("[defaultSecurityGroups]", vo.getDefaultSecurityGroups()));
			items.add(new ReplaceItemDTO("[availabilityZone]", vo.getAwsAvailabilityZone()));
			items.add(new ReplaceItemDTO("[privateKeyName]", vo.getPrivateKeyName()));
			items.add(new ReplaceItemDTO("[privateKeyPath]", PRIVATE_KEY_PATH +  vo.getPrivateKeyPath()));
			
			items.add(new ReplaceItemDTO("[cpiReleaseName]", "bosh-aws-cpi"));
			items.add(new ReplaceItemDTO("[cpiName]", "aws_cpi"));
		
		//OPENSTACK
		}else if("OPENSTACK".equals(iaas.toUpperCase())){
			items.add(new ReplaceItemDTO("[authUrl]", vo.getOpenstackAuthUrl()));
			items.add(new ReplaceItemDTO("[tenant]", vo.getOpenstackTenant()));
			items.add(new ReplaceItemDTO("[userName]", vo.getOpenstackUserName()));
			items.add(new ReplaceItemDTO("[apiKey]", vo.getOpenstackApiKey()));
			items.add(new ReplaceItemDTO("[privateKeyName]", vo.getPrivateKeyName()));
			items.add(new ReplaceItemDTO("[defaultSecurityGroup]", vo.getDefaultSecurityGroups()));
			items.add(new ReplaceItemDTO("[privateKeyPath]", PRIVATE_KEY_PATH +  vo.getPrivateKeyPath()));
			
			items.add(new ReplaceItemDTO("[cpiName]", "openstack_cpi"));
			items.add(new ReplaceItemDTO("[cpiReleaseName]", "bosh-openstack-cpi"));
		
		//vSphere
		}else if("VSPHERE".equals(iaas.toUpperCase())){
			items.add(new ReplaceItemDTO("[vCenterAddress]", vo.getvCenterAddress()));
			items.add(new ReplaceItemDTO("[vCenterUser]", vo.getvCenterUser()));
			items.add(new ReplaceItemDTO("[vCenterPassword]", vo.getvCenterPassword()));
			items.add(new ReplaceItemDTO("[vCenterName]", vo.getvCenterDatacenterName()));
			items.add(new ReplaceItemDTO("[vCenterVMFolder]", vo.getvCenterVMFolder()));
			items.add(new ReplaceItemDTO("[vCenterTemplateFolder]", vo.getvCenterTemplateFolder()));
			items.add(new ReplaceItemDTO("[vCenterDatastore]", vo.getvCenterDatastore()));
			items.add(new ReplaceItemDTO("[vCenterPersistentDatastore]", vo.getvCenterPersistentDatastore()));
			items.add(new ReplaceItemDTO("[vCenterDiskPath]", vo.getvCenterDiskPath()));
			items.add(new ReplaceItemDTO("[vCenterCluster]", vo.getvCenterCluster()));
			
			items.add(new ReplaceItemDTO("[cpiName]", "vsphere_cpi"));
			items.add(new ReplaceItemDTO("[cpiReleaseName]", "bosh-vsphere-cpi"));
		}
		
		//Default Info
		items.add(new ReplaceItemDTO("[deploymentName]", vo.getDeploymentName()));
		items.add(new ReplaceItemDTO("[directorName]", vo.getDirectorName()));
		items.add(new ReplaceItemDTO("[boshRelease]", LocalDirectoryConfiguration.getReleaseDir() + SEPARATOR + vo.getBoshRelease()));
		items.add(new ReplaceItemDTO("[boshCpiRelease]", LocalDirectoryConfiguration.getReleaseDir() + SEPARATOR + vo.getBoshCpiRelease()));
		items.add(new ReplaceItemDTO("[enableSnapshot]", vo.getEnableSnapshots()));
		items.add(new ReplaceItemDTO("[snapshotSchedule]", vo.getSnapshotSchedule()));
		items.add(new ReplaceItemDTO("[ntp]", vo.getNtp()));
		
		//Network Info
		if("AWS".equals(iaas.toUpperCase()) || "OPENSTACK".equals(iaas.toUpperCase()) ){ 
			//Internal
			items.add(new ReplaceItemDTO("[privateStaticIp]", vo.getPrivateStaticIp()));
			items.add(new ReplaceItemDTO("[subnetId]", vo.getSubnetId()));
			items.add(new ReplaceItemDTO("[networkName]", vo.getSubnetId()));
			items.add(new ReplaceItemDTO("[subnetRange]", vo.getSubnetRange() ));
			items.add(new ReplaceItemDTO("[subnetGateway]", vo.getSubnetGateway()));
			items.add(new ReplaceItemDTO("[subnetDns]", vo.getSubnetDns()));
			
			//External
			items.add(new ReplaceItemDTO("[publicStaticIp]", vo.getPublicStaticIp()));
		}else if("VSPHERE".equals(iaas.toUpperCase()) ){ 
			
			//Internal
			items.add(new ReplaceItemDTO("[privateStaticIp]", vo.getPrivateStaticIp()));
			items.add(new ReplaceItemDTO("[networkName]", vo.getSubnetId()));
			items.add(new ReplaceItemDTO("[subnetRange]", vo.getSubnetRange() ));
			items.add(new ReplaceItemDTO("[subnetGateway]", vo.getSubnetGateway()));
			items.add(new ReplaceItemDTO("[subnetDns]", vo.getSubnetDns()));
			
			//External
			items.add(new ReplaceItemDTO("[publicStaticIp]", vo.getPublicStaticIp()));
			items.add(new ReplaceItemDTO("[publicNetworkName]", vo.getPublicSubnetId()));
			items.add(new ReplaceItemDTO("[publicSubnetRange]", vo.getPublicSubnetRange() ));
			items.add(new ReplaceItemDTO("[publicSubnetGateway]", vo.getPublicSubnetGateway()));
			items.add(new ReplaceItemDTO("[publicSubnetDns]", vo.getPublicSubnetDns()));
		}
		

		//Resource Info
		if("AWS".equals(iaas.toUpperCase()) || "OPENSTACK".equals(iaas.toUpperCase()) ){ 
			items.add(new ReplaceItemDTO("[cloudInstanceType]", vo.getCloudInstanceType()));
		}else{
			items.add(new ReplaceItemDTO("[resourcePoolCPU]", vo.getResourcePoolCpu()));
			items.add(new ReplaceItemDTO("[resourcePoolRAM]", vo.getResourcePoolRam()));
			items.add(new ReplaceItemDTO("[resourcePoolDisk]", vo.getResourcePoolDisk()));
		}
		items.add(new ReplaceItemDTO("[stemcell]", LocalDirectoryConfiguration.getStemcellDir() + SEPARATOR + vo.getStemcell()));
		items.add(new ReplaceItemDTO("[boshPassword]", Sha512Crypt.Sha512_crypt(vo.getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		
		return items;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 단순 레코드 삭제
	 * @title               : deleteBootstrapInfoRecord
	 * @return            : void
	***************************************************/
	@Transactional
	public Boolean deleteBootstrapInfoRecord(Delete dto) throws SQLException{
		Boolean check = true;
		BootstrapVO vo = bootStrapDao.selectBootstrapInfo(Integer.parseInt(dto.getId()));
		if( vo != null  ){
			bootStrapDao.deleteBootstrapInfo(Integer.parseInt(dto.getId()));
		}
		File lockFile = new File("bootstrap.lock");
		if( lockFile.exists() ){
			check = lockFile.delete();
		}
		return check;
	}
}