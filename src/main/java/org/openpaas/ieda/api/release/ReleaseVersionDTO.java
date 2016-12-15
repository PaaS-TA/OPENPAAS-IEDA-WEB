package org.openpaas.ieda.api.release;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseVersionDTO {

	private String version; //릴리즈버전
	
	@JsonProperty("commit_hash")
	private String commitHash; //commitHash
	
	@JsonProperty("uncommitted_changes")
	private Boolean uncommittedChanges; //uncommittedChanges
	
	@JsonProperty("currently_deployed")
	private Boolean currentlyDeployed; //배포 사용중 여부
	
	@JsonProperty("job_names")
	private List<String> jobNames; //Job템플릿

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCommitHash() {
		return commitHash;
	}

	public void setCommitHash(String commitHash) {
		this.commitHash = commitHash;
	}

	public Boolean getUncommittedChanges() {
		return uncommittedChanges;
	}

	public void setUncommittedChanges(Boolean uncommittedChanges) {
		this.uncommittedChanges = uncommittedChanges;
	}

	public Boolean getCurrentlyDeployed() {
		return currentlyDeployed;
	}

	public void setCurrentlyDeployed(Boolean currentlyDeployed) {
		this.currentlyDeployed = currentlyDeployed;
	}

	public List<String> getJobNames() {
		return jobNames;
	}

	public void setJobNames(List<String> jobNames) {
		this.jobNames = jobNames;
	}
	
	
	
}
