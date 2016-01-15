package org.openpaas.ieda.web.config.stemcell;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class IEDAStemcellManagementDownload {
	
	private boolean isAvailable;
	final private String PUBLIC_STEMCELLS_BASE_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";

	private String subLink;
	private String stemcellFileName;
	private int percentage;
	
	final private int DOWNLOAD_BUFFER_SIZE = 8196; 
	
	private DownloadStatus status;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	public enum DownloadStatus {AVAILABLE, DOWNLOADING};

	@Async
	public void doDownload(StemcellManagementDto.Download dto) {
		
		if( status != null && status.equals(DownloadStatus.DOWNLOADING) ){
			throw new IEDACommonException("failedDownloadStemcell.publicStemcell.exception",
					"다운로드 중인 스템셀이 존재합니다.", HttpStatus.LOCKED);
		}
		
		setDownloadStatus(DownloadStatus.DOWNLOADING);
		
		
		String downloadLink = PUBLIC_STEMCELLS_BASE_URL + "/" + dto.getKey();
		
	    BufferedInputStream in = null;
	    FileOutputStream fout = null;
	    
	    percentage = 0;
	    double received = 0;
	    double stemcellSize = Double.parseDouble(dto.getFileSize());
	    Boolean isError = Boolean.FALSE;
	    try {
	        in = new BufferedInputStream(new URL(downloadLink).openStream());
	        fout = new FileOutputStream(LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator")  + dto.getFileName());

	        final byte data[] = new byte[DOWNLOAD_BUFFER_SIZE];
	        int count;
	        while ((count = in.read(data, 0, DOWNLOAD_BUFFER_SIZE)) != -1) {
	            fout.write(data, 0, count);
	            received += count;
	            if(percentage != (int)((received/stemcellSize) *100)){ 
	            	percentage = (int)((received/stemcellSize) *100);
					messagingTemplate.convertAndSend("/socket/downloadStemcell", dto.getRecid()+"/"+percentage);
	            }
	        }
	    } catch (FileNotFoundException e) {
	    	isError = Boolean.TRUE;
			e.printStackTrace();
			messagingTemplate.convertAndSend("/socket/uploadStemcell", e.getMessage());
		} catch (MalformedURLException e) {
			isError = Boolean.TRUE;
			e.printStackTrace();
			messagingTemplate.convertAndSend("/socket/uploadStemcell", e.getMessage());
		} catch (IOException e) {
			isError = Boolean.TRUE;
			e.printStackTrace();
			messagingTemplate.convertAndSend("/socket/uploadStemcell", e.getMessage());
		} finally {
	        if (in != null) {
	            try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        if (fout != null) {
	            try {
					fout.close();
					if(isError){//에러발생시 파일 삭제
						File targetFile = new File(LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator") + dto.getFileName());
						targetFile.delete();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	    
	    setDownloadStatus(DownloadStatus.AVAILABLE);
	}

	/* 미사용중
	 * private void resetConfigure() {
		this.subLink = null;
		this.stemcellFileName = null;
		this.stemcellFileName = null;
		isAvailable = true;
	}*/
	
	private DownloadStatus setDownloadStatus(DownloadStatus status) {
		this.status = status;
		return this.status;
	}
	
}
