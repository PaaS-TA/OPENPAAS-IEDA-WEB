package org.openpaas.ieda.web;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OpenpaasIedaWebApplication {
	
	public static void main(String[] args) {
		
        LocalDirectoryConfiguration.initialize();

        SpringApplication app = new SpringApplication(OpenpaasIedaWebApplication.class);

        app.addListeners(new ApplicationPidFileWriter("app.pid"));
        app.run(args);

    }
    
    @Bean
    public ModelMapper modelMapper() {
    	return new ModelMapper();
    }

}
