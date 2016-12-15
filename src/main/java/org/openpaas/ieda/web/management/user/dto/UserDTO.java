package org.openpaas.ieda.web.management.user.dto;

public class UserDTO {
		
	public static class SavePassword {
		
		private String userId;//사용자 아이디
		private String password; //비밀번호
		
		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
	
	
}
