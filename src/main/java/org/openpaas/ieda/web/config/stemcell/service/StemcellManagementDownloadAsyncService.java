package org.openpaas.ieda.web.config.stemcell.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StemcellManagementDownloadAsyncService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private StemcellManagementDAO dao;
	
	final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
	final static private  String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator");
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementDownloadAsyncService.class);
	final private static String MESSAGE_ENDPOINT = "/config/stemcell/regist/download/logs"; 
	final static private String PUBLIC_STEMCELLS_NEWEST_URL = "https://s3.amazonaws.com"; 
	final static private String PUBLIC_STEMCELLS_OLDEST_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";
	final private static String SEPARATOR = System.getProperty("file.separator");
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : wget을 통한 실제 물리 파일 다운로드
	 * @title         : registPublicStemcellUrlDownLoadInfo
	 * @return        : void
	***************************************************/
	public void registPublicStemcellUrlDownLoadInfo(StemcellManagementDTO.Regist dto, Principal principal) {
		if(StringUtils.isEmpty(dto.getStemcellFileName())){
			throw new CommonException("notfound.publicstemcell.exception",
					"스템셀 파일 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		File tmpFile = null;
		File stemcellFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String info = null;
		String status = "";
		String baseUrl = "";
		String realStemcellDownloadFilePath = "";
		if(dto.getFileType().toLowerCase().equals("version")){
			baseUrl = stemcellVersionTypeDownLoadBaseUrl(dto);
			realStemcellDownloadFilePath = baseUrl+ SEPARATOR +dto.getStemcellFileName();
		}else{
			realStemcellDownloadFilePath = dto.getStemcellUrl();
		}
		//1. 저장된 스템셀 정보 조회
		StemcellManagementVO result = dao.selectPublicStemcellById(dto.getId());
		try{
			//2. wget을 통해 스템셀 다운로드
			ProcessBuilder builder = new ProcessBuilder("wget", "-d", "-P", TMPDIRECTORY, "--content-disposition", realStemcellDownloadFilePath);
			builder.redirectErrorStream(true);
			Process process = builder.start();
			
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			String percentage  = "0%";
			//2.2 실행 출력하는 로그를 읽어온다.
			while ((info = bufferedReader.readLine()) != null){ 
				Pattern pattern = Pattern.compile("\\d+\\%");
				Matcher m = pattern.matcher(info);
				if(m.find()){
					status = "done";
						messagingTemplate.convertAndSendToUser(principal.getName() ,MESSAGE_ENDPOINT, dto.getId()+"/"+m.group());
				}
			}
			//다운로드 여부
			if( "done".equals(status) ){
				dto.setDownloadStatus("DOWNLOADED");
				savePublicStemcell(dto, principal);
				messagingTemplate.convertAndSendToUser(principal.getName() ,MESSAGE_ENDPOINT, dto.getId()+"/done");
			}
			
		} catch(IOException e){
			status = "error";
			deleteLockFile(status, dto.getStemcellFileName());
			throw new CommonException("stemcellDownload.publicstemcell.exception",
					"스템셀 파일 다운로드 처리 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			deleteLockFile(status, dto.getStemcellFileName());
			try {
				if(bufferedReader != null){
					bufferedReader.close();
				}
				if(inputStream != null){
					inputStream.close();
				}
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
		}
		
		//스템셀
		tmpFile = new File(TMPDIRECTORY+ System.getProperty("file.separator") + result.getStemcellFileName());
		stemcellFile = new File(STEMCELL_DIR + result.getStemcellFileName());
			
		if(stemcellFile.exists() && "true".equals(dto.getOverlayCheck())){
			Boolean checkDeleteStemcellFile = stemcellFile.delete();//삭제
			if( LOGGER.isErrorEnabled() ){
				LOGGER.debug("fileDelete",checkDeleteStemcellFile);
			}
		}
		//덮어쓰기 가능
		if(stemcellFile.exists() && "false".equals(dto.getOverlayCheck()) ){
			deleteLockFile(status, dto.getStemcellFileName());
			throw new CommonException("conflict.PublicStemcell.exception",
					"이미 동일한 스템셀 파일이 존재합니다.", HttpStatus.CONFLICT);
		}else{//덮어쓰기 불가능.
			try {
				FileUtils.moveFile(tmpFile,stemcellFile);
			} catch (IOException e) {
				deleteLockFile(status, dto.getStemcellFileName());
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error(e.getMessage());
				}
				throw new CommonException("conflict.PublicStemcell.exception",
						"이미 동일한 스템셀 파일이 존재합니다.", HttpStatus.CONFLICT);
			}finally{
				int index = dto.getStemcellFileName().lastIndexOf(".");
				String lockFileName = dto.getStemcellFileName().substring(0, index);
				File lockFile = new File(LocalDirectoryConfiguration.getLockDir()+ System.getProperty("file.separator")+lockFileName+"-download.lock");
				if(lockFile.exists()){
					Boolean checkLockStemcellFile = lockFile.delete();
					if( LOGGER.isErrorEnabled() ){
						LOGGER.debug("fileDelete",checkLockStemcellFile);
					}
				}
			}
		}
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 다운로드 정보 저장
	 * @title         : savePublicStemcell
	 * @return        : void
	***************************************************/
	private void savePublicStemcell(StemcellManagementDTO.Regist dto, Principal principal) {
		SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
		StemcellManagementVO result = dao.selectPublicStemcellById(dto.getId());
		
		if(result != null){
			dto.setStemcellFileName(result.getStemcellFileName());
			dto.setStemcellSize(result.getSize());
			dto.setUpdateUserId(sessionInfo.getUserId());
			dao.updatePublicStemcellById(dto);
		}else{
			throw new CommonException("Savestemcell.publicStemcell.exception",
					"시스템 스템셀 정보 저장에 실패하였습니다. 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 버전에 따른 BaseUrl 설정
	 * @title         : stemcellVersionTypeDownLoadBaseUrl
	 * @return        : String
	***************************************************/
	private String stemcellVersionTypeDownLoadBaseUrl(StemcellManagementDTO.Regist dto) {
		String baseUrl = "";
		if(Float.parseFloat(dto.getStemcellVersion())>3264){
			if(dto.getAwsLight().toLowerCase().equals("true")){
				baseUrl = PUBLIC_STEMCELLS_NEWEST_URL+SEPARATOR+"bosh-aws-light-stemcells";
			}else{
				baseUrl = PUBLIC_STEMCELLS_NEWEST_URL+SEPARATOR+"bosh-core-stemcells"+SEPARATOR+dto.getIaasType().toLowerCase();
			}
		}else{
			baseUrl = PUBLIC_STEMCELLS_OLDEST_URL+SEPARATOR+"bosh-stemcell"+SEPARATOR+dto.getIaasType().toLowerCase();
		}
		return baseUrl;
	}
	
	/***************************************************
	 * @project       : OpenPaas 플랫폼 설치 자동화
	 * @description   : 스템셀 다운로드 method 비동기 호출
	 * @title         : stemcellDownloadAsync
	 * @return        : void
	***************************************************/
	@Async
	public void stemcellDownloadAsync(StemcellManagementDTO.Regist dto, Principal principal) {
		registPublicStemcellUrlDownLoadInfo(dto, principal);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Exception 발생 시 lock 파일 삭제 
	 * @title               : deleteLockFile
	 * @return            : Boolean
	***************************************************/
	public Boolean deleteLockFile(String status,String  fileName){
		Boolean flag = false;
		if( status.equals("error") || status.equals("done")){
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
}
