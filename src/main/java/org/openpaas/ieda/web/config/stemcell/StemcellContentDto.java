package org.openpaas.ieda.web.config.stemcell;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

public class StemcellContentDto {

	@Data
	public static class Upload {
		@NotBlank
		private String fileName;
	}

	@Data
	public static class Delete {
		@NotBlank
		private String fileName;

		@NotBlank
		private String version;
	}
}
