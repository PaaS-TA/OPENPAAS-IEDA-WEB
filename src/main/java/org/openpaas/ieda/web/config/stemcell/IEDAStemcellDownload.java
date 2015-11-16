package org.openpaas.ieda.web.config.stemcell;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class IEDAStemcellDownload {
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

	private boolean isAvailable;
	private String PUBLIC_STEMCELLS_BASE_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";

	private String subLink;
	private String stemcellFileName;
	private int percentage;
	
	private DownloadStatus status;
	
	public enum DownloadStatus {AVAILABLE, DOWNLOADING};

/*	public void setConfigure(String subLink, String stemcellFileName, double stemcellSize) {
		this.subLink = subLink;
		this.stemcellFileName = stemcellFileName;
		this.stemcellTotalSize = stemcellSize;
	}*/

	@Async
	public void doDownload(String subLink, String stemcellFileName, double stemcellSize) {
		
		log.info("@@ status :: "  + status + " percentage :: " + this.getPercentage() );
		if( status != null && status.equals(DownloadStatus.DOWNLOADING) ){
			throw new IEDACommonException("failedDownloadStemcell.publicStemcell.exception",
					"다운로드 중인 스템셀이 존재합니다.", HttpStatus.LOCKED);
		}
		
		setDownloadStatus(DownloadStatus.DOWNLOADING);
		
		
		String downloadLink = PUBLIC_STEMCELLS_BASE_URL + "/" + subLink;
		
	    BufferedInputStream in = null;
	    FileOutputStream fout = null;
	    
	    percentage = 0;
	    double received = 0;
	    try {
	        in = new BufferedInputStream(new URL(downloadLink).openStream());
	        fout = new FileOutputStream(iedaConfiguration.getStemcellDir()+"/" + stemcellFileName);

	        final byte data[] = new byte[4096];
	        int count;
	        while ((count = in.read(data, 0, 4096)) != -1) {
	            fout.write(data, 0, count);
	            received += count;
	            percentage = (int)((received/stemcellSize) *100);
	            log.info("received:" + received + ", stemcellTotalSize: " + stemcellSize + " = " + percentage);
	        }
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
	    
	    setDownloadStatus(DownloadStatus.AVAILABLE);
	}

	private void resetConfigure() {
		this.subLink = null;
		this.stemcellFileName = null;
		this.stemcellFileName = null;
		isAvailable = true;
	}
	
	private DownloadStatus setDownloadStatus(DownloadStatus status) {
		this.status = status;
		return this.status;
	}
	
	public boolean isDownloaded() {
		
		
		return true;
	}
	
}
