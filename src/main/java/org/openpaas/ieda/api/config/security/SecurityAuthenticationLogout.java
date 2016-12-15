package org.openpaas.ieda.api.config.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class SecurityAuthenticationLogout implements LogoutHandler {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SecurityAuthenticationLogout.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로그아웃이 실행되는 핸들러
	 * @title               : logout
	***************************************************/
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
			try {
				response.sendRedirect("/login?code=logout");
			} catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
	}
	
}
