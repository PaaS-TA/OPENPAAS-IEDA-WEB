package org.openpaas.ieda.web.deploy.stemcell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UploadStemcellAsyncByScriptService {
	private String uploadStemcellLog;

	public Boolean uploadStemcell(String stemcellDir, String stemcellFileName) {
		Boolean success = Boolean.FALSE;

		Runtime r = Runtime.getRuntime();

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		String command = "D:/ieda_workspace/stemcell/bosh_upload_stemcell.bat ";
		command += stemcellDir + " ";
		command += stemcellFileName;

		log.info("## Command : " + command);

		try {
			uploadStemcellLog = "";
			Process process = r.exec(command);
			process.getInputStream();
			inputStream = process.getInputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String info = null;
			while ((info = bufferedReader.readLine()) != null) {
				uploadStemcellLog += info;
				log.info(info);
			}

		} catch (IOException e) {
			e.printStackTrace();
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
