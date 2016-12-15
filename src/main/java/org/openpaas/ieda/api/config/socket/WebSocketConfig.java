package org.openpaas.ieda.api.config.socket;


import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker( "/socket"
								, "/config/stemcell/download/logs"
								, "/config/stemcell/download/socket/downloadStemcell"
								, "/config/systemRelease/regist/socket/logs"
								, "/config/systemRelease/regist/download/logs"
								, "/deploy/bootstrap/install/logs"
								, "/deploy/bootstrap/delete/logs"
								, "/deploy/bosh/install/logs"
								, "/deploy/bosh/delete/logs"
								, "/deploy/cf/install/logs"
								, "/deploy/cf/delete/logs"
								, "/deploy/diego/install/logs"
								, "/deploy/diego/delete/logs"
								, "/deploy/cfDiego/install/logs"
								, "/deploy/cfDiego/delete/logs"
								, "/info/stemcell/upload/logs"
								, "/info/stemcell/delete/logs"
								, "/info/release/upload/socket/logs"
								, "/info/release/delete/socket/logs"
								, "/info/task/list/eventLog/socket"
								, "/info/vms/vmLogs/socket"
								, "/info/vms/snapshotLog/socket"
								, "/info/property/modify/socket"
								, "/deploy/servicePack/install/logs"
								, "/deploy/servicePack/delete/logs"
								); 
		config.setApplicationDestinationPrefixes("/app", "/send");
		config.setUserDestinationPrefix("/user");
	}

	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint( "/config/stemcell/download/stemcellDownloading"
							, "/config/systemRelease/regist/download/releaseDownloading"
							, "/deploy/bootstrap/install/bootstrapInstall"
							, "/deploy/bootstrap/delete/instance"
							, "/deploy/bosh/install/boshInstall"
							, "/deploy/bosh/delete/instance"
							, "/deploy/cf/install/cfInstall"
							, "/deploy/cf/delete/instance"
							, "/deploy/diego/install/diegoInstall"
							, "/deploy/diego/delete/instance"
							, "/deploy/cfDiego/install/cfDiegoinstall"
							, "/deploy/cfDiego/delete/instance"
							, "/info/stemcell/upload/stemcellUploading"
							, "/info/stemcell/delete/stemcellDelete"
							, "/info/release/upload/releaseUploading"
							, "/info/release/delete/releaseDelete"
							, "/info/task/list/eventLog/task"
							, "/info/vms/vmLogs/job"
							, "/info/vms/snapshotLog/snapshotTaking"
							, "/info/property/modify/createProperty"
							, "/deploy/servicePack/install/servicepackInstall"
							, "/deploy/servicePack/delete/instance"
							).withSockJS().setInterceptors(httpSessionIdHandshakeInterceptor());

	}
	
	@Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        return true;
    }

	@Bean
    public HttpSessionIdHandshakeInterceptor httpSessionIdHandshakeInterceptor() {
        return new HttpSessionIdHandshakeInterceptor();
    }
	
}