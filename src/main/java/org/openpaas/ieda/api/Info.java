package org.openpaas.ieda.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Info {
	private String name;
	private String uuid;
	private String version;
	private String user;
	private String cpi;
}
