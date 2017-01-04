package org.openpaas.ieda.web.config.systemRelease;

import static org.junit.Assert.assertTrue;

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
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class ReleaseManagementDownloadServiceTest {
	
	@Autowired private ReleaseManagementDAO dao;

	final private static String RELEASEDIRECTORY = LocalDirectoryConfiguration.getReleaseDir() + System.getProperty("file.separator");
	final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
	private final static Logger LOGGER = LoggerFactory.getLogger(ReleaseManagementDownloadServiceTest.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 다운로드
	 * @title               : testReleaseDownloadAsync
	 * @return            : void
	***************************************************/
	public void testReleaseDownloadAsync(ReleaseManagementDTO.Regist dto, Principal principal){
		
		if(StringUtils.isEmpty(dto.getReleaseFileName())){
			throw new CommonException("notfound.systemRelease.exception",
					"릴리즈 파일 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		File tmpFile = null;
		File releseFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String info = null;

		try{
			//2. wget을 통해 릴리즈 다운로드
			ProcessBuilder builder = new ProcessBuilder("wget", "-d", "-P", TMPDIRECTORY, "--content-disposition", dto.getReleaseFileName());
			builder.redirectErrorStream(true);
			Process process = builder.start();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			
			//2.2 실행 출력하는 로그를 읽어온다.
			while ((info = bufferedReader.readLine()) != null){ 
				Pattern pattern = Pattern.compile("\\d+\\%");
				Matcher m = pattern.matcher(info);
				if(m.find()){
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("System Release Downloading...." + m.group()); 
					}
				}
			}
		}catch(IOException e){
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error("System Release Download fail!!!"  );  
			}
		} finally {
			try {
				if(inputStream != null){
					inputStream.close();
				}
				if(bufferedReader != null){
					bufferedReader.close();
				}
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );  
				}
			}
		}
		
		//release path
		ReleaseManagementVO result = dao.selectSystemReleaseById(dto.getId());
		tmpFile = new File(TMPDIRECTORY+ System.getProperty("file.separator") + result.getReleaseFileName());
		releseFile = new File(RELEASEDIRECTORY + result.getReleaseFileName());
			
		if(releseFile.exists() && "true".equals(dto.getOverlayCheck())){
			releseFile.delete();//삭제
		}
		
		if(releseFile.exists() && "false".equals(dto.getOverlayCheck()) ){
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("System Release Download fail!!!");
			}
		}else{
			try {
				FileUtils.moveFile(tmpFile,releseFile);
				//다운로드 여부
				dto.setDownloadStatus("Y");
				saveSystemReleseSave(dto, principal);
			} catch (IOException e) {
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("System Release Download fail!!!");
				}
			}
			
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 다운로드 정보 저장
	 * @title               : saveSystemReleseSave
	 * @return            : void
	***************************************************/
	public void saveSystemReleseSave(ReleaseManagementDTO.Regist dto, Principal principal){
		try{
			SessionInfoDTO sessionInfo = new SessionInfoDTO(principal);
			ReleaseManagementVO result = dao.selectSystemReleaseById(dto.getId());
			if(result != null){
				dto.setReleaseFileName(result.getReleaseFileName());
				dto.setReleaseSize(result.getReleaseSize());
				dto.setUpdateUserId(sessionInfo.getUserId());
				
				assertTrue(dao.updateSystemReleaseById(dto) == 1);
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("System Release Download success!!!");
				}
			}
		}catch(Exception e){
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("System Release Download fail!!!");
			}
		}
	}
}
