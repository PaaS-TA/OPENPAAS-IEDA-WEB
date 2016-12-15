package org.openpaas.ieda.web.common.dto;

import java.security.Principal;

import org.openpaas.ieda.api.config.security.SecurityUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class SessionInfoDTO {
	private String userId;
	
	public SessionInfoDTO(){
		// Spring Security Token에서 사용자아이디 정보를 얻는다.
		SecurityUserDetails userDetails = (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
		userId = userDetails.getUserId();
	}
	
	public SessionInfoDTO(Principal principal) {
		this.userId= principal.getName();
			userId = principal.getName();
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
}
