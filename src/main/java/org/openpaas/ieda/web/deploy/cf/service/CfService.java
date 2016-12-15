package org.openpaas.ieda.web.deploy.cf.service;

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
import org.openpaas.ieda.web.deploy.bootstrap.service.BootstrapService;
import org.openpaas.ieda.web.deploy.cf.dao.CfDAO;
import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.cf.dto.CfListDTO;
import org.openpaas.ieda.web.deploy.cf.dto.CfParamDTO;
import org.openpaas.ieda.web.deploy.common.dao.key.KeyDAO;
import org.openpaas.ieda.web.deploy.common.dao.key.KeyVO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkDAO;
import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceDAO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceVO;
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
public class CfService {

	@Autowired private CfDAO cfDao;
	@Autowired private CommonDAO commonDao;
	@Autowired private NetworkDAO networkDao;
	@Autowired private KeyDAO keyDao;
	@Autowired private ResourceDAO resourceDao;
	@Autowired private CommonCodeDAO commonCodeDao;
	
	final private static String PARENT_CODE="1000"; //배포 코드
	final private static String SUB_GROUP_CODE="1100"; //배포 유형 코드
	final private static String CODE_NAME="DEPLOY_TYPE_CF"; //배포 할 플랫폼명
	final private static String SEPARATOR = System.getProperty("file.separator");
	final private static String MANIFEST_TEMPLATE_LOCATION = LocalDirectoryConfiguration.getManifastTemplateDir() + SEPARATOR +"cf" + SEPARATOR;
	final private static Logger LOGGER = LoggerFactory.getLogger(BootstrapService.class);
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 정보 목록 전체 조회  
	 * @title               : getCfLIst
	 * @return            : List<CfListDTO>
	***************************************************/
	public List<CfListDTO> getCfLIst(String iaas, String platform) {
		List<CfListDTO> cfList = null;

		List<CfVO> listCf  = cfDao.selectCfList(iaas, platform);
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);

		if( listCf != null){
			cfList = new ArrayList<>();
			
			int recid = 0;
			for( CfVO vo : listCf ){

				CfListDTO cfInfo = new CfListDTO();
				cfInfo.setRecid(recid++);
				cfInfo.setId(vo.getId());
				cfInfo.setIaas(vo.getIaasType());

				cfInfo.setCreateDate(vo.getCreateDate());
				cfInfo.setUpdateDate(vo.getUpdateDate());
				cfInfo.setDiegoYn(vo.getDiegoYn());
				cfInfo.setDeploymentName(vo.getDeploymentName());
				cfInfo.setDirectorUuid(vo.getDirectorUuid());
				cfInfo.setReleaseName(vo.getReleaseName());
				cfInfo.setReleaseVersion(vo.getReleaseVersion());
				cfInfo.setAppSshFingerprint(vo.getAppSshFingerprint());

				cfInfo.setDomain(vo.getDomain());
				cfInfo.setDescription(vo.getDescription());
				cfInfo.setDomainOrganization(vo.getDomainOrganization());

				cfInfo.setProxyStaticIps(vo.getProxyStaticIps());

				//NETWORK
				List<NetworkVO> netowrks = networkDao.selectNetworkList(vo.getId(),  codeVo.getCodeName());
				String br = "";
				int cnt = 0;
				String subnetRange , subnetGateway , subnetDns , subnetReservedIp;
				subnetRange = subnetGateway = subnetDns = subnetReservedIp = "";
				String subnetStaticIp ,subnetId , cloudSecurityGroups;
				subnetStaticIp  = subnetId = cloudSecurityGroups=  "";
				
				if(netowrks  != null){
					for(NetworkVO networkVO: netowrks){
						if( "internal".equals(networkVO.getNet().toLowerCase() )){
							cnt ++;
							if( cnt > 1  && cnt < netowrks.size() ){
								br = ""; 
							}else br = "<br>";
		
							subnetRange += networkVO.getSubnetRange()  + br;
							subnetGateway += networkVO.getSubnetGateway() + br;
							subnetDns += networkVO.getSubnetDns() + br;
							subnetReservedIp += (networkVO.getSubnetReservedFrom() + " - " +  networkVO.getSubnetReservedTo() + br);
							subnetStaticIp += networkVO.getSubnetStaticFrom() +" - " + networkVO.getSubnetStaticTo() + br;
							subnetId += networkVO.getSubnetId() + br;
							cloudSecurityGroups += networkVO.getCloudSecurityGroups() + br;
						}
					}
					cfInfo.setSubnetRange(subnetRange);
					cfInfo.setSubnetGateway(subnetGateway);
					cfInfo.setSubnetDns(subnetDns);
					cfInfo.setSubnetReservedIp(subnetReservedIp);
					cfInfo.setSubnetStaticIp(subnetStaticIp);
					cfInfo.setSubnetId(subnetId);
					cfInfo.setCloudSecurityGroups(cloudSecurityGroups);
				}
				
				//Resource
				ResourceVO resource = resourceDao.selectResourceInfo(vo.getId(),  codeVo.getCodeName());
				if( resource != null ){
					cfInfo.setStemcellName(resource.getStemcellName());
					cfInfo.setStemcellVersion(resource.getStemcellVersion());
					cfInfo.setBoshPassword(resource.getBoshPassword());
				}

				cfInfo.setDeployStatus(vo.getDeployStatus());
				cfInfo.setDeploymentFile(vo.getDeploymentFile());
				if( !StringUtils.isEmpty( vo.getTaskId() ) ) cfInfo.setTaskId(vo.getTaskId());

				cfList.add(cfInfo);
			}
		}
		return cfList;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 정보 상세 조회
	 * @title               : getCfInfo
	 * @return            : CfVO
	***************************************************/
	public CfVO getCfInfo(int id) {
		CfVO vo = null;
		try{
			vo = cfDao.selectCfInfoById(id);
			CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
			
			vo.setNetworks(networkDao.selectNetworkList(id,  codeVo.getCodeName()) );
			vo.setKeys(keyDao.selectKeyInfoLIst(id, codeVo.getCodeName()) );
			vo.setResource(resourceDao.selectResourceInfo(id,  codeVo.getCodeName()));
		} catch (NullPointerException e){
			throw new CommonException("notfound.cf.exception",
					"해당하는 CF가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		return vo;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 입력 정보를 바탕으로 manifest 파일 생성
	 * @title               : createSettingFile
	 * @return            : void
	***************************************************/
	public void createSettingFile(CfVO vo, String test) {

		String content = "";
		ManifestTemplateVO result = null;
		InputStream inputs  = null;
		
		try {
			//1. get Manifest Template info
			result = commonDao.getManifetTemplate(vo.getIaasType(), vo.getReleaseVersion(), "CF", vo.getReleaseName());
			
			ManifestTemplateVO manifestTemplate = null;
			if(result != null){
				inputs =  this.getClass().getClassLoader().getResourceAsStream("static/deploy_template/cf/"+ result.getTemplateVersion()  + "/" + vo.getIaasType().toLowerCase() + "/" +result.getInputTemplate());
				content = IOUtils.toString(inputs, "UTF-8");
				
				manifestTemplate = new ManifestTemplateVO();
				manifestTemplate = setOptionManifestTemplateInfo(result, manifestTemplate, vo);
			}else {
				throw new CommonException("notFound.cfRelease.exception",
						"지원하지 않는 릴리즈 또는 릴리즈 버전입니다.", HttpStatus.NOT_FOUND);
			}

			List<ReplaceItemDTO> replaceItems = setReplaceItems(vo, vo.getIaasType());
			for (ReplaceItemDTO item : replaceItems) {
				content = content.replace(item.getTargetItem(), item.getSourceItem());
			}

			IOUtils.write(content, new FileOutputStream(LocalDirectoryConfiguration.getTempDir() + SEPARATOR + vo.getDeploymentFile()), "UTF-8");
			CommonUtils.setSpiffMerge(vo.getIaasType().toLowerCase(), vo.getId(), "cf",  vo.getDeploymentFile(),  manifestTemplate);
		} catch (IOException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
			throw new CommonException("notfound.cfManifestTemplate.exception",
					"Manifest Template 파일 로드 중 오류가 발생했습니다.", HttpStatus.NOT_ACCEPTABLE);
		} catch(NullPointerException e){
			if( vo == null){
				throw new CommonException("notfound.cf.exception",
						"해당하는 배포 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}else if( result == null ){
				throw new CommonException("notfound.cf.exception",
						"지원하지 않는 릴리즈 또는 릴리즈 버전입니다.", HttpStatus.NOT_FOUND);
			}
		} 
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Option Manifest 템플릿 정보 설정
	 * @title               : setOptionManifestTemplateInfo
	 * @return            : ManifestTemplateVO
	***************************************************/
	public ManifestTemplateVO setOptionManifestTemplateInfo(ManifestTemplateVO result, ManifestTemplateVO  manifestTemplate, CfVO vo){
		//Base Template File
		if(result.getCommonBaseTemplate() != null && !(StringUtils.isEmpty( result.getCommonBaseTemplate()) )){
			manifestTemplate.setCommonBaseTemplate( MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR  +  result.getCommonBaseTemplate());
		}else{
			manifestTemplate.setCommonBaseTemplate("");
		}
		//Job Template File
		if(result.getCommonJobTemplate() != null && !(StringUtils.isEmpty( result.getCommonJobTemplate()) )){
			manifestTemplate.setCommonJobTemplate(  MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR  +  result.getCommonJobTemplate() );
		}else{
			manifestTemplate.setCommonJobTemplate("");
		}
		//meta Template File
		if(result.getMetaTemplate() != null  && !(StringUtils.isEmpty( result.getMetaTemplate()) )){
			manifestTemplate.setMetaTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR  +  result.getMetaTemplate());
		}else{
			manifestTemplate.setMetaTemplate("");
		}
		//iaas Property Template File
		if(result.getIaasPropertyTemplate() != null  && !(StringUtils.isEmpty( result.getIaasPropertyTemplate()) )){
			manifestTemplate.setIaasPropertyTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getIaasPropertyTemplate() );
		}else{
			manifestTemplate.setIaasPropertyTemplate("");
		}
		//네트워크를 추가할 경우(2개 이상)
		if( vo.getNetworks().size() >2 && result.getOptionNetworkTemplate() != null  && !(StringUtils.isEmpty( result.getOptionNetworkTemplate()) )){
			manifestTemplate.setOptionNetworkTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getOptionNetworkTemplate() );
		}else{
			manifestTemplate.setOptionNetworkTemplate("");
		}
		//resource Template File 
		if( result.getOptionResourceTemplate() != null && !(StringUtils.isEmpty( result.getOptionResourceTemplate())) ){
			manifestTemplate.setOptionResourceTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getOptionResourceTemplate() );
		}else{
			manifestTemplate.setOptionResourceTemplate("");
		}
		//diego use Template File
		if( "true".equals(vo.getDiegoYn().toLowerCase()) && result.getCommonOptionTemplate() != null  && !(StringUtils.isEmpty( result.getCommonOptionTemplate()) )){
			manifestTemplate.setCommonOptionTemplate(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + "common" + SEPARATOR + result.getCommonOptionTemplate() );
		}else{
			manifestTemplate.setCommonOptionTemplate("");
		}
		//option etc Template File
		if(result.getOptionEtc() != null  && !(StringUtils.isEmpty( result.getOptionEtc()) )){
			manifestTemplate.setOptionEtc(MANIFEST_TEMPLATE_LOCATION + result.getTemplateVersion() + SEPARATOR + vo.getIaasType().toLowerCase() + SEPARATOR + result.getOptionEtc() );
		}else{
			manifestTemplate.setOptionEtc("");
		}
		
		return manifestTemplate;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 입력한 정보를 ReplaceItemDTO 목록에 넣고 setReplaceItems 메소드에 응답
	 * @title               : setReplaceItems
	 * @return            : List<ReplaceItemDTO>
	***************************************************/
	public List<ReplaceItemDTO> setReplaceItems(CfVO vo, String iaas) {

		List<ReplaceItemDTO> items = new ArrayList<ReplaceItemDTO>();

		// 1.1 Deployment 정보
		items.add(new ReplaceItemDTO("[deploymentName]", vo.getDeploymentName()));
		items.add(new ReplaceItemDTO("[directorUuid]", vo.getDirectorUuid()));
		items.add(new ReplaceItemDTO("[releaseName]", vo.getReleaseName()));
		items.add(new ReplaceItemDTO("[releaseVersion]",  "\"" +vo.getReleaseVersion() + "\""));

		// 1.2 기본정보
		items.add(new ReplaceItemDTO("[domain]", vo.getDomain()));
		items.add(new ReplaceItemDTO("[description]", vo.getDescription()));
		items.add(new ReplaceItemDTO("[domainOrganization]", vo.getDomainOrganization()));
		items.add(new ReplaceItemDTO("[deaDiskMB]", String.valueOf(vo.getDeaDiskMB())));
		items.add(new ReplaceItemDTO("[deaMemoryMB]", String.valueOf(vo.getDeaMemoryMB())));
		items.add(new ReplaceItemDTO("[deaMemoryMB]", String.valueOf(vo.getDeaMemoryMB())));
		if("TRUE".equals(vo.getDiegoYn().toUpperCase())){
			items.add(new ReplaceItemDTO("[appSshFingerprint]", vo.getAppSshFingerprint()));
		}else{
			items.add(new ReplaceItemDTO("[appSshFingerprint]", ""));
		}
		
		// 2. 네트워크 정보
		int InternalCnt = 0;
		for( int i=0; i<vo.getNetworks().size(); i++ ){
			if( "INTERNAL".equals(vo.getNetworks().get(i).getNet().toUpperCase()) ){
				InternalCnt ++;
				if(InternalCnt  == 1 ){
					items.add(new ReplaceItemDTO("[subnetRange]", vo.getNetworks().get(i).getSubnetRange()));
					items.add(new ReplaceItemDTO("[subnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
					items.add(new ReplaceItemDTO("[subnetDns]", vo.getNetworks().get(i).getSubnetDns()));
					items.add(new ReplaceItemDTO("[subnetReserved]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
					items.add(new ReplaceItemDTO("[subnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
					items.add(new ReplaceItemDTO("[cloudNetId]", vo.getNetworks().get(i).getSubnetId()));			
					if( !("VSPHERE".equals(vo.getIaasType().toUpperCase())) ){
						items.add(new ReplaceItemDTO("[cloudSecurityGroups]", vo.getNetworks().get(i).getCloudSecurityGroups()));
					}
				}else if( InternalCnt > 1){
					items.add(new ReplaceItemDTO("[subnetRange1]", vo.getNetworks().get(i).getSubnetRange()));
					items.add(new ReplaceItemDTO("[subnetGateway1]", vo.getNetworks().get(i).getSubnetGateway()));
					items.add(new ReplaceItemDTO("[subnetDns1]", vo.getNetworks().get(i).getSubnetDns()));
					items.add(new ReplaceItemDTO("[subnetReserved1]", vo.getNetworks().get(i).getSubnetReservedFrom() + " - " + vo.getNetworks().get(i).getSubnetReservedTo()));
					items.add(new ReplaceItemDTO("[subnetStatic1]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
					items.add(new ReplaceItemDTO("[cloudNetId1]", vo.getNetworks().get(i).getSubnetId()));			
					if( !("VSPHERE".equals(vo.getIaasType().toUpperCase())) ){
						items.add(new ReplaceItemDTO("[cloudSecurityGroups1]", vo.getNetworks().get(i).getCloudSecurityGroups()));
					}
				}
			}else if( "EXTERNAL".equals(vo.getNetworks().get(i).getNet().toUpperCase()) &&  "VSPHERE".equals(vo.getIaasType().toUpperCase()) ){
				items.add(new ReplaceItemDTO("[publicSubnetRange]", vo.getNetworks().get(i).getSubnetRange()));
				items.add(new ReplaceItemDTO("[publicSubnetGateway]", vo.getNetworks().get(i).getSubnetGateway()));
				items.add(new ReplaceItemDTO("[publicSubnetDns]", vo.getNetworks().get(i).getSubnetDns()));
				items.add(new ReplaceItemDTO("[publicSubnetStatic]", vo.getNetworks().get(i).getSubnetStaticFrom() + " - " + vo.getNetworks().get(i).getSubnetStaticTo()));
				items.add(new ReplaceItemDTO("[publicCloudNetId]", vo.getNetworks().get(i).getSubnetId()));			
			}
		} 
		if( InternalCnt < 2 ){
			items.add(new ReplaceItemDTO("[subnetRange1]", ""));
			items.add(new ReplaceItemDTO("[subnetGateway1]", ""));
			items.add(new ReplaceItemDTO("[subnetDns1]", ""));
			items.add(new ReplaceItemDTO("[subnetReserved1]", ""));
			items.add(new ReplaceItemDTO("[subnetStatic1]", ""));
			items.add(new ReplaceItemDTO("[cloudNetId1]", ""));			
			items.add(new ReplaceItemDTO("[cloudSecurityGroups1]", ""));
		}
		
		// 3.1 프록시 정보
		items.add(new ReplaceItemDTO("[proxyStaticIps]", vo.getProxyStaticIps()));
		items.add(new ReplaceItemDTO("[sslPemPub]", CommonUtils.lineAddSpace(vo.getSslPemPub(),4)));
		items.add(new ReplaceItemDTO("[sslPemRsa]", CommonUtils.lineAddSpace(vo.getSslPemRsa(),4)));
				
		for(KeyVO keyVo : vo.getKeys()){
			// 3.2 UAA 정보
			if(keyVo.getKeyType() == 1310){
				items.add(new ReplaceItemDTO("[loginSecret]", vo.getLoginSecret()));
				items.add(new ReplaceItemDTO("[signingKey]", CommonUtils.lineAddSpace(keyVo.getPrivateKey(),4)));
				items.add(new ReplaceItemDTO("[verificationKey]", CommonUtils.lineAddSpace(keyVo.getPublicKey(),4)));
			}
			//4. Consul
			else if(keyVo.getKeyType() == 1320){
				items.add(new ReplaceItemDTO("[agentCert]", CommonUtils.lineAddSpace(keyVo.getAgentCert(),4)));
				items.add(new ReplaceItemDTO("[agentKey]", CommonUtils.lineAddSpace(keyVo.getAgentKey(),4)));
				items.add(new ReplaceItemDTO("[caCert]", CommonUtils.lineAddSpace(keyVo.getCaCert(),4)));
				items.add(new ReplaceItemDTO("[encryptKeys]", vo.getEncryptKeys()));
				items.add(new ReplaceItemDTO("[serverCert]", CommonUtils.lineAddSpace(keyVo.getServerCert(),4)));
				items.add(new ReplaceItemDTO("[serverKey]", CommonUtils.lineAddSpace(keyVo.getServerKey(),4)));
			}
			//5. BlobStore
			else if(keyVo.getKeyType() == 1330){
				items.add(new ReplaceItemDTO("[blobstoreTlsCert]", CommonUtils.lineAddSpace(keyVo.getTlsCert(),4)));
				items.add(new ReplaceItemDTO("[blobstorePrivateKey]", CommonUtils.lineAddSpace(keyVo.getPrivateKey(),4)));
				items.add(new ReplaceItemDTO("[blobstoreCaCert]", CommonUtils.lineAddSpace(keyVo.getCaCert(),4)));
			}
			//6. Hm9000
			else if(keyVo.getKeyType() == 1340 && !("TRUE".equals(vo.getDiegoYn()))){
					items.add(new ReplaceItemDTO("[hm9000ServerKey]", CommonUtils.lineAddSpace(keyVo.getServerKey(),4)));
					items.add(new ReplaceItemDTO("[hm9000ServerCert]", CommonUtils.lineAddSpace(keyVo.getServerCert(),4)));
					items.add(new ReplaceItemDTO("[hm9000ClientKey]", CommonUtils.lineAddSpace(keyVo.getClientKey(),4)));
					items.add(new ReplaceItemDTO("[hm9000ClientCert]", CommonUtils.lineAddSpace(keyVo.getClientCert(),4)));
					items.add(new ReplaceItemDTO("[hm9000CaCert]", CommonUtils.lineAddSpace(keyVo.getCaCert(),4)));
			}
		}
		if("TRUE".equals(vo.getDiegoYn().toUpperCase())){
			//hm9000
			items.add(new ReplaceItemDTO("[hm9000ServerKey]", ""));
			items.add(new ReplaceItemDTO("[hm9000ServerCert]", ""));
			items.add(new ReplaceItemDTO("[hm9000ClientKey]", ""));
			items.add(new ReplaceItemDTO("[hm9000ClientCert]", ""));
			items.add(new ReplaceItemDTO("[hm9000CaCert]", ""));
		}
		
		// 7. 리소스 정보
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
			items.add(new ReplaceItemDTO("[runnerInstanceType]", vo.getResource().getRunnerFlavor()));
		}
		
		return items;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 단순 레코드 삭제 
	 * @title               : deleteCfInfoRecord
	 * @return            : void
	***************************************************/
	@Transactional
	public void deleteCfInfoRecord(CfParamDTO.Delete dto) throws SQLException {
		cfDao.deleteCfInfoRecord(Integer.parseInt(dto.getId()));
		CommonCodeVO codeVo = commonCodeDao.selectCommonCodeByCodeName(PARENT_CODE, SUB_GROUP_CODE, CODE_NAME);
		if( dto.getId() != null ){
			networkDao.deleteNetworkInfoRecord(Integer.parseInt( dto.getId()), codeVo.getCodeName() );
			keyDao.deleteKeyInfo( Integer.parseInt(dto.getId()),  codeVo.getCodeName() );
			resourceDao.deleteResourceInfo( Integer.parseInt(dto.getId()),  codeVo.getCodeName() );
		}
	}
}