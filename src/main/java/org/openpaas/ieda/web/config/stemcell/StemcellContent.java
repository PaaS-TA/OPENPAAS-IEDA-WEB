package org.openpaas.ieda.web.config.stemcell;

import lombok.Data;

@Data
public class StemcellContent {
	private String key;
	private String lastModified;
	private String etag;
	private String size;
	private String storageClass;
	
	private int recid;
	private String os;
	private String osVersion;
	private String iaas;
	private String stemcellFileName;
	private String stemcellVersion;
	private String isExisted;
}
