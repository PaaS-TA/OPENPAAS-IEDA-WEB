package org.openpaas.ieda.api.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class SecurityAuthenticationFailure implements AuthenticationFailureHandler {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로그인 인증에 실패 했을 경우 비즈니스 로직 구현
	 * @title               : onAuthenticationFailure
	***************************************************/
	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException exception)
			throws IOException, ServletException {
		String exceptionmsgname = exception.getMessage(); // 로그인 화면에 표시할 인증오류
		
		if("Maximum sessions of 1 for this principal exceeded".equals(exceptionmsgname)){
			res.sendRedirect("/login?code=loging"); // 로그인 중복 화면을 표시한다.
		}else{
			res.sendRedirect("/login?code=authFail"); // 로그인 실패 화면을 표시한다.
		}
			
	}	
}