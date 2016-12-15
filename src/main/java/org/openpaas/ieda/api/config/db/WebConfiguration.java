package org.openpaas.ieda.api.config.db;

import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class WebConfiguration {
	
	
	/*******************************************************
	 * H2 데이터베이스 콘솔 Database Console
	 * SpringBootdptj H2를 사용할 경우 구동시에 db상태를 볼 수 있다.
	 * http://localhost:8080/console/
	 *
	 *******************************************************/
    @Bean
    ServletRegistrationBean h2servletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
        registrationBean.addUrlMappings("/console/*");
        return registrationBean;
    }
    
    
    
    
}