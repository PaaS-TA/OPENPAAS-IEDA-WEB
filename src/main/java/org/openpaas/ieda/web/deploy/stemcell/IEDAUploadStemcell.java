package org.openpaas.ieda.web.deploy.stemcell;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class IEDAUploadStemcell {

	private String filePath;
	private String uploadFileName;

	private String taskId;
	private String taskLog;

	public IEDAUploadStemcell(String filePath, String uploadFileName) {
		this.filePath = filePath;
		this.uploadFileName = uploadFileName;
	}

	@Async
	public void execute() {
		try {
			Thread.sleep(1000 * 10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("### do it now to upload stemcell");
	}

}
