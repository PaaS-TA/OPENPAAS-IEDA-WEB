package org.openpaas.ieda.web.config.systemStemcell;
 
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
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO.Regist;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementDownloadAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@Transactional
@TestPropertySource(locations="classpath:application_test.properties")
public class StemcellManagementServiceTest {
	
    @Autowired private StemcellManagementDAO dao;
    final static private  String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator");
	final private static String TMPDIRECTORY = LocalDirectoryConfiguration.getTmpDir();
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementServiceTest.class);
	final static private String PUBLIC_STEMCELLS_NEWEST_URL = "https://s3.amazonaws.com"; 
	final static private String PUBLIC_STEMCELLS_OLDEST_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";
	final private static String SEPARATOR = System.getProperty("file.separator");
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 다운로드
	 * @title               : testReleaseDownloadAsync
	 * @return            : void
	***************************************************/
	public void teststemcellDownloadAsync(Regist dto, Principal principalTest) {
		if(StringUtils.isEmpty(dto.getStemcellFileName())){
			throw new CommonException("notfound.publicstemcell.exception",
					"스템셀 파일 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		File tmpFile = null;
		File stemcellFile = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String info = null;
		StemcellManagementVO result = dao.selectPublicStemcellById(dto.getId());
		String StemcellDownloadPath = "";
		String baseUrl = "";
		if(dto.getFileType().equals("url")){
			StemcellDownloadPath = dto.getStemcellUrl();
		}else{
			baseUrl = stemcellVersionTypeDownLoadBaseUrl(dto);
			StemcellDownloadPath = baseUrl+ SEPARATOR +dto.getStemcellFileName();
		}
		try{
			//2. wget을 통해 스템셀 다운로드
			ProcessBuilder builder = new ProcessBuilder("wget", "-d", "-P", TMPDIRECTORY, "--content-disposition", StemcellDownloadPath);
			builder.redirectErrorStream(true);
			Process process = builder.start();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
			//2.2 실행 출력하는 로그를 읽어온다.
			while ((info = bufferedReader.readLine()) != null){ 
				Pattern pattern = Pattern.compile("\\d+\\%");
				Matcher m = pattern.matcher(info);
				if(m.find()){
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("Stemcell Downloading...." + m.group()); 
					}
				}
			}
		} catch(IOException e){
			throw new CommonException("stemcellDownload.publicstemcell.exception",
					"스템셀 파일 다운로드 처리 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
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
			stemcellFile.delete();//삭제
		}
		//덮어쓰기 가능
		if("false".equals(dto.getOverlayCheck()) ){
			throw new CommonException("conflict.PublicStemcell.exception",
					"이미 동일한 스템셀 파일이 존재합니다.", HttpStatus.CONFLICT);
		}else{//덮어쓰기 불가능.
			try {
				FileUtils.moveFile(tmpFile,stemcellFile);
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error(e.getMessage());
				}
				e.printStackTrace();
				throw new CommonException("conflict.PublicStemcell.exception",
						"이미 동일한 스템셀 파일이 존재합니다.", HttpStatus.CONFLICT);
			}
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
    
}