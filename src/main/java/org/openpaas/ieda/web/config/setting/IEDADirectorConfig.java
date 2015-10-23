/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.setting;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Entity(name="IEDA_DIRECTOR_CONFIG")
@Data
public class IEDADirectorConfig {
	
	@Id @GeneratedValue
	private Integer iedaDirectorConfigSeq;
	
	private String userId;
	
	private String userPassword;
	
	private String directorName;
	
	private String directorVersion;
	
	private String directorUrl;
	
	private Integer directorPort;
	
	private String directorUuid;
	
	private String directorCpi;
	
	private String currentDeployment;
	
	private String defaultYn;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
}
