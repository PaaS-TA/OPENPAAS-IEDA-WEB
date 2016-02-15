package org.openpaas.ieda.web.config.setting;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotBlank;
import lombok.Data;

public class IEDADirectorConfigDto {
	
	@Data
	public static class Create {
		
		@NotBlank
		@Size(min=4)
		private String  userId;
		
		@NotBlank
		@Size(min=4)
		private String  userPassword;
		
		@NotBlank
		private String  directorUrl;
		
		@NotNull
		private Integer directorPort;

	}
	
	@Data
	public static class Update {
		
		private Integer iedaDirectorConfigSeq;
		
		@NotBlank
		@Size(min=4)
		private String  userId;
		
		@NotBlank
		@Size(min=4)
		private String  userPassword;
	}

	@Data
	public static class Response {
		private int recid;
		
		private Integer iedaDirectorConfigSeq;
		
		private String  userId;
		
		private String  directorName;
		
		private String  directorVersion;
		
		private String  directorUrl;
		
		private Integer directorPort;
		
		private String  directorUUID;
		
		private String  directorCpi;
		
		private String  currentDeployment;
		
		@NotBlank
		private String  defaultYn;
		
		@Temporal(TemporalType.DATE)
		private Date    createdDate;
		
		@Temporal(TemporalType.DATE)
		private Date    updatedDate;
	}
	
}
