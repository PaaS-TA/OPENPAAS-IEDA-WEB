package org.openpaas.ieda;

import javax.servlet.MultipartConfigElement;

import org.openpaas.ieda.api.config.security.SecurityAuthenticationFailure;
import org.openpaas.ieda.api.config.security.SecurityAuthenticationLogout;
import org.openpaas.ieda.api.config.security.SecurityAuthenticationSuccess;
import org.openpaas.ieda.api.config.security.SecurityPath;
import org.openpaas.ieda.api.config.security.SecuritySessionListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAsync
@ComponentScan
@Configuration
@EnableAutoConfiguration
public class TestBeansConfiguration {
	
	@Bean
    public ObjectMapper objectMapper() {
    	//ObjectMapper 빈 등록
    	return new ObjectMapper();
    }
    
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
    

}
