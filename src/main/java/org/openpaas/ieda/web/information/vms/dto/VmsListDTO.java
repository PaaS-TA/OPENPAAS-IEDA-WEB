package org.openpaas.ieda.web.information.vms.dto;

import javax.validation.constraints.NotNull;

public class VmsListDTO {

	private Integer recid; //recid
	@NotNull
	private String deploymentName; //배포명
	@NotNull
	private String jobName;//VM명
	@NotNull
	private String index;//VM 인덱스 번호
	private String state; // 상태
	private String jobState;//VM의 가동 상태 표시
	private String az; //az
	private String vmType; //VM 유형
	private String ips; //Ips
	private String load; //load
	private String cpuWait; //Cpu wait 사용률
	private String cpuSys; //cpu sys 사용률
	private String cpuUser; //cpu user 사용률
	private String diskSystem; //system 디스크 사용률
	private String diskEphemeral; //임시할당 디스크 사용률
	private String diskPersistent;  //영구할당 디스크 사용률
	private String memoryUsage; //메모리 사용률
	private String swapUsage; //swap 디스크 사용률
	
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
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public String getJobState() {
		return jobState;
	}
	public void setJobState(String jobState) {
		this.jobState = jobState;
	}
	public String getAz() {
		return az;
	}
	public void setAz(String az) {
		this.az = az;
	}
	public String getVmType() {
		return vmType;
	}
	public void setVmType(String vmType) {
		this.vmType = vmType;
	}
	public String getIps() {
		return ips;
	}
	public void setIps(String ips) {
		this.ips = ips;
	}
	public String getLoad() {
		return load;
	}
	public void setLoad(String load) {
		this.load = load;
	}
	public String getCpuWait() {
		return cpuWait;
	}
	public void setCpuWait(String cpuWait) {
		this.cpuWait = cpuWait;
	}
	public String getCpuSys() {
		return cpuSys;
	}
	public void setCpuSys(String cpuSys) {
		this.cpuSys = cpuSys;
	}
	public String getCpuUser() {
		return cpuUser;
	}
	public void setCpuUser(String cpuUser) {
		this.cpuUser = cpuUser;
	}
	public String getDiskSystem() {
		return diskSystem;
	}
	public void setDiskSystem(String diskSystem) {
		this.diskSystem = diskSystem;
	}
	public String getDiskEphemeral() {
		return diskEphemeral;
	}
	public void setDiskEphemeral(String diskEphemeral) {
		this.diskEphemeral = diskEphemeral;
	}
	public String getDiskPersistent() {
		return diskPersistent;
	}
	public void setDiskPersistent(String diskPersistent) {
		this.diskPersistent = diskPersistent;
	}
	public String getMemoryUsage() {
		return memoryUsage;
	}
	public void setMemoryUsage(String memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	public String getSwapUsage() {
		return swapUsage;
	}
	public void setSwapUsage(String swapUsage) {
		this.swapUsage = swapUsage;
	}
	
	
}
