package org.openpaas.ieda.api.director.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectorInfoDTO {
	private String name; //관리자 이름
	private String uuid; //관리자 UUID
	private String version; //버전
	private String user; //관리자 계정
	private String cpi; //CPI
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getCpi() {
		return cpi;
	}
	public void setCpi(String cpi) {
		this.cpi = cpi;
	}
	
	
}
