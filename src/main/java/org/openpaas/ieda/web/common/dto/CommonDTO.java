package org.openpaas.ieda.web.common.dto;

import javax.validation.constraints.NotNull;

public class CommonDTO {

	
	public static class DeployLog{
		@NotNull
		private String service; //service
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private Integer id; //id
		public String getService() {
			return service;
		}
		public void setService(String service) {
			this.service = service;
		}
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
	}

	public static class Download{
		@NotNull
		private String deployFileName; //배포파일명

		public String getDeployFileName() {
			return deployFileName;
		}

		public void setDeployFileName(String deployFileName) {
			this.deployFileName = deployFileName;
		}
	}
	
}
