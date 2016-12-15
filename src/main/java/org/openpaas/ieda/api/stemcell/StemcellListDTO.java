package org.openpaas.ieda.api.stemcell;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class StemcellListDTO {
	private String name; //스템셀명

	@JsonProperty("operating_system")
	private String operatingSystem; //운영체계

	private String version; //스템셀 버전
	private String cid; //cid
	private List<HashMap<String, String>> deployments; //배포명
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public List<HashMap<String, String>> getDeployments() {
		return deployments;
	}
	public void setDeployments(List<HashMap<String, String>> deployments) {
		this.deployments = deployments;
	}
	
	
}
