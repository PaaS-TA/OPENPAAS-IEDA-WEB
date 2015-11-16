package org.openpaas.ieda.web.config.stemcell;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity(name="IEDA_PUBLIC_STEMCELLS")
public class StemcellContent {
	
	@Id @GeneratedValue
	@Column(name="ID")
	private Integer recid;
	
	@Column(name="SUBLINK")
	private String key;
	
	@Transient
	private String lastModified;
	
	@Transient
	private String etag;
	
	@Column(name="SIZE")
	private String size;
	
	@Transient
	private String storageClass;
	
	@Column(name="OS")
	private String os;
	
	@Column(name="OS_VERSION")
	private String osVersion;
	
	@Column(name="IAAS")
	private String iaas;
	
	@Column(name="STEMCELL_FILENAME")
	private String stemcellFileName;
	
	@Column(name="STEMCELL_VERSION")
	private String stemcellVersion;
	
	@Transient
	private String isExisted;
	
	@Column(name="DOWNLOAD_STATUS")  // NULL, DOWNLOADING, DOWNLOADED
	private String downloadStatus; 
}
