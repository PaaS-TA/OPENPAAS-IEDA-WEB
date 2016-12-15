package org.openpaas.ieda.api.config.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.web.management.user.dao.UserDAO;
import org.openpaas.ieda.web.management.user.dao.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.stereotype.Component;

@Component
public class SecurityAuthenticationProvider extends AbstractSecurityWebApplicationInitializer implements AuthenticationProvider {

	@Autowired private UserDAO dao;

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Spring Security 인증 Dependency 를 Override 한다 (사용자를 인증한다.)
	 * @title               : authenticate
	***************************************************/
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String userId = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();
		
		UserVO user = dao.selectUser(userId, password);
		
		if (user == null)
			throw new BadCredentialsException("BadCredentials");
		
		// 권한정보를 취득한다.
		Collection<? extends GrantedAuthority> authorities = getAuthorities(user);

		// token 의 본체가 되는 커스텀 인증객체를 생성한다.
		SecurityUserDetails iedaUserDetails = new SecurityUserDetails(user.getName(), password, authorities, userId, user);

		// token 생성
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userId, password,
				getAuthorities(user));

		result.setDetails(iedaUserDetails);

		return result;

	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Spring Security 인증 Dependency 를 Override 한다 (사용자를 인증한다.)
	 * @title               : supports
	***************************************************/
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : UserVO 객체에서 사용자권한을 추출하여, Spring Security 권한객체로 생성 후 리턴한다.
	 * @title               : getAuthorities
	 * @return            : Collection<? extends GrantedAuthority>
	***************************************************/
	private Collection<? extends GrantedAuthority> getAuthorities(UserVO user) {

		// 사용자의 권한상세 목록을 가져온다
		List<HashMap<String, String>> userRoleMapList = dao.getRoleDetailsByRoleId((Integer) user.getRoleId());

		// Security Dependency 의 인증 권한으로 매핑한다.
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(0);
		for (HashMap<String, String> userRole : userRoleMapList) {
			// 권한 상세 코드를 Security Dependency 인증 객체에 주입한다.
			authorities.add(new SimpleGrantedAuthority((String) userRole.get("auth_code")));
		}
		return authorities;
	}
	

}
