/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.setting;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import lombok.Data;

import org.hibernate.validator.constraints.NotBlank;


/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */
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
		
		@NotBlank
		private Integer directorPort;
		
		@NotBlank
		private String  defaultYn;
	}
	
	@Data
	public static class Update {
		@NotBlank
		@Size(min=4)
		private String  userId;
		
		@NotBlank
		@Size(min=4)
		private String  userPassword;
		
		@NotBlank
		private String  defaultYn;
	}

	@Data
	public static class Response {
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
