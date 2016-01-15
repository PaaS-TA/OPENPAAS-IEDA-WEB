package org.openpaas.ieda.web.information.stemcell;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

public class StemcellParam {

	@Data
	public static class Upload {
		@NotBlank
		private String fileName;
	}

	@Data
	public static class Delete {
		@NotBlank
		private String stemcellName;

		@NotBlank
		private String version;
	}
}
