package org.openpaas.ieda.web.deploy.release;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeleteReleaseAsyncByScriptService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

	public Boolean deleteRelease(String dir, String fileName, String version) {
		Boolean success = Boolean.FALSE;

		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String command = iedaConfiguration.getScriptDir()+"bosh_delete_release.bat ";
		command += dir + " ";
		command += fileName + " ";
		command += version;

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
				log.info("##### DeleteRelease ::: " + info);
				messagingTemplate.convertAndSend("/socket/deleteRelease", info.toString());
			}

		} catch (IOException e) {
			e.printStackTrace();
			messagingTemplate.convertAndSend("/socket/deleteRelease", e.getMessage());
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
	public void deleteReleaseAsync(String dir, String fileName, String version) {
		try {
			deleteRelease(dir, fileName, version);
		} catch (Exception e) {
			log.info("# Exception caught deleting asynchronous Release. " + e);
		}
	}
	
	
}
