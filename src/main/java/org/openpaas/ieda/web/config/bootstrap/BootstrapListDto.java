package org.openpaas.ieda.web.config.bootstrap;

import java.util.Date;

import lombok.Data;

@Data
public class BootstrapListDto {

	private Integer recid;
	private Integer id;
	private String iaas;
	private String directorPrivateIp;
	private String directorPublicIp;
	private Date createdDate;
	private Date updatedDate;
}
