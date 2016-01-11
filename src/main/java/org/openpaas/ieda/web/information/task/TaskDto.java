package org.openpaas.ieda.web.information.task;

import org.hibernate.validator.constraints.NotBlank;
import org.openpaas.ieda.web.information.release.ReleaseContentDto;
import org.openpaas.ieda.web.information.release.ReleaseContentDto.Upload;

import lombok.Data;

public class TaskDto {
	
	@Data
	public static class GetLog{
		@NotBlank
		private String logType;

		@NotBlank
		private String taskId;
		
	}

}
