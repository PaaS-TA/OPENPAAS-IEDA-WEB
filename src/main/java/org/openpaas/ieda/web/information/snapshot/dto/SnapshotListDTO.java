package org.openpaas.ieda.web.information.snapshot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SnapshotListDTO {
	private Integer recid;//recid
	private String deploymentName;//배포명
	private String job;//job
	private String index;//index
	private String uuid;//uuid
	@JsonProperty("snapshot_cid")
	private String snapshotCid;//스냅샷 cid
	@JsonProperty("created_at")
	private String createdAt;
	private String clean;
	
	
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public String getDeploymentName() {
		return deploymentName;
	}
	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getSnapshotCid() {
		return snapshotCid;
	}
	public void setSnapshotCid(String snapshotCid) {
		this.snapshotCid = snapshotCid;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getClean() {
		return clean;
	}
	public void setClean(String clean) {
		this.clean = clean;
	}
	
}
