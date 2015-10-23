package org.openpaas.ieda.api;

import java.util.Map;

import lombok.Data;

@Data
public class Info {
	private String name;
	private String uuid;
	private String version;
	private String user;
	private String cpi;
	private Map<String, Object> features;
	private Map<String, Object> user_authenticate;
}



