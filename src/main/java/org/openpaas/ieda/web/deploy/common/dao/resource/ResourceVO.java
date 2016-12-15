package org.openpaas.ieda.web.deploy.common.dao.resource;

import java.util.Date;

public class ResourceVO {
	
	private Integer id;//id
	private String deployType;//id
	private String boshPassword;//VM 비밀번호
	private String stemcellName;//스템셀명
	private String stemcellVersion;//스템셀 버전
	private String smallFlavor;//small Resource type
	private String mediumFlavor;//medium Resource type
	private String largeFlavor;//large Resource type
	private String runnerFlavor;//runner Resource type
	private Integer smallCpu;
	private Integer smallRam;
	private Integer smallDisk;
	private Integer mediumCpu;
	private Integer mediumRam;
	private Integer mediumDisk;
	private Integer largeCpu;
	private Integer largeRam;
	private Integer largeDisk;
	private Integer runnerCpu;
	private Integer runnerRam;
	private Integer runnerDisk;
	private String createUserId;
	private String updateUserId;
	private Date createDate; // 생성일자
	private Date updateDate; // 수정일자

	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDeployType() {
		return deployType;
	}
	public void setDeployType(String deployType) {
		this.deployType = deployType;
	}
	public String getBoshPassword() {
		return boshPassword;
	}
	public void setBoshPassword(String boshPassword) {
		this.boshPassword = boshPassword;
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
	public Integer getSmallCpu() {
		return smallCpu;
	}
	public void setSmallCpu(Integer smallCpu) {
		this.smallCpu = smallCpu;
	}
	public Integer getSmallRam() {
		return smallRam;
	}
	public void setSmallRam(Integer smallRam) {
		this.smallRam = smallRam;
	}
	public Integer getSmallDisk() {
		return smallDisk;
	}
	public void setSmallDisk(Integer smallDisk) {
		this.smallDisk = smallDisk;
	}
	public Integer getMediumCpu() {
		return mediumCpu;
	}
	public void setMediumCpu(Integer mediumCpu) {
		this.mediumCpu = mediumCpu;
	}
	public Integer getMediumRam() {
		return mediumRam;
	}
	public void setMediumRam(Integer mediumRam) {
		this.mediumRam = mediumRam;
	}
	public Integer getMediumDisk() {
		return mediumDisk;
	}
	public void setMediumDisk(Integer mediumDisk) {
		this.mediumDisk = mediumDisk;
	}
	public Integer getLargeCpu() {
		return largeCpu;
	}
	public void setLargeCpu(Integer largeCpu) {
		this.largeCpu = largeCpu;
	}
	public Integer getLargeRam() {
		return largeRam;
	}
	public void setLargeRam(Integer largeRam) {
		this.largeRam = largeRam;
	}
	public Integer getLargeDisk() {
		return largeDisk;
	}
	public void setLargeDisk(Integer largeDisk) {
		this.largeDisk = largeDisk;
	}
	public Integer getRunnerCpu() {
		return runnerCpu;
	}
	public void setRunnerCpu(Integer runnerCpu) {
		this.runnerCpu = runnerCpu;
	}
	public Integer getRunnerRam() {
		return runnerRam;
	}
	public void setRunnerRam(Integer runnerRam) {
		this.runnerRam = runnerRam;
	}
	public Integer getRunnerDisk() {
		return runnerDisk;
	}
	public void setRunnerDisk(Integer runnerDisk) {
		this.runnerDisk = runnerDisk;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public void setUpdateDate(Date updateDate) {
		if(updateDate == null) {
			this.updateDate = null;
		} else {
			this.updateDate = new Date(updateDate.getTime());
		}
	}

	public Date getUpdateDate() {
		if(updateDate == null) {
			return null;
		} else {
			return new Date(updateDate.getTime());
		}
	}

	public Date getCreateDate() {
		if(createDate == null) {
			return null;
		} else {
			return new Date(createDate.getTime());
		}
	}
	
	public void setCreateDate(Date createDate) {
		if(createDate == null) {
			this.createDate = null;
		} else {
			this.createDate = new Date(createDate.getTime());
		}
	}
}
