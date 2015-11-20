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
public class DeleteStemcellAsyncByScriptService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public Boolean deleteStemcell(String stemcellDir, String stemcellFileName, String stemcellVersion) {
		Boolean success = Boolean.FALSE;

		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String command = "D:/ieda_workspace/stemcell/bosh_delete_stemcell.bat ";
		command += stemcellDir + " ";
		command += stemcellFileName + " ";
		command += stemcellVersion;
		
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
				log.info("##### DeleteStemcell ::: " + info);
				messagingTemplate.convertAndSend("/socket/deleteStemcell", info.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
			messagingTemplate.convertAndSend("/socket/deleteStemcell", e.getMessage());
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
	public void deleteStemcellAsync(String stemcellDir, String stemcellFileName, String stemcellVersion) {
		try {
			deleteStemcell(stemcellDir, stemcellFileName, stemcellVersion);
		} catch (Exception e) {
			log.info("# Exception caught deleting asynchronous stemcell. " + e);
		}
	}

}
