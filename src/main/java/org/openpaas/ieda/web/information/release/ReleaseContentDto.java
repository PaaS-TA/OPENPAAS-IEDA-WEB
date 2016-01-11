package org.openpaas.ieda.web.information.release;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

public class ReleaseContentDto {
	@Data
	public static class Upload{
		@NotBlank
		private String fileName;
	}
	
	@Data
	public static class Delete{
		@NotBlank
		private String fileName;
		
		@NotBlank
		private String version;
	}
	
	@Data
	public static class DeleteLocal{
		@NotBlank
		private String fileName;
	}
	
	@Data
	public static class Download {
		@NotBlank
		private String key;
		
		@NotBlank
		private String fileName;
		
		@NotBlank
		private String fileSize;
	}
}
