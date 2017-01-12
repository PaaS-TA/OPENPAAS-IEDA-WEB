package org.openpaas.ieda.web.deploy.diego.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dao.CommonDAO;
import org.openpaas.ieda.web.common.dao.ManifestTemplateVO;
import org.openpaas.ieda.web.common.dto.ReplaceItemDTO;
import org.openpaas.ieda.web.common.service.CommonUtils;
import org.openpaas.ieda.web.common.service.Sha512Crypt;
import org.openpaas.ieda.web.deploy.bootstrap.service.BootstrapService;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoDAO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoListDTO;
import org.openpaas.ieda.web.deploy.diego.dto.DiegoParamDTO;
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
public class DiegoService {

	@Autowired private DiegoDAO diegoDao; 
	@Autowired private CommonDAO commonDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_DIEGO"; //배포 할 플랫폼명
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String CF_FILE  = LocalDirectoryConfiguration.getDeploymentDir()+ SEPARATOR;
	final private static String SHELLSCRIPT_FILE = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR  + "diego" + SEPARATOR;
	final private static Logger LOGGER = LoggerFactory.getLogger(BootstrapService.class);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : Diego 목록 정보를 조회
	 * @title         : getDiegoInfoList
	 * @return        : List<DiegoListDTO>
	***************************************************/
	public List<DiegoListDTO> getDiegoInfoList(String iaasType) {
		List<DiegoListDTO> diegoList = null;
		List<DiegoVO> resultList = diegoDao.selectDiegoListInfo(iaasType);
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		
		if( resultList != null ){
			diegoList = new ArrayList<>();
			int recid = 0;

			for(DiegoVO vo:resultList){
				DiegoListDTO diegoInfo = new DiegoListDTO();
				diegoInfo.setRecid(recid++);
				diegoInfo.setId(vo.getId());
				diegoInfo.setIaas(vo.getIaasType());
				diegoInfo.setCreateDate(vo.getCreateDate());
				diegoInfo.setUpdateDate(vo.getUpdateDate());

				//1.1 기본정보	
				diegoInfo.setDeploymentName(vo.getDeploymentName());
				diegoInfo.setDirectorUuid(vo.getDirectorUuid());
				diegoInfo.setDiegoReleaseName(vo.getDiegoReleaseName());
				diegoInfo.setDiegoReleaseVersion(vo.getDiegoReleaseVersion());
				diegoInfo.setCflinuxfs2rootfsreleaseName(vo.getCflinuxfs2rootfsreleaseName());
				diegoInfo.setCflinuxfs2rootfsreleaseVersion(vo.getCflinuxfs2rootfsreleaseVersion());
				diegoInfo.setCfId(vo.getCfId());
				diegoInfo.setCfDeployment(vo.getCfDeployment());
				diegoInfo.setGardenReleaseName(vo.getGardenReleaseName());
				diegoInfo.setGardenReleaseVersion(vo.getGardenReleaseVersion());
				diegoInfo.setEtcdReleaseName(vo.getEtcdReleaseName());
				diegoInfo.setEtcdReleaseVersion(vo.getEtcdReleaseVersion());
				vo.setNetworks(networkDao.selectNetworkList(vo.getId(), codeVo.getCodeName()));
				List<NetworkVO> netowrks = networkDao.selectNetworkList(vo.getId(), codeVo.getCodeName());
				String br = "";
				int cnt = 0;
				String subnetRange , subnetGateway , subnetDns , subnetReservedIp;
				subnetRange = subnetGateway = subnetDns = subnetReservedIp = "";
				String subnetStaticIp, publicStaticIp ,subnetId , cloudSecurityGroups, availabilityZone;
				subnetStaticIp = publicStaticIp = subnetId = cloudSecurityGroups= availabilityZone = "";
				
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
							subnetId += networkVO.getSubnetId() + br;
							cloudSecurityGroups += networkVO.getCloudSecurityGroups() + br;
							availabilityZone += networkVO.getAvailabilityZone() + br;
						}else {
							publicStaticIp = networkVO.getSubnetStaticFrom();
						}
					}
					diegoInfo.setSubnetRange(subnetRange);
					diegoInfo.setSubnetGateway(subnetGateway);
					diegoInfo.setSubnetDns(subnetDns);
					diegoInfo.setSubnetReservedIp(subnetReservedIp);
					diegoInfo.setSubnetStaticIp(subnetStaticIp);
					diegoInfo.setSubnetId(subnetId);
					diegoInfo.setCloudSecurityGroups(cloudSecurityGroups);
					diegoInfo.setAvailabilityZone(availabilityZone);
					diegoInfo.setPublicStaticIp(publicStaticIp);
				}
				vo.setResource(resourceDao.selectResourceInfo(vo.getId(), codeVo.getCodeName()));
				if(vo.getResource() != null){
				//4 리소스 정보	
				diegoInfo.setStemcellName(vo.getResource().getStemcellName());
				diegoInfo.setStemcellVersion(vo.getResource().getStemcellVersion());
				diegoInfo.setBoshPassword(vo.getResource().getBoshPassword());
				diegoInfo.setDeployStatus(vo.getDeployStatus());
				diegoInfo.setDeploymentFile(vo.getDeploymentFile());
				if( !StringUtils.isEmpty( vo.getTaskId() ) ) diegoInfo.setTaskId(vo.getTaskId());
				}
				diegoList.add(diegoInfo);
			}

		}

		return diegoList;
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 상세 조회  
	 * @title         : getDiegoDetailInfo
	 * @return        : DiegoVO
	***************************************************/
	public DiegoVO getDiegoDetailInfo(int id) {
		DiegoVO vo =  diegoDao.selectDiegoInfo(id);
		if( vo != null ){
			CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
			
			vo.setNetworks(networkDao.selectNetworkList(id, codeVo.getCodeName()));
			vo.setResource(resourceDao.selectResourceInfo(id, codeVo.getCodeName()));
		}
		return vo;
	}

	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 입력 정보를 바탕으로 manifest 파일 생성 및 배포 파일명 응답  
	 * @title         : createSettingFile
	 * @return        : void
	***************************************************/
	public void createSettingFile(DiegoVO vo, String iaas) {
		String content = "";
		ManifestTemplateVO result = null;
		InputStream inputs  = null;
		
		try {
			result = commonDao.selectManifetTemplate(vo.getIaasType().toLowerCase(), vo.getDiegoReleaseVersion(), "DIEGO",vo.getDiegoReleaseName());
			ManifestTemplateVO manifestTemplate = null;
			if(result != null){
				inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/diego/"+result.getTemplateVersion()+ SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR +result.getInputTemplate());
				content = IOUtils.toString(inputs, "UTF-8");
				manifestTemplate = new ManifestTemplateVO();
				manifestTemplate = setOptionManifestTemplateInfo(result, manifestTemplate, vo);
				manifestTemplate.setMinReleaseVersion(result.getTemplateVersion());
			}else {
				throw new CommonException("notFound.diego.exception",
						"해당하는 Manifest 템플릿 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}

			List<ReplaceItemDTO> replaceItems = setReplaceItems(vo);
			for (ReplaceItemDTO item : replaceItems) {
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}
			if(LOGGER.isDebugEnabled()){ LOGGER.debug("content : " + content);  }
			
			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
			
			CommonUtils.setSpiffScript("diego",  vo.getDeploymentFile(),  manifestTemplate, vo, SEPARATOR);
		} catch (IOException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		} catch(NullPointerException e){
			throw new CommonException("notFound.diego.exception", "배포 파일을 만드는데 실패하셨습니다.", HttpStatus.NOT_FOUND);
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : option Manifest 템플릿 정보 설정
	 * @title               : setOptionManifestTemplateInfo
	 * @return            : ManifestTemplateVO
	***************************************************/
	public ManifestTemplateVO setOptionManifestTemplateInfo(ManifestTemplateVO result, ManifestTemplateVO  manifestTemplate, DiegoVO vo){
		//Base Template File
		if( result.getCommonBaseTemplate() !=null && !(StringUtils.isEmpty( result.getCommonBaseTemplate())) ){
			manifestTemplate.setCommonBaseTemplate( result.getCommonBaseTemplate() );
		} else{
			manifestTemplate.setCommonBaseTemplate("");
		}
		//Job Template File
		if( result.getCommonJobTemplate()!=null && !(StringUtils.isEmpty( result.getCommonJobTemplate())) ){
			manifestTemplate.setCommonJobTemplate( result.getCommonJobTemplate());
		} else{
			manifestTemplate.setCommonJobTemplate("");
		}
		//common option Template File 
		if( result.getCommonOptionTemplate() != null  && !(StringUtils.isEmpty( result.getCommonOptionTemplate())) ){
			manifestTemplate.setCommonOptionTemplate( result.getCommonOptionTemplate() );
		} else{
			manifestTemplate.setCommonOptionTemplate("");	
		}
		//iaas Property Template File
		if( result.getIaasPropertyTemplate() != null && !(StringUtils.isEmpty( result.getIaasPropertyTemplate())) ){
			manifestTemplate.setIaasPropertyTemplate(vo.getIaasType().toLowerCase() +SEPARATOR+ result.getIaasPropertyTemplate() );
		} else{
			manifestTemplate.setIaasPropertyTemplate("");
		}
		//네트워크를 추가할 경우(2개 이상)
		if( vo.getNetworks().size() > 1 && result.getOptionNetworkTemplate() != null && !StringUtils.isEmpty( result.getOptionNetworkTemplate()) ){
			manifestTemplate.setOptionNetworkTemplate(vo.getIaasType().toLowerCase() +SEPARATOR+ result.getOptionNetworkTemplate() );
		} else{
			manifestTemplate.setOptionNetworkTemplate("");
		}
		//option resource Template File 
		if( result.getCommonOptionTemplate() != null  && !(StringUtils.isEmpty( result.getCommonOptionTemplate())) ){
			manifestTemplate.setOptionResourceTemplate( result.getOptionResourceTemplate() );
		} else{
			manifestTemplate.setOptionResourceTemplate("");	
		}
		//option etc Template File(Network 3개 일 경우)
		if( result.getOptionEtc() != null && vo.getNetworks().size() == 3 && !(StringUtils.isEmpty( result.getOptionEtc())) ){
			manifestTemplate.setOptionEtc( vo.getIaasType().toLowerCase() +SEPARATOR+ result.getOptionEtc() );
		} else{
			manifestTemplate.setOptionEtc("");	
		}
		//meta Template File
		if( result.getMetaTemplate() != null && !(StringUtils.isEmpty( result.getMetaTemplate())) ) {
			manifestTemplate.setMetaTemplate(vo.getIaasType().toLowerCase() +SEPARATOR+ result.getMetaTemplate());
		} else{
			manifestTemplate.setMetaTemplate("");	
		}
		 //임시 CF 파일 경로 + 명
		manifestTemplate.setCfTempleate( CF_FILE +vo.getCfDeployment() );  
		//shell script File
		manifestTemplate.setShellScript(SHELLSCRIPT_FILE + "generate_diego_manifest");
		
		return manifestTemplate;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
	 * @title               : setReplaceItems
	 * @return            : List<ReplaceItemDTO>
	***************************************************/
	public List<ReplaceItemDTO> setReplaceItems(DiegoVO vo) {
		
		//1.1 기본정보
		List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();	
		items.add(new ReplaceItemDTO("[diegoReleaseName]", vo.getDiegoReleaseName()));
		items.add(new ReplaceItemDTO("[diegoReleaseVersion]", "\"" + vo.getDiegoReleaseVersion() + "\""));
		items.add(new ReplaceItemDTO("[etcdReleaseName]", vo.getEtcdReleaseName()));
		items.add(new ReplaceItemDTO("[etcdReleaseVersion]", "\"" + vo.getEtcdReleaseVersion() + "\""));
		items.add(new ReplaceItemDTO("[gardenLinuxReleaseName]", vo.getGardenReleaseName()));
		items.add(new ReplaceItemDTO("[gardenLinuxReleaseVersion]", "\"" + vo.getGardenReleaseVersion() + "\""));
		if(vo.getCflinuxfs2rootfsreleaseName()!=null && !vo.getCflinuxfs2rootfsreleaseName().equals("")&&vo.getCflinuxfs2rootfsreleaseVersion()!=null && !vo.getCflinuxfs2rootfsreleaseVersion().equals("")){
			items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseName]", vo.getCflinuxfs2rootfsreleaseName()));
			items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseVersion]", ("\"" + vo.getCflinuxfs2rootfsreleaseVersion()+"\"").trim()));
		}else{
			items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseName]", "\"" + "" + "\""));
			items.add(new ReplaceItemDTO("[cflinuxfs2RootfsReleaseVersion]", "\"" + "" + "\""));
		}
		items.add(new ReplaceItemDTO("[cadvisorDriverIp]", vo.getCadvisorDriverIp()));
		items.add(new ReplaceItemDTO("[cadvisorDriverPort]", vo.getCadvisorDriverPort()));
		// 2. 네트워크 정보
		for( int i=0; i<vo.getNetworks().size(); i++ ){
			if( "INTERNAL".equals(vo.getNetworks().get(i).getNet().toUpperCase())){
				if( !vo.getIaasType().toUpperCase().equals("VSPHERE") ){//aws or openstack
					if(i  == 0 ){
						items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
						items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
						items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
						items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
						items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
						items.add(new ReplaceItemDTO("[subnetId]", vo.getNetworks().get(i).getSubnetId()));			
						items.add(new ReplaceItemDTO("[cloudSecurityGroups]", vo.getNetworks().get(i).getCloudSecurityGroups()));
						items.add(new ReplaceItemDTO("[availabilityZone]", vo.getNetworks().get(i).getAvailabilityZone()));
					}else if(i > 0){
						items.add(new ReplaceItemDTO("[subnetRange"+i+"]", vo.getNetworks().get(i).getSubnetRange()));
						items.add(new ReplaceItemDTO("[subnetGateway"+i+"]", vo.getNetworks().get(i).getSubnetGateway()));
						items.add(new ReplaceItemDTO("[subnetDns"+i+"]", vo.getNetworks().get(i).getSubnetDns()));
						items.add(new ReplaceItemDTO("[subnetReserved"+i+"]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
						items.add(new ReplaceItemDTO("[subnetStatic"+i+"]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
						items.add(new ReplaceItemDTO("[subnetId"+i+"]", vo.getNetworks().get(i).getSubnetId()));			
						items.add(new ReplaceItemDTO("[cloudSecurityGroups"+i+"]", vo.getNetworks().get(i).getCloudSecurityGroups()));
						items.add(new ReplaceItemDTO("[availabilityZone"+i+"]", vo.getNetworks().get(i).getAvailabilityZone()));
					}
				}else if(vo.getIaasType().toUpperCase().equals("VSPHERE")){
					if(i == 0){
						items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
						items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
						items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
						items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
						items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
						items.add(new ReplaceItemDTO("[subnetId]", vo.getNetworks().get(i).getSubnetId()));			
					}else if(i > 0){
						items.add(new ReplaceItemDTO("[subnetRange"+i+"]", vo.getNetworks().get(i).getSubnetRange()));
						items.add(new ReplaceItemDTO("[subnetGateway"+i+"]", vo.getNetworks().get(i).getSubnetGateway()));
						items.add(new ReplaceItemDTO("[subnetDns"+i+"]", vo.getNetworks().get(i).getSubnetDns()));
						items.add(new ReplaceItemDTO("[subnetReserved"+i+"]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
						items.add(new ReplaceItemDTO("[subnetStatic"+i+"]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
						items.add(new ReplaceItemDTO("[subnetId"+i+"]", vo.getNetworks().get(i).getSubnetId()));			
					}
				}
			}
		} 
		if( vo.getNetworks().size() == 1  ){
			//network1
			items.add(new ReplaceItemDTO("[subnetRange1]", ""));
			items.add(new ReplaceItemDTO("[subnetGateway1]", ""));
			items.add(new ReplaceItemDTO("[subnetDns1]", ""));
			items.add(new ReplaceItemDTO("[subnetReserved1]", ""));
			items.add(new ReplaceItemDTO("[subnetStatic1]", ""));
			items.add(new ReplaceItemDTO("[subnetId1]", ""));			
			//network2
			items.add(new ReplaceItemDTO("[subnetRange2]", ""));
			items.add(new ReplaceItemDTO("[subnetGateway2]", ""));
			items.add(new ReplaceItemDTO("[subnetDns2]", ""));
			items.add(new ReplaceItemDTO("[subnetReserved2]", ""));
			items.add(new ReplaceItemDTO("[subnetStatic2]", ""));
			items.add(new ReplaceItemDTO("[subnetId2]", ""));			
			
			items.add(new ReplaceItemDTO("[cloudSecurityGroups1]", ""));	
			items.add(new ReplaceItemDTO("[cloudSecurityGroups2]", ""));
			
			items.add(new ReplaceItemDTO("[availabilityZone1]", ""));	
			items.add(new ReplaceItemDTO("[availabilityZone2]", ""));
		}else if( vo.getNetworks().size() > 1 ){
			items.add(new ReplaceItemDTO("[subnetRange2]", ""));
			items.add(new ReplaceItemDTO("[subnetGateway2]", ""));
			items.add(new ReplaceItemDTO("[subnetDns2]", ""));
			items.add(new ReplaceItemDTO("[subnetReserved2]", ""));
			items.add(new ReplaceItemDTO("[subnetStatic2]", ""));
			items.add(new ReplaceItemDTO("[subnetId2]", ""));			
			items.add(new ReplaceItemDTO("[cloudSecurityGroups2]", ""));
			items.add(new ReplaceItemDTO("[availabilityZone2]", ""));
		}
				
		//3.리소스 정보
		items.add(new ReplaceItemDTO("[stemcellName]", vo.getResource().getStemcellName() ));
		items.add(new ReplaceItemDTO("[stemcellVersion]", "\"" + vo.getResource().getStemcellVersion() + "\"" ));	
		items.add(new ReplaceItemDTO("[boshPassword]", Sha512Crypt.Sha512_crypt(vo.getResource().getBoshPassword(), RandomStringUtils.randomAlphabetic(10), 0)));
		
		if("VSPHERE".equals(vo.getIaasType().toUpperCase())){
			//small Flavor
			items.add(new ReplaceItemDTO("[sInsTypeCPU]",  String.valueOf(vo.getResource().getSmallCpu())));
			items.add(new ReplaceItemDTO("[sInsTypeRAM]", String.valueOf(vo.getResource().getSmallRam())));
			items.add(new ReplaceItemDTO("[sInsTypeDISK]", String.valueOf(vo.getResource().getSmallDisk())));
			//medium Flavor
			items.add(new ReplaceItemDTO("[mInsTypeCPU]",  String.valueOf(vo.getResource().getMediumCpu())));
			items.add(new ReplaceItemDTO("[mInsTypeRAM]", String.valueOf(vo.getResource().getMediumRam())));
			items.add(new ReplaceItemDTO("[mInsTypeDISK]", String.valueOf(vo.getResource().getMediumDisk())));
			//large Flavor
			items.add(new ReplaceItemDTO("[lInsTypeCPU]",  String.valueOf(vo.getResource().getLargeCpu())));
			items.add(new ReplaceItemDTO("[lInsTypeRAM]", String.valueOf(vo.getResource().getLargeRam())));
			items.add(new ReplaceItemDTO("[lInsTypeDISK]", String.valueOf(vo.getResource().getLargeDisk())));
			//runner Flavor
			items.add(new ReplaceItemDTO("[rInsTypeCPU]",  String.valueOf(vo.getResource().getRunnerCpu())));
			items.add(new ReplaceItemDTO("[rInsTypeRAM]", String.valueOf(vo.getResource().getRunnerRam())));
			items.add(new ReplaceItemDTO("[rInsTypeDISK]", String.valueOf(vo.getResource().getRunnerDisk())));
		}else{
			items.add(new ReplaceItemDTO("[smallInstanceType]", vo.getResource().getSmallFlavor()));
			items.add(new ReplaceItemDTO("[mediumInstanceType]", vo.getResource().getMediumFlavor()));
			items.add(new ReplaceItemDTO("[largeInstanceType]", vo.getResource().getLargeFlavor()));
			items.add(new ReplaceItemDTO("[cellInstanceType]", vo.getResource().getRunnerFlavor()));
		}
		
		return items;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 단순 레코드 삭제
	 * @title               : deleteDiegoInfoRecord
	 * @return            : void
	***************************************************/
	@Transactional
	public void deleteDiegoInfoRecord(DiegoParamDTO.Delete dto) throws SQLException {
		diegoDao.deleteDiegoInfoRecord(Integer.parseInt(dto.getId()));
		if( dto.getId() != null ){
			CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
			networkDao.deleteNetworkInfoRecord(Integer.parseInt(dto.getId()), codeVo.getCodeName());
			resourceDao.deleteResourceInfo(Integer.parseInt(dto.getId()), codeVo.getCodeName());
		}
	}

}
