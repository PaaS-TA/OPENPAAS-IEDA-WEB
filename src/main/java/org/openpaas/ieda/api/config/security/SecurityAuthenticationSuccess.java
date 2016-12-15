package org.openpaas.ieda.api.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openpaas.ieda.web.management.user.dao.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.RequestCache;

public class SecurityAuthenticationSuccess extends SavedRequestAwareAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
    
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로그인 인증에 성공 했을 경우 비즈니스 로직 구현
	 * @title               : onAuthenticationSuccess
	***************************************************/
	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws IOException, ServletException  {
		// AuthenticationManagerBuilder 에서 취득한 사용자정보에서 패스워드 변경 여부를 체크한다.
		SecurityUserDetails userDetail = (SecurityUserDetails)auth.getDetails();
		UserVO user = userDetail.getUser();
		req.getSession().setAttribute("id", userDetail.getUserId());
		// 초기패스워드를 변경하지 않았다면, 패스워드 리셋 화면을 표시한다.
		if(user.getInitPassYn()!=null && "N".equals(user.getInitPassYn())){
			res.sendRedirect("/common/user/resetPassword");
		} else {
			res.sendRedirect("/");
		}				
	}

	@Override
	public void setRequestCache(RequestCache requestCache) {
		super.setRequestCache(requestCache);
	}
	
}