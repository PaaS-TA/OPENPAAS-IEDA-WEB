package org.openpaas.ieda.web.information.task.dto;

import org.hibernate.validator.constraints.NotBlank;

public class TaskDTO {
	
	public static class GetLog{
		@NotBlank
		private String logType; //로그 유형
		@NotBlank
		private String taskId; //Task ID
		@NotBlank
		private String lineOneYn;
		
		public String getLogType() {
			return logType;
		}
		public void setLogType(String logType) {
			this.logType = logType;
		}
		public String getTaskId() {
			return taskId;
		}
		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}
		public String getLineOneYn() {
			return lineOneYn;
		}
		public void setLineOneYn(String lineOneYn) {
			this.lineOneYn = lineOneYn;
		}
	}
}
