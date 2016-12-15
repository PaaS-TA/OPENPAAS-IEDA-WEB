package org.openpaas.ieda.web.common.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.api.release.ReleaseInfoDTO;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.service.CommonService;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementService;
import org.openpaas.ieda.web.config.systemRelease.service.ReleaseManagementService;
import org.openpaas.ieda.web.information.deploy.service.DeploymentService;
import org.openpaas.ieda.web.information.release.service.ReleaseService;
import org.openpaas.ieda.web.information.stemcell.service.StemcellService;
import org.openpaas.ieda.web.management.code.controller.CommonCodeController;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.web.management.code.service.CommonCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class CommonController {

	@Autowired private CommonService commonService;
	@Autowired private ReleaseManagementService systemReleaseService;
	@Autowired private ReleaseService releaseService;
	@Autowired private StemcellService stemcellService;
	@Autowired private StemcellManagementService stemcellManageService;
	@Autowired private CommonCodeService codeService;
	@Autowired private DirectorConfigService directorService;
	@Autowired private DeploymentService deploymentService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CommonCodeController.class);
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 정보 조회
	 * @title               : getDefaultDirector
	 * @return            : ResponseEntity<DirectorConfigVO>
	***************************************************/
	@RequestMapping(value="/common/use/director", method=RequestMethod.GET)
	public ResponseEntity<DirectorConfigVO> getDefaultDirector() {
			
		if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 정보 조회 요청"); }
		DirectorConfigVO content = directorService.getDefaultDirector();
		if(LOGGER.isInfoEnabled()){ LOGGER.info("=====================> 기본 설치 관리자 정보 조회 성공!!"); }
		
		return new ResponseEntity<DirectorConfigVO>(content, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포명 조회
	 * @title               : getDeploymentList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/common/use/deployments", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getDeploymentList(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포명 조회 요청");  }
		
		List<DeploymentInfoDTO> contents = deploymentService.listDeployment();
		HashMap<String, Object> result = new HashMap<String, Object>();
		int size =0;
		if (contents != null)  size = contents.size();
		result.put("total", size);
		result.put("contents", contents);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포명 조회 요청 성공");  }
		return new ResponseEntity<HashMap<String, Object>>( result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Private Key 파일업로드
	 * @title               : doBootstrapKeyPathFileUpload
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/common/deploy/key/upload", method=RequestMethod.POST)
	public ResponseEntity<?> doBootstrapKeyPathFileUpload( MultipartHttpServletRequest request){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Private key 파일 업로드 조회 요청"); }
		commonService.uploadKeyFile(request);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Private key 파일 업로드 조회 성공"); }
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Private Key 파일 정보 목록  조회
	 * @title               : getKeyPathFileList
	 * @return            : ResponseEntity<List<String>>
	***************************************************/
	@RequestMapping(value="/common/deploy/key/list" , method=RequestMethod.GET)
	public ResponseEntity<List<String>> getKeyPathFileList (){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Private Key 파일  정보 목록 조회 요청"); }
		List<String> keyPathFileList = commonService.getKeyFileList();
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> Private Key 파일  정보 목록 조회 성공"); }
		
		return new ResponseEntity<List<String>>(keyPathFileList, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포파일 정보 조회
	 * @title               : getBoshAwsDeployInfo
	 * @return            : ResponseEntity<String>
	***************************************************/
	@RequestMapping(value="/common/use/deployment/{deploymentFile:.+}", method=RequestMethod.GET)
	public ResponseEntity<String> getBoshAwsDeployInfo(@PathVariable @Valid String deploymentFile){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 정보 조회 요청"); }
		if(LOGGER.isDebugEnabled()){ LOGGER.debug("deploymentFile :"  + deploymentFile); }
		
		HttpStatus status = HttpStatus.OK;
		String content = commonService.getDeploymentInfo(deploymentFile);
		if(StringUtils.isEmpty(content) ) {
			status = HttpStatus.NO_CONTENT;
		}		
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 정보 조회 성공"); }
		return new ResponseEntity<String>(content, status);
	}


	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포파일 브라우저 다운로드
	 * @title               : downloadDeploymentFile
	 * @return            : void
	***************************************************/
	@RequestMapping(value = "/common/deploy/download/manifest/{fileName}", method = RequestMethod.GET)
	public void downloadDeploymentFile( @PathVariable("fileName") String fileName,
			HttpServletRequest request, HttpServletResponse response){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 브라우저 다운로드 요청"); }
	        File file = new File(LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") +fileName +".yml");
	        try {
		        if( file.exists() ){ //파일이 있으면
		        		//파일 타입 확인
		        	String mimeType= URLConnection.guessContentTypeFromName(file.getName());
			        if( StringUtils.isEmpty(mimeType) ){
			        	mimeType = "application/octet-stream";
			        }
		
			        response.setContentType(mimeType);
			        //웹에 다운로드
			        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".yml"); 
			        response.setContentLength((int)file.length());
			        InputStream inputStream = new BufferedInputStream(new FileInputStream(file)); 
			        //파일복사
		       
					FileCopyUtils.copy(inputStream, response.getOutputStream());
		        }
	        } catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			} catch (Exception e){
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 배포파일 브라우저 다운로드 성공"); }
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 시스템 릴리즈 콤보
	 * @title               : localReleaseList
	 * @return            : ResponseEntity<List<String>>
	***************************************************/
	@RequestMapping(value = "/common/deploy/systemRelease/list/{type}/{iaas}", method = RequestMethod.GET)
	public ResponseEntity<List<String>> localReleaseList(@PathVariable String type, @PathVariable String iaas){
		String iaastype = iaas;
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 공통 시스템 릴리즈 콤보 요청"); }
		if("''".equals(iaastype)) iaastype = "";
		List<String> contents = systemReleaseService.getLocalReleaseList(type, iaastype);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 공통 시스템 릴리즈 콤보 성공"); }
		
		return new ResponseEntity<List<String>>( contents, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 릴리즈 콤보(cf/diego/garden/etcd)
	 * @title               : listLocalFilterReleaseList
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping( value="/common/deploy/release/list/{type}", method =RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> listLocalFilterReleaseList(@PathVariable  String type){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 공통 릴리즈 콤보 요청"); }
		List<ReleaseInfoDTO> contents = releaseService.getReleasesFilter(type);
		Map<String, Object> result = new HashMap<>();
		result.put("records", contents);
		result.put("total", (contents == null) ? 0:contents.size());
		return new ResponseEntity<Map<String, Object>>( result, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 스템셀 콤보
	 * @title               : listStemcell
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping( value="/common/deploy/stemcell/list/{type}/{iaas}", method= RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listStemcell(@PathVariable String type, @PathVariable String iaas) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("=======================> 업로드 된 공통 스템셀 콤보 조회 요청!"); }
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<StemcellManagementVO> contents = null;
		
		if("bootstrap".equals(type)){ 
			contents = stemcellManageService.listLocalStemcells(iaas.toLowerCase()); 
		}else{ 
			contents = stemcellService.listStemcell(iaas.toLowerCase()); 
		}
		result.put("total", contents.size());
		result.put("records", contents);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("=======================> 업로드 된 공통 스템셀 콤보 조회 성공!"); }
		return new ResponseEntity<HashMap<String, Object>>(result, HttpStatus.OK);
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 코드 조회 (하위 코드 목록)
	 * @title               : getSubCode
	 * @return            : ResponseEntity<List<CommonCodeVO>>
	***************************************************/
	@RequestMapping(value="/common/deploy/codes/parent/{parentCode}", method=RequestMethod.GET)
	public ResponseEntity<List<CommonCodeVO>> getSubCode(@PathVariable String parentCode) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 서브 그룹 조회 요청");  }
		List<CommonCodeVO> content = codeService.getSubGroupCodeList(parentCode, "", "2");
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 서브 그룹 조회 성공");  }
		return new ResponseEntity<List<CommonCodeVO>>(content, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 코드 조회 (하위 그룹의 코드 목록)
	 * @title               : getComplexCode
	 * @return            : ResponseEntity<List<CommonCodeVO>>
	***************************************************/
	@RequestMapping(value="/common/deploy/codes/parent/{parentCode}/subcode/{subGroupCode}", method=RequestMethod.GET)
	public ResponseEntity<List<CommonCodeVO>> getComplexCode(@PathVariable String parentCode, @PathVariable String subGroupCode) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 공통 코드 조회 요청");  }
		List<CommonCodeVO> content = codeService.getSubGroupCodeList(parentCode, subGroupCode, "3");
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("parentCode ============>" + parentCode);
			LOGGER.debug("subGroupCode ============>" + subGroupCode);
			LOGGER.debug("content ==================>"  + content);
		}
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 공통 코드 조회 성공");  }
		
		return new ResponseEntity<List<CommonCodeVO>>(content, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : lock 파일 생성
	 * @title               : setLockFile
	 * @return            : ResponseEntity<Boolean> 
	***************************************************/
	@RequestMapping(value="/common/deploy/lockFile/{FileName:.*}", method=RequestMethod.GET)
	public ResponseEntity<Boolean> setLockFile(@PathVariable @Valid String FileName){
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 락 파일 요청"); }
		Boolean lock = commonService.lockFileSet(FileName);
		if(LOGGER.isInfoEnabled()){ LOGGER.debug("====================================> 락 파일 요청 성공"); }
		return new ResponseEntity<Boolean>(lock, HttpStatus.OK);
	}
	
}
