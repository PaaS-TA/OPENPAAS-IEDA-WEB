package org.openpaas.ieda.api.config.security;

import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.management.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class SecurityPath  {

	@Autowired private UserDAO dao;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Security 설정을 인증포로토콜에 설정
	 * @title               : setSecurityPath
	 * @return            : HttpSecurity
	***************************************************/
	public HttpSecurity setSecurityPath(HttpSecurity http) {
		
		// role_code 
		int codeParentId = 10000;

		List<HashMap<String, String>> pathList = dao.getSecurityPathMapList(codeParentId);
		if (pathList == null)
			throw new CommonException("notfound.security.exception", " getSecurityPathMapList is NULL", HttpStatus.NOT_FOUND);
		
		String path = "";
		String roleDetail = "";

		for (HashMap<String, String> codeObject : pathList) {
			path = (String) codeObject.get("code_description");
			roleDetail = (String) codeObject.get("code_name");
			try {
				http.authorizeRequests().antMatchers(path).access("hasAuthority('" + roleDetail + "')");
			} catch (Exception e) {
				throw new CommonException("notfound.security.exception", " getSecurityPathMapList is NULL", HttpStatus.NOT_FOUND);
			}
		}
		return http;
	}
}

