package org.openpaas.ieda.web;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OpenpaasIedaWebApplication extends SpringBootServletInitializer {
	
    @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
 
    	LocalDirectoryConfiguration.initialize();
       	return builder.sources(OpenpaasIedaWebApplication.class);
	}

	public static void main(String[] args) {
    	LocalDirectoryConfiguration.initialize();
    	SpringApplication.run(OpenpaasIedaWebApplication.class, args);
    }
    
    @Bean
    public ModelMapper modelMapper() {
    	return new ModelMapper();
    }

}
