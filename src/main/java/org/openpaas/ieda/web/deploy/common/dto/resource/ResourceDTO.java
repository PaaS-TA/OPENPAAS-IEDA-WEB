package org.openpaas.ieda.web.deploy.common.dto.resource;

import javax.validation.constraints.NotNull;

public class ResourceDTO {
	@NotNull
	private String id; //id
	@NotNull
	private String iaas; //IaaS
	@NotNull
	private String platform; //플랫폼 구분
	// 5. 리소스 정보
	@NotNull
	private String stemcellName; //스템셀명
	@NotNull
	private String stemcellVersion; //스템셀버전
	@NotNull
	private String boshPassword; //VM 비밀번호
	private String smallFlavor;//small 인스턴스 유형
	private String mediumFlavor;//medium 인스턴스 유형
	private String largeFlavor;//large 인스턴스 유형
	private String runnerFlavor;//runner 인스턴스 유형
	private String smallCpu; //small 인스턴스 유형 Cpu
	private String smallRam;//small 인스턴스 유형 Ram
	private String smallDisk;//small 인스턴스 유형 Disk
	private String mediumCpu;//medium 인스턴스 유형 Cpu
	private String mediumRam;//medium 인스턴스 유형 Ram
	private String mediumDisk;//medium 인스턴스 유형 Disk
	private String largeCpu;//large 인스턴스 유형 Cpu
	private String largeRam;//large 인스턴스 유형 Ram
	private String largeDisk;//large 인스턴스 유형 Disk
	private String runnerCpu;//runner 인스턴스 유형 Cpu
	private String runnerRam;//runner 인스턴스 유형 Ram
	private String runnerDisk;//runner 인스턴스 유형 Disk
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIaas() {
		return iaas;
	}
	public void setIaas(String iaas) {
		this.iaas = iaas;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getStemcellName() {
		return stemcellName;
	}
	public void setStemcellName(String stemcellName) {
		this.stemcellName = stemcellName;
	}
	public String getStemcellVersion() {
		return stemcellVersion;
	}
	public void setStemcellVersion(String stemcellVersion) {
		this.stemcellVersion = stemcellVersion;
	}
	public String getBoshPassword() {
		return boshPassword;
	}
	public void setBoshPassword(String boshPassword) {
		this.boshPassword = boshPassword;
	}
	public String getSmallFlavor() {
		return smallFlavor;
	}
	public void setSmallFlavor(String smallFlavor) {
		this.smallFlavor = smallFlavor;
	}
	public String getMediumFlavor() {
		return mediumFlavor;
	}
	public void setMediumFlavor(String mediumFlavor) {
		this.mediumFlavor = mediumFlavor;
	}
	public String getLargeFlavor() {
		return largeFlavor;
	}
	public void setLargeFlavor(String largeFlavor) {
		this.largeFlavor = largeFlavor;
	}
	public String getRunnerFlavor() {
		return runnerFlavor;
	}
	public void setRunnerFlavor(String runnerFlavor) {
		this.runnerFlavor = runnerFlavor;
	}
	public String getSmallCpu() {
		return smallCpu;
	}
	public void setSmallCpu(String smallCpu) {
		this.smallCpu = smallCpu;
	}
	public String getSmallRam() {
		return smallRam;
	}
	public void setSmallRam(String smallRam) {
		this.smallRam = smallRam;
	}
	public String getSmallDisk() {
		return smallDisk;
	}
	public void setSmallDisk(String smallDisk) {
		this.smallDisk = smallDisk;
	}
	public String getMediumCpu() {
		return mediumCpu;
	}
	public void setMediumCpu(String mediumCpu) {
		this.mediumCpu = mediumCpu;
	}
	public String getMediumRam() {
		return mediumRam;
	}
	public void setMediumRam(String mediumRam) {
		this.mediumRam = mediumRam;
	}
	public String getMediumDisk() {
		return mediumDisk;
	}
	public void setMediumDisk(String mediumDisk) {
		this.mediumDisk = mediumDisk;
	}
	public String getLargeCpu() {
		return largeCpu;
	}
	public void setLargeCpu(String largeCpu) {
		this.largeCpu = largeCpu;
	}
	public String getLargeRam() {
		return largeRam;
	}
	public void setLargeRam(String largeRam) {
		this.largeRam = largeRam;
	}
	public String getLargeDisk() {
		return largeDisk;
	}
	public void setLargeDisk(String largeDisk) {
		this.largeDisk = largeDisk;
	}
	public String getRunnerCpu() {
		return runnerCpu;
	}
	public void setRunnerCpu(String runnerCpu) {
		this.runnerCpu = runnerCpu;
	}
	public String getRunnerRam() {
		return runnerRam;
	}
	public void setRunnerRam(String runnerRam) {
		this.runnerRam = runnerRam;
	}
	public String getRunnerDisk() {
		return runnerDisk;
	}
	public void setRunnerDisk(String runnerDisk) {
		this.runnerDisk = runnerDisk;
	}
}
