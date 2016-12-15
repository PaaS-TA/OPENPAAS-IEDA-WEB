package org.openpaas.ieda.web.deploy.servicepack.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class ServicePackVO {
	private String recid;//recid
	private int id;//id
	private String iaas; //iaas 타입
	private String deploymentName; //배포 명
	private String deploymentFile; //배포 파일 명
	private String createUserId;//IAAS 유형
	private String deployStatus; //배포상태
	private String updateUserId;//IAAS 유형
	private Date createDate; // 생성일자
	private Date updateDate; // 수정일자
	
	public String getRecid() {
		return recid;
	}
	public void setRecid(String recid) {
		this.recid = recid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIaas() {
		return iaas;
	}
	public void setIaas(String iaas) {
		this.iaas = iaas;
	}
	public String getDeploymentName() {
		return deploymentName;
	}
	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	public String getDeploymentFile() {
		return deploymentFile;
	}
	public void setDeploymentFile(String deploymentFile) {
		this.deploymentFile = deploymentFile;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
	}
	public String getCreateDate() {
		String createDate2 = "";
		if(createDate != null  ){
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
				createDate2 = format.format(createDate);
		}
		return createDate2;
	}
	public void setCreateDate(Date createDate) {
		if(createDate == null) {
			this.createDate = null;
		} else {
			this.createDate = new Date(createDate.getTime());
		}
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public String getUpdateDate() {
		String updateDate2 = "";
		if(updateDate != null  ){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
			updateDate2 = format.format(updateDate);
		}
		return updateDate2;
	}
	public void setUpdateDate(Date updateDate) {
		if(updateDate == null) {
			this.updateDate = null;
		} else {
			this.updateDate = new Date(updateDate.getTime());
		}
	}
	
	
}