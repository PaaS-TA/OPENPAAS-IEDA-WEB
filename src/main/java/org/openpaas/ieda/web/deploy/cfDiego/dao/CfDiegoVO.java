package org.openpaas.ieda.web.deploy.cfDiego.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.openpaas.ieda.web.deploy.cf.dao.CfVO;
import org.openpaas.ieda.web.deploy.diego.dao.DiegoVO;

public class CfDiegoVO {
	
	private Integer id;//id
	private Integer recid;//recid
	private CfVO cfVo;//cfVO
	private DiegoVO diegoVo;//diegoVO
	private String iaasType;//iaas 유형
	private String deployStatus;//배포 상태
	private String createUserId;//생성자
	private String updateUserId;//수정자
	private Date createDate;//생성일
	private Date updateDate;//수정일
	
	public CfDiegoVO(){
		cfVo = new CfVO();
		diegoVo = new DiegoVO();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getRecid() {
		return recid;
	}
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	public CfVO getCfVo() {
		return cfVo;
	}
	public void setCfVo(CfVO cfVo) {
		this.cfVo = cfVo;
	}
	public DiegoVO getDiegoVo() {
		return diegoVo;
	}
	public void setDiegoVo(DiegoVO diegoVo) {
		this.diegoVo = diegoVo;
	}
	public String getIaasType() {
		return iaasType;
	}
	public void setIaasType(String iaasType) {
		this.iaasType = iaasType;
	}
	public String getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
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
