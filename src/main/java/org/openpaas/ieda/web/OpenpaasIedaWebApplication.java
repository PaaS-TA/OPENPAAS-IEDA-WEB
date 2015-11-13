package org.openpaas.ieda.web;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties(IEDAConfiguration.class)
public class OpenpaasIedaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenpaasIedaWebApplication.class, args);
    }
    
    @Bean
    public ModelMapper modelMapper() {
    	return new ModelMapper();
    }

}
