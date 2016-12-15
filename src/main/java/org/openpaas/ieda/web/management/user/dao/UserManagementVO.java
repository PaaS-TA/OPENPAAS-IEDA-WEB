package org.openpaas.ieda.web.management.user.dao;

import java.util.Date;

public class UserManagementVO {
	
	private Integer recid;

	private String userId; // 사용자 아이디

	private String userName; // 사용자 이름

	private String userPassword; // 사용자 패스워드

	private String email; // 사용자 이메일

	private String roleId; // 권한 아이디
	
	private String roleName;

	private String initPassYn; // 허용 거부

	private String createUserId; // 생성한 사용자

	private String updateUserId; // 수정한 사용자

	private Date createDate; // 생성일자
	
	private Date updateDate; // 수정일자
	
	public Integer getRecid() {
		return recid;
	}

	public void setRecid(Integer recid) {
		this.recid = recid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getInitPassYn() {
		return initPassYn;
	}

	public void setInitPassYn(String initPassYn) {
		this.initPassYn = initPassYn;
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

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
