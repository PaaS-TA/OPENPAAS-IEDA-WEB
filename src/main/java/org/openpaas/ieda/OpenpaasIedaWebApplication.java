package org.openpaas.ieda;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ComponentScan
@Configuration
@EnableAutoConfiguration
public class OpenpaasIedaWebApplication implements CommandLineRunner {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 디렉토리 생성 요청
	 * @title               : main
	 * @return            : void
	***************************************************/
	public static void main(String[] args) {
		
		//LocalDirectoryConfiguration.initialize();

        //spring boot 어플리케이션 구동
        SpringApplication app = new SpringApplication(OpenpaasIedaWebApplication.class);

        app.addListeners(new ApplicationPidFileWriter("app.pid"));
        app.run(args);

    }
	
	@Override
	public void run(String... args) throws Exception {
		LocalDirectoryConfiguration.initialize();		
	}
    
    @Bean
    public ModelMapper modelMapper() {
    	//ModelMapper 빈 등록
    	return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
    	//ObjectMapper 빈 등록
    	return new ObjectMapper();
    }
    

}
