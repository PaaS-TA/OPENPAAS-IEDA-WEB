package org.openpaas.ieda.web.common;

import java.nio.charset.Charset;

import java.security.Principal;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations="classpath:application_test.properties")
public class BaseTestController {
	
	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로그인 Test
	 * @title               : getLoggined
	 * @return            : Principal
	***************************************************/
	public Principal getLoggined() throws Exception {
		Authentication auth = new UsernamePasswordAuthenticationToken("admin", "admin");
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(auth);
		securityContext.getAuthentication().getPrincipal();
		
		return auth;
	}
}
