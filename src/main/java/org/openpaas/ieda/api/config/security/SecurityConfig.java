package org.openpaas.ieda.api.config.security;

import java.util.Arrays;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //Spring Method level 의 권한설정을 유효하게 하는 옵션 값 (@secure)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired SessionRegistry sessionRegistry;
	@Autowired SecurityAuthenticationSuccess successHandler;
	@Autowired SecurityAuthenticationFailure failureHandler;
    @Autowired SecurityAuthenticationProvider authenticationProvider;
    @Autowired SecurityAuthenticationLogout logoutSuccessHandler;
    @Autowired SecurityPath securityPath;
    
	@Bean
	SecurityAuthenticationLogout logoutSuccessHandler() {
        return new SecurityAuthenticationLogout();
    }

	@Bean
	SecurityAuthenticationSuccess successHander() {
        return new SecurityAuthenticationSuccess();
    }	
	
	@Bean
	SecurityAuthenticationFailure failureHandler() {
        return new SecurityAuthenticationFailure();
    }	
	
	@Bean
    SessionRegistry sessionRegistry() {            
        return new SessionRegistryImpl();
    }

	@Bean
	public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
	    return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
	}
	
	@Bean
	public SecuritySessionListener securitySessionListener(){
		return new SecuritySessionListener();
	}
	
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		//File 사이즈 설정
	    MultipartConfigFactory factory = new MultipartConfigFactory();
	    factory.setMaxFileSize("5000000000");
	    factory.setMaxRequestSize("5000000000");

	    return factory.createMultipartConfig();
	}

	@Bean
	public MultipartResolver multipartResolver() {
	    return new StandardServletMultipartResolver();
	}
	
	@Bean
	SecurityPath securityPath() {
        return new SecurityPath();
    }
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Spring Security 권한정보 설정
	 * @title               : configure
	***************************************************/
	@Override
	protected void configure(HttpSecurity httpeScurity) throws Exception {
		HttpSecurity http = httpeScurity; 
		
		http.authorizeRequests()
			.antMatchers("/css/**/*", "/js/**/*","/css/**", "/js/**", "/images/**", "/resources/**", "/webjars/**", "/jquery/**").permitAll();
		// URL 직접 접근에 대한 접근권한을 설정한다.
		http = securityPath.setSecurityPath(http);	
		http.authorizeRequests() 
				.antMatchers("/login").anonymous()
				.anyRequest().authenticated()
				.and()
			// 로그인 페이지에 대한 설정			
			.formLogin()
				.loginPage("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.successHandler(successHandler) // 로그인에 성공한 경우, AuthenticationSuccessHandler(커스텀 핸들러) 로 인증 이후 처리를 매핑한다.
				.failureHandler(failureHandler).permitAll()// 로그인에 실패한 경우, AuthenticationFailureHandler(커스텀 핸들러) 처리 핸들러를 매핑한다.
				.and()
			// 로그아웃에 대한 설정
			.logout()
				.logoutSuccessUrl("/login?code=logout")
				.invalidateHttpSession(true)
	            .deleteCookies("JSESSIONID")
	            .permitAll()
	            .and()
            .exceptionHandling() // 비정상 접근에 대해 로그아웃처리
        		.accessDeniedPage("/login?code=abuse")
	            .and()
        	.csrf().disable() //선택적 보안 옵션 해제
        	.httpBasic().disable(); //기본 옵션 해제
		

		//session Management
		http.sessionManagement()
			.invalidSessionUrl("/login?code=invalid")
			.sessionFixation().newSession()
			.maximumSessions(1)
			.maxSessionsPreventsLogin(true)
			.expiredUrl("/login?code=expire")
			.sessionRegistry(sessionRegistry);
		
	}	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Spring Security 사용자 인증
	 * @title               : authenticationManager
	***************************************************/
	@Override
	protected AuthenticationManager authenticationManager() {
		return new ProviderManager(Arrays.asList((SecurityAuthenticationProvider) authenticationProvider));		 
	}	
}