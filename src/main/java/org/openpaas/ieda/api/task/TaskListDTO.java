package org.openpaas.ieda.api.task;

public class TaskListDTO {
	
	private Integer recid;//recid
	private String id;//TaskID
	private String state;//상태
	private String description;//실행내용
	private Long timestamp;//시간
	private String result;//결과
	private String user;//사용자
	private String runTime;//시간
	private String started_at;
	private String deployment;

	public Integer getRecid() {
		return recid;
	}

	public void setRecid(Integer recid) {
		this.recid = recid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getRunTime() {
		return runTime;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public String getStarted_at() {
		return started_at;
	}

	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}

	public String getDeployment() {
		return deployment;
	}

	public void setDeployment(String deployment) {
		this.deployment = deployment;
	}
	
	
}
