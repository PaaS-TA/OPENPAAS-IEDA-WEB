package org.openpaas.ieda.web.management.user.dto;

public class UserManagementDTO {
	public static class Regist {

		private String userId; // 사용자 아이디

		private String userName; // 사용자 이름

		private String userPassword; // 사용자 패스워드

		private String email; // 사용자 이메일

		private String roleId; // 권한 아이디

		private String initPassYn; // 허용 거부

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
	}
}
