package org.openpaas.ieda.api.config.security;

import java.util.Collection;

import org.openpaas.ieda.web.management.user.dao.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private final String username;//사용자 이름
	private final String password;//사용자 password 
	Collection<? extends GrantedAuthority> authorities; //권한

	private String userId; //사용자id
	private UserVO user; //UserVO
	
	public SecurityUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
			String userId, UserVO user) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.userId = userId;
		this.user = user;
	}
	public SecurityUserDetails(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public SecurityUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities ) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int hashCode() {
		return (userId != null? userId.hashCode(): 0);
	}
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserVO getUser() {
		return user;
	}

	public void setUser(UserVO user) {
		this.user = user;
	}
}
