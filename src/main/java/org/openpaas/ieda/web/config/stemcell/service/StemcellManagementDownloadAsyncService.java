package org.openpaas.ieda.web.config.stemcell.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StemcellManagementDownloadAsyncService {
	
	@Autowired private SimpMessagingTemplate messagingTemplate;
	@Autowired private StemcellManagementDAO dao;
	
	final static private String PUBLIC_STEMCELLS_BASE_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";
	final static private int BUFFER_SIZE = 8196; 
	final static private  String DESTINATION= "/config/stemcell/download/logs";
	final static private  String STEMCELL_DIR = LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator");
	private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementDownloadAsyncService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Public_Stemcell에서 해당하는 스템셀을 다운로드하며 메시지로 결과 응답
	 * @title               : doDownload
	 * @return            : void
	***************************************************/
	@Async
	public void doDownload(StemcellManagementDTO.Download dto, Principal principal) {
		
		String downloadLink = PUBLIC_STEMCELLS_BASE_URL + "/" + dto.getSublink();
		
	    BufferedInputStream bufferIs = null;
	    FileOutputStream fout = null;
	    
	    int percentage = 0;
	    double received = 0;
	    double stemcellSize = Double.parseDouble(dto.getFileSize());
	    Boolean isError = Boolean.FALSE;
	    try {
	    	
	    	bufferIs = new BufferedInputStream(new URL(downloadLink).openStream());
	        fout = new FileOutputStream(STEMCELL_DIR + dto.getFileName());

	        final byte data[] = new byte[BUFFER_SIZE];
	        int count;
	        while ((count = bufferIs.read(data, 0, BUFFER_SIZE)) != -1) {
	            fout.write(data, 0, count);
	            received += count;
	            if(percentage != (int)((received/stemcellSize) * 100)){ 
	            	percentage = (int)((received/stemcellSize) * 100);
					messagingTemplate.convertAndSend(DESTINATION, dto.getRecid()+"/"+percentage);
	            }
	        }
	        
	        File stemcellFile = new File(STEMCELL_DIR + dto.getFileName());
	        int fileLength = Integer.parseInt(dto.getFileSize());
	        if(stemcellFile.exists() && stemcellFile.length() == fileLength){
	        	SessionInfoDTO session = new SessionInfoDTO(principal);
	        	dto.setUpdateUserId(session.getUserId());
	        	dto.setDownloadStatus("Y");
	        	dao.insertDownloadStatusById(dto);
	        }
	    } catch (FileNotFoundException e) {
	    	isError = Boolean.TRUE;
			messagingTemplate.convertAndSend(DESTINATION, e.getMessage());
		} catch (MalformedURLException e) {
			isError = Boolean.TRUE;
			messagingTemplate.convertAndSend(DESTINATION, e.getMessage());
		} catch (IOException e) {
			isError = Boolean.TRUE;
			messagingTemplate.convertAndSend(DESTINATION, e.getMessage());
		} finally {
	        if (bufferIs != null) {
	            try {
	            	bufferIs.close();
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );
					}
				}
	        }
	        if (fout != null) {
	            try {
					fout.close();
					if(isError){//에러발생시 파일 삭제
						File targetFile = new File(LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator") + dto.getFileName());
						if(targetFile.exists()){
							Boolean check = targetFile.delete();
							if( LOGGER.isDebugEnabled() ){
								LOGGER.debug( "에러 발생 후 파일 삭제  : " +check );
							}
						}
					}
				} catch (IOException e) {
					if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );
					}
				}
	        }
	        int index = dto.getFileName().lastIndexOf(".");
			String lockFileName = dto.getFileName().substring(0, index);
			File lockFile = new File(LocalDirectoryConfiguration.getLockDir()+ System.getProperty("file.separator")+lockFileName+"-download.lock");
			if(lockFile.exists()){
				Boolean check  = lockFile.delete();
				if( LOGGER.isDebugEnabled() ){
					LOGGER.debug("delete lock file : "  + check);
				}
			}
	    }
	}
}
