package org.openpaas.ieda.web.config.stemcell.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.common.service.CommonService;
import org.openpaas.ieda.web.common.service.CommonUtils;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO.Regist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StemcellManagementService {
	
	@Autowired private CommonService commonService;
	@Autowired private StemcellManagementDAO dao;
	final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
	final private static String STEMCELLDIR = LocalDirectoryConfiguration.getStemcellDir() + System.getProperty("file.separator");
	final private static String SEPARATOR = System.getProperty("file.separator");
	final static private String PUBLIC_STEMCELLS_NEWEST_URL = "https://s3.amazonaws.com"; 
	final static private String PUBLIC_STEMCELLS_OLDEST_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementService.class);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 로컬 스템셀과 비교 후 스템셀 목록 조회
	 * @title         : getStemcellList
	 * @return        : List<StemcellManagementVO>
	***************************************************/
	public List<StemcellManagementVO> getStemcellList() {
		
		List<StemcellManagementVO> list = dao.selectPublicStemcellList();
		if( list != null ){
			for( StemcellManagementVO stemcell : list ){
				if( stemcell.getDownloadStatus() != null ){
					if( stemcell.getDownloadStatus().equals("DOWNLOADED") ){
						File stemcellFile = new File(LocalDirectoryConfiguration.getStemcellDir()+System.getProperty("file.separator")+stemcell.getStemcellFileName());
						if(!stemcellFile.exists() || stemcellFile.length() == 0){
							StemcellManagementDTO.Delete dto = new StemcellManagementDTO.Delete();
							dto.setId(stemcell.getId());
							dto.setStemcellFileName(stemcell.getStemcellFileName());
							dao.deletePublicStemcell(dto);
						}
					}
				}
			}
			list = dao.selectPublicStemcellList();
		}
		return list;
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 정보 검사
	 * @title         : registPublicStemcellUploadInfo
	 * @return        : StemcellManagementVO
	***************************************************/
	public StemcellManagementVO registPublicStemcellUploadInfo(Regist dto, String testFlag) {
		String fileName = dto.getStemcellFileName();
		String status = "";
		int index = fileName.lastIndexOf(".");
		String lockFile = fileName.substring(0, index);
		Boolean checkLock = commonService.lockFileSet(lockFile +"-download");
		if(!checkLock ){
			throw new CommonException("conflict.publicStemcell.exception", 
					"현재 다른 플랫폼 설치 관리자가 동일한 스템셀을 등록 중 입니다.", HttpStatus.CONFLICT);
		}
		StemcellManagementVO vo = null;
		if( fileName.indexOf(".tgz") < 0 && fileName.indexOf(".zip") < 0 ){
			status = "error";
			deleteLockFile(status, dto.getStemcellFileName());
			throw new CommonException("preconditionFailed.publicStemcell.exception",
					"잘못된 확장자를 갖은 스템셀 파일 입니다.", HttpStatus.PRECONDITION_FAILED);
		}
		if(StringUtils.isEmpty(dto.getStemcellFileName())){
			status = "error";
			deleteLockFile(status, dto.getStemcellFileName());
			throw new CommonException("notfound.publicStemcell.exception",
					"해당하는 스템셀 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		if(Long.parseLong(dto.getStemcellSize()) < 1 ){
			status = "error";
			deleteLockFile(status, dto.getStemcellFileName());
			throw new CommonException("notfound.publicStemcell.exception",
					"해당하는 스템셀 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		//스템셀 path
		File stemcell = new File(LocalDirectoryConfiguration.getStemcellDir()+"/"+ dto.getStemcellFileName());
		
		//스템셀 파일이 존재하고 덮어쓰기 체크가 안되어 있을 경우
		if(stemcell.exists() && "false".equals(dto.getOverlayCheck())) {
			status = "conflict";
			throw new CommonException("existStemcellFile.publicStemcell.exception",
					dto.getStemcellFileName() + "의 스템셀 파일은 이미 존재합니다.", HttpStatus.CONFLICT);
		}else{
			status = "done";
			// 스템셀 사이즈 format
			long stemcellSize = Long.parseLong(dto.getStemcellSize());
			dto.setStemcellSize(CommonUtils.formatSizeUnit(stemcellSize));
			dto.setDownloadStatus("DOWNLOADING");
			vo = savePublicStemcell(dto, testFlag);
		}
		return vo;
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 시 스템셀 정보 저장
	 * @title         : savePublicStemcell
	 * @return        : StemcellManagementVO
	***************************************************/
	private StemcellManagementVO savePublicStemcell(StemcellManagementDTO.Regist dto, String testFlag) {
		StemcellManagementVO vo = null;
		StemcellManagementVO fileCheckResult = null;
		String stemcellFileVersion = "";
		try{
			SessionInfoDTO sessionInfo = new SessionInfoDTO();
			//중복
			fileCheckResult = dao.selectPublicStemcell(dto.getStemcellFileName());
			if(dto.getAwsLight().toLowerCase().equals("true") || dto.getStemcellFileName().indexOf("light")!=-1){
				stemcellFileVersion = dto.getStemcellFileName().split("-")[3];
			}else{
				stemcellFileVersion = dto.getStemcellFileName().split("-")[2];
			}
			
			if( fileCheckResult == null || "Y".equals(testFlag)){
				dto.setStemcellVersion(stemcellFileVersion);
				dto.setCreateUserId(sessionInfo.getUserId());
				dto.setUpdateUserId(sessionInfo.getUserId());
				dao.insertPublicStemcell(dto);
			} else{
				dto.setStemcellVersion(stemcellFileVersion);
				dto.setUpdateUserId(sessionInfo.getUserId());
				dto.setId((fileCheckResult.getId()));
				dao.updatePublicStemcell(dto);
			}
			vo = dao.selectPublicStemcellById(dto.getId());
			
		} catch(NullPointerException e){
			deleteLockFile("error", dto.getStemcellFileName());
			throw new CommonException("NullPointerException.publicStemcell.exception",
					"스템셀 정보 저장에 실패하였습니다. 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Exception e){
			deleteLockFile("error", dto.getStemcellFileName());
			throw new CommonException("Exception.publicStemcell.exception",
					"스템셀 정보 저장에 실패하였습니다. 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return vo;
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 다운로드 시 정보 출력
	 * @title         : registPublicStemcellDownLoadInfo
	 * @return        : StemcellManagementVO
	***************************************************/
	public StemcellManagementVO registPublicStemcellDownLoadInfo(StemcellManagementDTO.Regist dto, String testFlag) {
		if(StringUtils.isEmpty(dto.getStemcellVersion())&& dto.getStemcellVersion() == null){
			throw new CommonException("notfound.publicStemcell.exception",
					"스템셀 파일 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		InputStream inputStream = null;
		StemcellManagementVO vo = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		String status = "";
		String[] search = null;
		String stemcellSize = "";
		StringBuffer accumulatedBuffer = new StringBuffer();
		String info = null;
		boolean flag = false;
		//IaaS별 상세 하이퍼 바이저 조합
		String iaas = iaasHypervisor(dto);
		//스템셀 버전 별 다운로드 Url 조합
		String stemcellDownLoadPath = "";
		String stemcellFileName = "";
		String stemcellFileVersion = "";
		if(dto.getFileType().toLowerCase().equals("version")){
			
			String baseUrl = stemcellVersionTypeDownLoadBaseUrl(dto);
			String subUrl = stemcellVersionTypeDownLoadSubUrl(dto);
			
			if(("centos").equals(dto.getOsName().toLowerCase())){
				if("6.X".equals(dto.getOsVersion())){
					stemcellDownLoadPath = baseUrl+SEPARATOR+subUrl+"-"+dto.getStemcellVersion().toLowerCase()+"-"+iaas+"-"+dto.getOsName().toLowerCase()+"-"+dto.getOsVersion().toLowerCase().replace("6.x", "")+"go_agent.tgz";
				}else if("7.X".equals(dto.getOsVersion())){
					stemcellDownLoadPath = baseUrl+SEPARATOR+subUrl+"-"+dto.getStemcellVersion().toLowerCase()+"-"+iaas+"-"+dto.getOsName().toLowerCase()+"-"+dto.getOsVersion().toLowerCase().replace("7.x", "7")+"-go_agent.tgz";
				}
			}else{
				stemcellDownLoadPath = baseUrl+SEPARATOR+subUrl+"-"+dto.getStemcellVersion().toLowerCase()+"-"+iaas+"-"+dto.getOsName().toLowerCase()+"-"+dto.getOsVersion().toLowerCase()+"-go_agent.tgz";
			}
			
		}else if(dto.getFileType().toLowerCase().equals("url")){
			stemcellDownLoadPath = dto.getStemcellUrl();
		}
		try{
			//wget 실행
			ProcessBuilder builder = new ProcessBuilder("wget", "--spider","-d", "-P", TMPDIRECTORY, "--content-disposition", stemcellDownLoadPath);
			builder.redirectErrorStream(true);
			process = builder.start();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String contains = "";
			while ((info = bufferedReader.readLine()) != null){ 
				accumulatedBuffer.append(info + "\n");
				if(dto.getFileType().toLowerCase().equals("url") && dto.getStemcellUrl().contains("bosh.io")){
					contains = "Location:";
				}else{
					contains ="https:";
				}
				if(info.contains(contains) && !flag){
					search = info.split("/");
					stemcellFileName = search[search.length-1];
					if(dto.getFileType().toLowerCase().equals("url") && dto.getStemcellUrl().contains("bosh.io")){
						if(stemcellFileName.contains("light")){
							stemcellFileVersion = stemcellFileName.split("-")[3];
						}else{
							stemcellFileVersion = stemcellFileName.split("-")[2];
						}
					}else{
						if(dto.getAwsLight().toLowerCase().equals("true") || stemcellFileName.contains("light")){
							stemcellFileVersion = stemcellFileName.split("-")[3];
						}else if(dto.getAwsLight().toLowerCase().equals("false")){
							stemcellFileVersion = stemcellFileName.split("-")[2];
						}
					}
					flag = true;
				}
				if(info.contains("Content-Length:")){
					search = info.split(" ");
					stemcellSize = search[search.length-1];
				}
			}
		}catch(IOException e){
			throw new CommonException("stemcellSave.publicstemcell.exception",
					"스템셀 파일 다운로드 처리 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			try {
				if(bufferedReader!= null){
					bufferedReader.close();
				}
				if(inputStream!= null){
					inputStream.close();
				}
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error(e.getMessage());
				}
			}
		}
		File stemcllFile = new File(STEMCELLDIR + stemcellFileName);
		//스템셀 파일이 존재하고 덮어쓰기 체크가 안되어 있을 경우
		if(stemcllFile.exists() && "false".equals(dto.getOverlayCheck())) {
			status ="error";
			throw new CommonException("existReleaseFile.publicStemcell.exception", 
					stemcellFileName + "의 스템셀 파일은 이미 존재합니다.", HttpStatus.CONFLICT);
		}else{
			status ="done";
			if("".equals(stemcellSize) ||  stemcellSize.isEmpty()){
				throw new CommonException("notfoundStemcellFile.publicStemcell.exception", 
						stemcellFileName + "<br> 해당 스템셀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
			}
			//create lock file 
			int index = stemcellFileName.lastIndexOf(".");
			String lockFile = stemcellFileName.substring(0, index);
			Boolean checkLock = commonService.lockFileSet(lockFile +"-download");
			if( !checkLock ){
				throw new CommonException("conflict.publicStemcell.exception", 
						"현재 다른 플랫폼 설치 관리자가 동일한 스템셀을 등록 중 입니다.", HttpStatus.CONFLICT);
			}
			dto.setStemcellSize(CommonUtils.formatSizeUnit(Long.parseLong(stemcellSize)));
			dto.setStemcellVersion(stemcellFileVersion);
			dto.setStemcellFileName(stemcellFileName);
			dto.setDownloadStatus("DOWNLOADING");
			vo = savePublicStemcell(dto, testFlag);
		}
		return vo;
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 버전 별 서브 Url 조합
	 * @title         : stemcellVersionTypeDownLoadSubUrl
	 * @return        : String
	***************************************************/
	private String stemcellVersionTypeDownLoadSubUrl(StemcellManagementDTO.Regist dto) {
		String subUrl = "";
			if(dto.getAwsLight().toLowerCase().equals("true")){
				subUrl = "light-bosh-stemcell";
			}else{
				subUrl = "bosh-stemcell";
			}
		return subUrl;
	}

	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : IaaS 별 하이퍼바이저 조합
	 * @title         : iaasHypervisor
	 * @return        : String
	***************************************************/
	private String iaasHypervisor(StemcellManagementDTO.Regist dto) {
		String iaas = "";
		if(dto.getIaasType().toLowerCase().equals("openstack")){
			iaas = dto.getIaasType().toLowerCase()+"-kvm";
		}else if(dto.getIaasType().toLowerCase().equals("vsphere")){
			iaas = dto.getIaasType().toLowerCase()+"-esxi";
		}else if(dto.getIaasType().toLowerCase().equals("aws")){
			if(dto.getAwsLight().toLowerCase().equals("true")){
				iaas = dto.getIaasType().toLowerCase()+"-xen-hvm";
			}else{
				iaas = dto.getIaasType().toLowerCase()+"-xen";
			}
			
		}
		return iaas;
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 버전 별 중간 Url 조합
	 * @title         : stemcellVersionTypeDownLoadUrl
	 * @return        : String
	***************************************************/
	private String stemcellVersionTypeDownLoadBaseUrl(StemcellManagementDTO.Regist dto) {
		String baseUrl = "";
		try{
			if(Float.parseFloat(dto.getStemcellVersion())>3264){
				if(dto.getAwsLight().toLowerCase().equals("true")){
					baseUrl = PUBLIC_STEMCELLS_NEWEST_URL+SEPARATOR+"bosh-aws-light-stemcells";
				}else{
					baseUrl = PUBLIC_STEMCELLS_NEWEST_URL+SEPARATOR+"bosh-core-stemcells"+SEPARATOR+dto.getIaasType().toLowerCase();
				}
			}else{
				baseUrl = PUBLIC_STEMCELLS_OLDEST_URL+SEPARATOR+"bosh-stemcell"+SEPARATOR+dto.getIaasType().toLowerCase();
			}
		}catch(NumberFormatException e){
			throw new CommonException("badRequest.publicStemcell.exception.", "잘못된 스템셀 버전 요청 입니다.", HttpStatus.BAD_REQUEST);
		}
		return baseUrl;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Exception 발생 시 lock 파일 삭제 
	 * @title               : deleteLockFile
	 * @return            : Boolean
	***************************************************/
	public Boolean deleteLockFile(String status,String  fileName){
		Boolean flag = false;
		if( status.equals("error")){
			//lock file delete
			if( !StringUtils.isEmpty(fileName) ){
				int index = fileName.lastIndexOf(".");
				String lockFileName = fileName.substring(0, index);
				File lockFile = new File(LocalDirectoryConfiguration.getLockDir()+ System.getProperty("file.separator")+lockFileName+"-download.lock");
				if(lockFile.exists()){
					flag = lockFile.delete();
				}
			}
		}
		return flag;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 삭제
	 * @title               : deletePublicStemcell
	 * @return            : Boolean
	***************************************************/
	public Boolean deletePublicStemcell(StemcellManagementDTO.Delete dto) throws SQLException{
		Boolean check = false;
		File file = new File(STEMCELLDIR +  dto.getStemcellFileName());
		dao.deletePublicStemcell(dto);
		if(file.exists()){ 
			int index = dto.getStemcellFileName().indexOf(".tgz");
			String lockFileName = dto.getStemcellFileName().substring(0, index) + "-download.lock";
			File lcokFile = new File(LocalDirectoryConfiguration.getLockDir() + lockFileName );
			if(  lcokFile.exists() ) check = lcokFile.delete();
			boolean delete = file.delete(); 
			if(!delete){
				throw new CommonException("sqlException Error.publicStemcell.exception",
						"스템셀 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}else{
			throw new CommonException("notfound.publicStemcell.exception",
					"해당하는 스템셀 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}   
		return check;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 로컬 스템셀 콤보
	 * @title               : listLocalStemcells
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	public List<StemcellManagementVO> listLocalStemcells(String iaas){
		
		List<StemcellManagementVO> list = dao.selectLocalStemcellList(iaas);
		
		if( list != null ){
			for( StemcellManagementVO stemcell : list ){
				if( stemcell.getDownloadStatus() != null ){
					if( stemcell.getDownloadStatus().equals("DOWNLOADED") ){
						File stemcellFile = new File(LocalDirectoryConfiguration.getStemcellDir()+System.getProperty("file.separator")+stemcell.getStemcellFileName());
						if(!stemcellFile.exists() || stemcellFile.length() == 0){
							StemcellManagementDTO.Delete dto = new StemcellManagementDTO.Delete();
							dto.setId(stemcell.getId());
							dto.setStemcellFileName(stemcell.getStemcellFileName());
							dao.deletePublicStemcell(dto);
						}
					}
				}
			}
			list = dao.selectLocalStemcellList(iaas);
		}
		return list;
	}
}
