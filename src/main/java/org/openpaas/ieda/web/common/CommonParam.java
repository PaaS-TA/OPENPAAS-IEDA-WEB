package org.openpaas.ieda.web.common;

import javax.validation.constraints.NotNull;

import lombok.Data;

public class CommonParam {

	@Data
	public static class DeployLog{
		@NotNull
		private String service;
		@NotNull
		private String iaas;
		@NotNull
		private Integer id;
	}
}
