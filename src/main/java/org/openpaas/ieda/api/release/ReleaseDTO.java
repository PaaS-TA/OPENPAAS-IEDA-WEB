package org.openpaas.ieda.api.release;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseDTO {

	private String name; //릴리즈 파일명
	@JsonProperty("release_versions")
	private List<ReleaseVersionDTO> releaseVersions; //릴리즈 파일크기
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ReleaseVersionDTO> getReleaseVersions() {
		return releaseVersions;
	}
	public void setReleaseVersions(List<ReleaseVersionDTO> releaseVersions) {
		this.releaseVersions = releaseVersions;
	}
	
	
}
