package org.openpaas.ieda.web.deploy.release;

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
public class UploadReleasAsyncByScriptService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	public Boolean uploadRelease(String dir, String fileName) {
		Boolean success = Boolean.FALSE;

		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String command = "D:/ieda_workspace/release/bosh_upload_release.bat ";
		command += dir + " ";
		//		command += fileName;

		command += dir.replace("/", "\\") + "\\" + fileName;
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
				log.info("##### uploadRelease ::: " + info);
				messagingTemplate.convertAndSend("/socket/uploadRelease", info.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			messagingTemplate.convertAndSend("/socket/uploadRelease", e.getMessage());
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
	public void uploadReleaseAsync(String dir, String fileName) {
		try {
			uploadRelease(dir, fileName);
		} catch (Exception e) {
			log.info("# Exception caught uploading asynchronous Release. " + e);
		}
	}
}
