package org.openpaas.ieda.web.config.socket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/socket", "/stemcell", "/bootstrap");
		config.setApplicationDestinationPrefixes("/app", "/send");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint( "/stemcellUploading"
							, "/stemcellDownloading"
							, "/stemcellDelete"
							, "/releaseUploading"
							, "/releaseDownloading"
							, "/releaseDelete"
							, "/bootstrapInstall"
							, "/bootstrapDelete"
							).withSockJS();
	}

}