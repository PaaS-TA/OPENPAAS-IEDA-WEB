package org.openpaas.ieda.api.deployment;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class DeploymentDTO {
	private String name; //배포 이름
	private List<HashMap<String, String>> releases; //릴리즈 정보
	private List<HashMap<String, String>> stemcells; //스템셀 정보
	@JsonProperty("cloud_config")
	private String cloudConfig; //cloud 설정

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<HashMap<String, String>> getReleases() {
		return releases;
	}

	public void setReleases(List<HashMap<String, String>> releases) {
		this.releases = releases;
	}

	public List<HashMap<String, String>> getStemcells() {
		return stemcells;
	}

	public void setStemcells(List<HashMap<String, String>> stemcells) {
		this.stemcells = stemcells;
	}

	public String getCloudConfig() {
		return cloudConfig;
	}

	public void setCloudConfig(String cloudConfig) {
		this.cloudConfig = cloudConfig;
	}
	
	
}
