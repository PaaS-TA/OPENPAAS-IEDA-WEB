package org.openpaas.ieda.web.config.bootstrap;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity(name="IEDA_BOOTSTRAP")
@Data
public class IEDABootstrapConfig {

	@Id @GeneratedValue
	//@OneToOne(fetch=FetchType.LAZY)
	//@JoinColumn(name="BOOTSTRAP_ID")
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Date createdDate;
	
	@Temporal(TemporalType.DATE)
	private Date updatedDate;
	
	private String iaas;
}
