package org.openpaas.ieda.api.config.security;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecuritySessionListener implements HttpSessionListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(SecuritySessionListener.class);

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("==== Session is created ==== : ");
		}
			event.getSession().setMaxInactiveInterval(10 * 60);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("==== Session is destroyed ====");
		}
	}


}
