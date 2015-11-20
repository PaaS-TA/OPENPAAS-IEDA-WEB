package org.openpaas.ieda.web.deploy.stemcell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadStemcellAsyncByScriptService {
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	public Boolean uploadStemcell(String stemcellDir, String stemcellFileName) {
		Boolean success = Boolean.FALSE;

		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String command = "D:/ieda_workspace/stemcell/bosh_upload_stemcell.bat ";
		command += stemcellDir + " ";
		
		String dir = stemcellDir.replace("/", "\\");
		command += dir + "\\" + stemcellFileName;
		
		log.info("## Command : " + command);

		try {
			Process process = r.exec(command);
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			String streamLogs = "";
			
			while ((info = bufferedReader.readLine()) != null) {
				streamLogs += info;
				log.info("##### uploadStemcell ::: " + info);
				messagingTemplate.convertAndSend("/socket/uploadStemcell", info.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
			messagingTemplate.convertAndSend("/socket/uploadStemcell", e.getMessage());
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (Exception e) {
			}
		}

		success = Boolean.TRUE;

		return success;
	}

	@Async
	public void uploadStemcellAsync(String stemcellDir, String stemcellFileName) {
		try {
			uploadStemcell(stemcellDir, stemcellFileName);
		} catch (Exception e) {
			log.info("# Exception caught uploading asynchronous stemcell. " + e);
		}
	}
}
