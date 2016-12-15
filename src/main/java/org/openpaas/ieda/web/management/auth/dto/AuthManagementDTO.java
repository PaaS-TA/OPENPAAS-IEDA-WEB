package org.openpaas.ieda.web.management.auth.dto;

import java.util.List;

public class AuthManagementDTO {
	
	public static class Regist {
	
		private String roleId; // 롤 아이디

		private String roleName; // 롤 이름

		private String roleDescription; // 롤 설명

		private List<String> activeYn; //허용 거부
		
		public String getRoleId() {
			return roleId;
		}

		public void setRoleId(String roleId) {
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
	}	
}
