package org.openpaas.ieda.web.code;

import lombok.Data;

import org.hibernate.validator.constraints.NotBlank;

public class IEDACommonCodeDto {

	@Data
	public static class Create {
		@NotBlank
		private String  codeName;
		
		@NotBlank
		private String  codeValue;
		
		private String  codeDescription;
		
		private Integer sortOrder;
		
		private Integer parentCodeIdx;
	}
	
	@Data
	public static class Update {
		private String  codeName;
		private String  codeValue;
		private String  codeDescription;
		private Integer sortOrder;
		private Integer parentCodeIdx;
	}	
	
	@Data
	public static class Response {
		private Integer codeIdx;
		private String  codeName;
		private String  codeValue;
		private String  codeDescription;
		private Integer sortOrder;
		private Integer parentCodeIdx;
	}
	
	
}
