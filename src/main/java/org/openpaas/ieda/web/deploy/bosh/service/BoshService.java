package org.openpaas.ieda.web.deploy.bosh.service;

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
import org.openpaas.ieda.web.deploy.bosh.dao.BoshDAO;
import org.openpaas.ieda.web.deploy.bosh.dao.BoshVO;
import org.openpaas.ieda.web.deploy.bosh.dto.BoshListDTO;
import org.openpaas.ieda.web.deploy.bosh.dto.BoshParamDTO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class BoshService {

	@Autowired private BoshDAO boshDao;
	@Autowired private CommonDAO commonDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	@Autowired private DirectorConfigDAO directorDao;

	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_BOSH"; //배포 할 플랫폼명
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String MANIFEST_TEMPLATE_PATH = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"bosh"  + SEPARATOR;
	final private static Logger LOGGER = LoggerFactory.getLogger(BoshService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 정보 목록을 조회
	 * @title               : getBoshList
	 * @return            : List<BoshListDTO>
	***************************************************/
	public List<BoshListDTO> getBoshList(String iaas){
		int recid = 0;
		List<BoshListDTO> list = new ArrayList<>();
		List<BoshVO> boshList = boshDao.selectBoshListByIaasType(iaas);
		
		if ( boshList == null ) {
			throw new CommonException("notfound.bosh.exception",
					"Bosh 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		if( !boshList.isEmpty()){
			for(BoshVO vo : boshList){
				BoshListDTO boshInfo = new BoshListDTO();
				
				boshInfo.setIaas(vo.getIaasType());
				
				//AWS
				boshInfo.setRecid(recid);
				boshInfo.setId(vo.getId());
				boshInfo.setDeploymentName(vo.getDeploymentName());
				boshInfo.setCreateDate(vo.getCreateDate());
				//OPENSTACK
				boshInfo.setRecid(recid++);
				boshInfo.setId(vo.getId());
				boshInfo.setDeploymentName(vo.getDeploymentName());
				boshInfo.setCreateDate(vo.getCreateDate());

				// BOSH
				boshInfo.setDirectorUuid(vo.getDirectorUuid());
				boshInfo.setDeploymentName(vo.getDeploymentName());
				boshInfo.setReleaseVersion(vo.getReleaseVersion());
				boshInfo.setSnapshotSchedule(vo.getSnapshotSchedule());
				boshInfo.setEnableSnapshots(vo.getEnableSnapshots());
				boshInfo.setDirectorName(vo.getDirectorName());
				boshInfo.setNtp(vo.getNtp());
				// NETWORK
				vo.setNetworks(networkDao.selectNetworkList(vo.getId(), codeVo.getCodeName()) );
				List<NetworkVO> netowrks = networkDao.selectNetworkList( vo.getId(), codeVo.getCodeName() );
				String br = "";
				int cnt = 0;
				String subnetRange , subnetGateway , subnetDns , subnetReservedIp;
				subnetRange = subnetGateway = subnetDns = subnetReservedIp = "";
				String subnetStaticIp, publicStaticIp = "";
				subnetStaticIp = publicStaticIp;
				StringBuffer subnetId = new StringBuffer();
				
				if(netowrks  != null){
					for(NetworkVO networkVO: netowrks){
						if( "internal".equals(networkVO.getNet().toLowerCase() )){
							cnt ++;
							if( cnt > 2  && cnt < netowrks.size() ){
								br = ""; 
							}else br = "<br>";
		
							subnetRange += networkVO.getSubnetRange()  + br;
							subnetGateway += networkVO.getSubnetGateway() + br;
							subnetDns += networkVO.getSubnetDns() + br;
							subnetReservedIp += (networkVO.getSubnetReservedFrom() + " - " +  networkVO.getSubnetReservedTo() + br);
							subnetStaticIp += networkVO.getSubnetStaticFrom() +" - " + networkVO.getSubnetStaticTo() + br;
							subnetId.append(networkVO.getSubnetId() + br);
						}else {
							publicStaticIp = networkVO.getSubnetStaticFrom();
						}
					}
					boshInfo.setSubnetRange(subnetRange);
					boshInfo.setSubnetGateway(subnetGateway);
					boshInfo.setSubnetDns(subnetDns);
					boshInfo.setSubnetReservedIp(subnetReservedIp);
					boshInfo.setSubnetStaticIp(subnetStaticIp);
					boshInfo.setSubnetId(subnetId.toString());
					boshInfo.setPublicStaticIp(publicStaticIp);
				}
				vo.setResource(resourceDao.selectResourceInfo( vo.getId(), codeVo.getCodeName()) );
				if(vo.getResource() != null){
				//4 리소스 정보	
					boshInfo.setStemcellName(vo.getResource().getStemcellName());
					boshInfo.setStemcellVersion(vo.getResource().getStemcellVersion());
					boshInfo.setBoshPassword(vo.getResource().getBoshPassword());
					boshInfo.setDeployStatus(vo.getDeployStatus());
					boshInfo.setDeploymentFile(vo.getDeploymentFile());
					if( !StringUtils.isEmpty( vo.getTaskId() ) )  boshInfo.setTaskId(vo.getTaskId());
				}
				list.add(boshInfo);
			}
		}
		list.stream().sorted((BoshListDTO o1, BoshListDTO o2) -> o1.getCreateDate().compareTo(o2.getCreateDate()));
		return list;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 정보 상세 조회 
	 * @title               : getBoshDetailInfo
	 * @return            : BoshVO
	***************************************************/
	public BoshVO getBoshDetailInfo(int id){
		BoshVO vo =  null;
			vo = boshDao.selectBoshDetailInfo(id);
			CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
			vo.setNetworks(networkDao.selectNetworkList(id, codeVo.getCodeName()));
			vo.setResource(resourceDao.selectResourceInfo(id, codeVo.getCodeName()));
		return vo;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 입력한 정보를 바탕으로 bosh manifest 파일 생성
	 * @title               : createSettingFile
	 * @return            : void
	***************************************************/
	public void createSettingFile(BoshVO createSettionFilevo, String test) {
		BoshVO vo = createSettionFilevo;
		String content = "";
		ManifestTemplateVO result = null;
		InputStream inputs  = null;
		try {
			
			vo = boshDao.selectBoshDetailInfo(vo.getId());
			CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
			
			vo.setNetworks(networkDao.selectNetworkList(vo.getId(), codeVo.getCodeName()) );
			vo.setResource(resourceDao.selectResourceInfo(vo.getId(),codeVo.getCodeName()));
			
			String releaseVersion = vo.getReleaseVersion().split("/")[1];
			String releaseName = vo.getReleaseVersion().split("/")[0];
			result = commonDao.getManifetTemplate(vo.getIaasType().toLowerCase(), releaseVersion, "BOSH", releaseName);
			
			ManifestTemplateVO manifestTemplate = new ManifestTemplateVO();
			if(result != null){
				inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/bosh/"+ result.getTemplateVersion() + "/"+  vo.getIaasType().toLowerCase() + "/" +result.getInputTemplate());
				content = IOUtils.toString(inputs, "UTF-8");
				
				manifestTemplate = setOptionManifestTemplateInfo(result, manifestTemplate, vo, releaseVersion );
			}else{
				throw new CommonException("notfound.bootstrap.exception",
						"해당하는 Manifest 템플릿 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
			
			List<ReplaceItemDTO> replaceItems = setReplaceBoshItems(vo, test);
			for (ReplaceItemDTO item : replaceItems) {
				if(LOGGER.isDebugEnabled()){ LOGGER.debug("info : " + item.getTargetItem()+":"+ item.getSourceItem());  }
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}
			if(LOGGER.isDebugEnabled()){ LOGGER.debug("content : " + content);  }

			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
			CommonUtils.setSpiffMerge(vo.getIaasType().toLowerCase(), vo.getId(), "bosh" ,vo.getDeploymentFile(), manifestTemplate);
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
	 * @description   : Manifest Template 정보 설정
	 * @title               : setOptionManifestTemplateInfo
	 * @return            : ManifestTemplateVO
	***************************************************/
	public ManifestTemplateVO setOptionManifestTemplateInfo(ManifestTemplateVO result, ManifestTemplateVO  manifestTemplate, BoshVO vo, String releaseVersion){
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
		//property
		if(result.getIaasPropertyTemplate() != null  && !(StringUtils.isEmpty( result.getIaasPropertyTemplate()) )){
			manifestTemplate.setIaasPropertyTemplate(MANIFEST_TEMPLATE_PATH + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getIaasPropertyTemplate() );
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
	 * @description   : 입력한 정보를 ReplaceItemDTO 목록에 넣고 createSettingFile 메소드에 응답
	 * @title               : setReplaceBoshItems
	 * @return            : List<ReplaceItemDTO>
	***************************************************/
	public List<ReplaceItemDTO> setReplaceBoshItems(BoshVO vo, String iaas) {
		
		List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();
		if("AWS".equals(vo.getIaasType()) ){
			// AWS
			items.add(new ReplaceItemDTO("[accessKeyId]", vo.getAwsAccessKeyId()));
			items.add(new ReplaceItemDTO("[secretAccessKey]", vo.getAwsSecretAccessId()));
			items.add(new ReplaceItemDTO("[availabilityZone]", vo.getAwsAvailabilityZone()));
			items.add(new ReplaceItemDTO("[region]", vo.getAwsRegion()));
			items.add(new ReplaceItemDTO("[defaultSecurityGroups]", vo.getDefaultSecurityGroups()));
			items.add(new ReplaceItemDTO("[privateKeyName]", vo.getPrivateKeyName()));
		}
		else if("OPENSTACK".equals(vo.getIaasType())){
			// Openstack
			items.add(new ReplaceItemDTO("[authUrl]", vo.getOpenstackAuthUrl()));
			items.add(new ReplaceItemDTO("[tenant]", vo.getOpenstackTenant()));
			items.add(new ReplaceItemDTO("[userName]", vo.getOpenstackUserName()));
			items.add(new ReplaceItemDTO("[apiKey]", vo.getOpenstackApiKey()));
			items.add(new ReplaceItemDTO("[defaultSecurityGroups]", vo.getDefaultSecurityGroups()));
			items.add(new ReplaceItemDTO("[privateKeyName]", vo.getPrivateKeyName()));
		}else if("VSPHERE".equals(vo.getIaasType())){
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
		}
		
		// BOSH
		items.add(new ReplaceItemDTO("[deploymentName]", vo.getDeploymentName()));
		items.add(new ReplaceItemDTO("[directorUuid]", vo.getDirectorUuid()));
		
		items.add(new ReplaceItemDTO("[releaseName]", vo.getReleaseVersion().split("/")[0]));
		items.add(new ReplaceItemDTO("[releaseVersion]", "\"" + vo.getReleaseVersion().split("/")[1] + "\""));
		
		items.add(new ReplaceItemDTO("[ntp]", vo.getNtp()));
		items.add(new ReplaceItemDTO("[enableSnapshot]", vo.getEnableSnapshots()));
		items.add(new ReplaceItemDTO("[snapshotSchedule]", vo.getSnapshotSchedule()));
		
		//network
		if("VSPHERE".equals(vo.getIaasType())){
			for(int i=0; i<vo.getNetworks().size(); i++){
				if(i==0){
					items.add(new ReplaceItemDTO("[publicSubnetRange]", vo.getNetworks().get(i).getSubnetRange()));
					items.add(new ReplaceItemDTO("[publicSubnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
					items.add(new ReplaceItemDTO("[publicSubnetDns]", vo.getNetworks().get(i).getSubnetDns()));
					items.add(new ReplaceItemDTO("[publicStaticIp]", vo.getNetworks().get(i).getSubnetStaticTo()));
					items.add(new ReplaceItemDTO("[publicNetworkName]", vo.getNetworks().get(i).getSubnetId()));
				}else if(i==1){
					items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
					items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
					items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
					items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
					items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
					items.add(new ReplaceItemDTO("[networkName]", vo.getNetworks().get(i).getSubnetId()));
				}
			}
		}else{
			for( int i=0; i<vo.getNetworks().size(); i++ ){
				if( "INTERNAL".equals(vo.getNetworks().get(i).getNet().toUpperCase()) ){
						items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
						items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
						items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
						items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
						items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
						items.add(new ReplaceItemDTO("[subnetId]", vo.getNetworks().get(i).getSubnetId()));			
				}else if( "EXTERNAL".equals(vo.getNetworks().get(i).getNet().toUpperCase())){
					items.add(new ReplaceItemDTO("[publicStaticIp]", vo.getNetworks().get(i).getSubnetStaticFrom()));
				}
			}
		}
		
		// Resource
		items.add(new ReplaceItemDTO("[stemcellName]", vo.getResource().getStemcellName()));
		items.add(new ReplaceItemDTO("[stemcellVersion]", "\"" +vo.getResource().getStemcellVersion()+ "\"" ));
		items.add(new ReplaceItemDTO("[boshPassword]", Sha512Crypt.Sha512_crypt(vo.getResource().getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		if(!"VSPHERE".equals(vo.getIaasType())){
			items.add(new ReplaceItemDTO("[smallInstanceType]", vo.getResource().getSmallFlavor()));
			items.add(new ReplaceItemDTO("[directorInstanceType]", vo.getResource().getMediumFlavor()));
		}else {
			items.add(new ReplaceItemDTO("[sInsTypeRAM]", String.valueOf(vo.getResource().getSmallRam())));
			items.add(new ReplaceItemDTO("[sInsTypeDISK]", String.valueOf(vo.getResource().getSmallDisk())));
			items.add(new ReplaceItemDTO("[sInsTypeCPU]", String.valueOf(vo.getResource().getSmallCpu())));
			items.add(new ReplaceItemDTO("[dInsTypeRAM]", String.valueOf(vo.getResource().getMediumRam())));
			items.add(new ReplaceItemDTO("[dInsTypeDISK]", String.valueOf(vo.getResource().getMediumDisk())));
			items.add(new ReplaceItemDTO("[dInsTypeCPU]", String.valueOf(vo.getResource().getMediumCpu())));
		}

		return items;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 단순 레코드 삭제 
	 * @title               : deleteBoshInfoRecord
	 * @return            : void
	***************************************************/
	@Transactional
	public void deleteBoshInfoRecord(BoshParamDTO.Delete dto) throws SQLException {
			boshDao.deleteBoshInfoRecord(Integer.parseInt(dto.getId()));
			if(dto.getId() != null){
				CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
				networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.getId()), codeVo.getCodeName());
				resourceDao.deleteResourceInfo(Integer.parseInt(dto.getId()), codeVo.getCodeName());
			}
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
			throw new CommonException("notfoundBoshInfo.director.exception",
					"기본 설치관리자가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}		
		return boshDao.selectSnapshotInfo(vo);
	}
	

}