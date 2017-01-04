package org.openpaas.ieda.web.management.auth.dao;

import java.util.Date;
import java.util.List;

public class AuthManagementVO {
	
	private Integer recid;
	
	private int roleId; // 롤 아이디
	
	private String roleName; // 롤 이름
	
	private String roleDescription; // 롤 설명
	
	private List<String> activeYn; // 허용 거부
	
	private String createUserId; // 생성한 사용자

	private String updateUserId; // 수정한 사용자
	
	private Date createDate; // 생성일자
	
	private Date updateDate; // 수정일자
	
	private String authCode;//권한코드
	
	private String seq;//seq
	
	public Integer getRecid() {
		return recid;
	}
	
	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	
	public int getRoleId() {
		return roleId;
	}
	
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	
	
	public String getRoleName() {
		return roleName;
	}
	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public String getRoleDescription() {
		return roleDescription;
	}
	
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}	
	
	public List<String> getActiveYn() {
		return activeYn;
	}
	
	public void setActiveYn(List<String> activeYn) {
		this.activeYn = activeYn;
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

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
	
}
