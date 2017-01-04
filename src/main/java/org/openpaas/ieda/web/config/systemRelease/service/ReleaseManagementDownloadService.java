package org.openpaas.ieda.web.config.systemRelease.service;

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
import org.openpaas.ieda.web.config.systemRelease.dao.ReleaseManagementDAO;
import org.openpaas.ieda.web.config.systemRelease.dao.ReleaseManagementVO;
import org.openpaas.ieda.web.config.systemRelease.dto.ReleaseManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ReleaseManagementDownloadService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private ReleaseManagementDAO dao;
	
	final private static String RELEASEDIRECTORY = LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator");
	final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
	final private static String MESSAGE_ENDPOINT = "/config/systemRelease/regist/download/logs"; 
	private final static Logger LOGGER = LoggerFactory.getLogger(ReleaseManagementDownloadService.class);
	
	
	/***************************************************
	 * @project			: OpenPaas 플랫폼 설치 자동
	 * @description 	: wget을 이용하여 시스템 릴리즈 다운로드
	 * @title 				: systemReleaseURLSave
	 * @return 			: void
	***************************************************/
	public void systemReleaseDownload(ReleaseManagementDTO.Regist dto, Principal principal) {
		
		if(StringUtils.isEmpty(dto.getReleaseFileName())){
			throw new CommonException("notfound.systemRelease.exception",
					"릴리즈 파일 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		File tmpFile = null;
		File releseFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String info = null;
		String status = "";

		//1. 저장된 릴리즈 정보 조회
		ReleaseManagementVO result = dao.selectSystemReleaseById(dto.getId());
		try{
			//2. wget을 통해 릴리즈 다운로드
			ProcessBuilder builder = new ProcessBuilder("wget", "-d", "-P", TMPDIRECTORY, "--content-disposition", dto.getReleaseFileName());
			builder.redirectErrorStream(true);
			Process process = builder.start();
			
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			
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
				saveSystemRelese(dto, principal);
				messagingTemplate.convertAndSendToUser(principal.getName() ,MESSAGE_ENDPOINT, dto.getId()+"/done");
			}
			
		} catch(IOException e){
			status = "error";
			throw new CommonException("ioFileRead.releaseDownload.exception",
					"릴리즈 파일 다운로드 처리 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			//lock 파일 삭제
			if( status.equals("error") ){
				deleteLockFile( result.getReleaseFileName() );
			}
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
		
		//release path
		tmpFile = new File(TMPDIRECTORY+ System.getProperty("file.separator") + result.getReleaseFileName());
		releseFile = new File(RELEASEDIRECTORY + result.getReleaseFileName());
			
		if(releseFile.exists() && "true".equals(dto.getOverlayCheck())){
			boolean deleteFlie = releseFile.delete();//삭제
			if(!deleteFlie){
				deleteLockFile( result.getReleaseFileName() );
				throw new CommonException("internal_server_error.releaseDownload.exception",
						"기존 파일 삭제 실패.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		//덮어쓰기 가능
		if(releseFile.exists() && "false".equals(dto.getOverlayCheck()) ){
			deleteLockFile( result.getReleaseFileName() );
			throw new CommonException("conflict.releaseDownload.exception",
					"이미 동일한 릴리즈 파일이 존재합니다.", HttpStatus.CONFLICT);
		}else{//덮어쓰기 불가능.
			try {
				FileUtils.moveFile(tmpFile,releseFile);
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error(e.getMessage());
				}
				throw new CommonException("conflict.releaseDownload.exception",
						"이미 동일한 릴리즈 파일이 존재합니다.", HttpStatus.CONFLICT);
			}finally{
				deleteLockFile( result.getReleaseFileName() );
				
			}
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 lock 파일 삭제 
	 * @title               : deleteLockFile
	 * @return            : Boolean
	***************************************************/
	public Boolean deleteLockFile(String fileName){
		Boolean check = false;
		int index = fileName.indexOf(".tgz");
		String lockFile = fileName.substring(0,index);
		File releaseLockFile = new File(LocalDirectoryConfiguration.getLockDir()+System.getProperty("file.separator")+lockFile+"-download"+".lock");
		if(releaseLockFile.exists()){
			check = releaseLockFile.delete();
		}
		return check;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 다운로드 정보 저장
	 * @title               : saveSystemRelese
	 * @return            : void
	***************************************************/
	public void saveSystemRelese(ReleaseManagementDTO.Regist dto, Principal principal){
			SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
			ReleaseManagementVO result = dao.selectSystemReleaseById(dto.getId());
			
			if(result != null){
				dto.setReleaseFileName(result.getReleaseFileName());
				dto.setReleaseSize(result.getReleaseSize());
				dto.setUpdateUserId(sessionInfo.getUserId());
				dao.updateSystemReleaseById(dto);
			}else{
				throw new CommonException("sql.systemRelease.exception",
						"시스템 릴리즈 정보 저장에 실패하였습니다. 확인해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비동기로 systemReleaseDownload 메소드 호출
	 * @title               : releaseDownloadAsync
	 * @return            : void
	***************************************************/
	@Async
	public void releaseDownloadAsync(ReleaseManagementDTO.Regist dto, Principal principal){
		systemReleaseDownload(dto, principal);
	}
}
