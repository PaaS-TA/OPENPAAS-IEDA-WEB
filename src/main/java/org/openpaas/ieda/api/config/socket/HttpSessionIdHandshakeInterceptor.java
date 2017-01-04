package org.openpaas.ieda.api.config.socket;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class HttpSessionIdHandshakeInterceptor implements HandshakeInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpSessionIdHandshakeInterceptor.class);

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {

		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			HttpSession session = servletRequest.getServletRequest().getSession(false);
			if (session != null) {
				attributes.put("HTTPSESSIONID", session.getId());

				long now = new Date().getTime();
				long lastAccessed = session.getLastAccessedTime();
				long timeoutPeriod = session.getMaxInactiveInterval();
				long remainingTime = ((timeoutPeriod * 1000) - (now - lastAccessed)) / 1000;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Remaining time is " + remainingTime + " seconds");
				}

				//platform 설치 requestMapping value값은  deploy/{platform}/으로 규칙
				String uri= request.getURI().getPath();
				String menu = uri.split("/")[2];
				int maxInactiveInterval= 30*60;
				
				if( uri.indexOf("/install") > -1 ){
					if( menu.toLowerCase().equals("bootstrap") || menu.toLowerCase().equals("bosh") ){
						maxInactiveInterval = 60*60;
					}else if( menu.toLowerCase().equals("cf") || menu.toLowerCase().equals("diego")
							|| menu.toLowerCase().equals("cfdiego") || menu.toLowerCase().equals("servicepack") ){
						maxInactiveInterval = 180*60;
					}
				}else if( uri.indexOf("/systemRelease") > -1 || uri.indexOf("/release") > -1 || uri.indexOf("/stemcell")  > -1){
					//stemcell 및 release upload/download
					maxInactiveInterval= 20*60;
				}else{
					//delete
					maxInactiveInterval = 30*60;
				}
				session.setMaxInactiveInterval(maxInactiveInterval);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug( menu+" Websocket session time : " + maxInactiveInterval );
				}
			}

		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {
	}

}
