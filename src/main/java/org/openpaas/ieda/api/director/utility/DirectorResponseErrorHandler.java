package org.openpaas.ieda.api.director.utility;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;


public class DirectorResponseErrorHandler implements ResponseErrorHandler {
	
	private final static Logger LOG = LoggerFactory.getLogger(DirectorResponseErrorHandler.class);
	
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		///주어진 응답에 오류가 있는지 여부를 응답한다.
		if(LOG.isDebugEnabled()){
			LOG.debug("# DirectorResponseErrorHandler STATUS CODE : " + response.getStatusCode());
		}
		return RestErrorUtil.isError(response.getStatusCode());
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		//주어진 응답의 오류를 처리한다.
		if(LOG.isDebugEnabled()){
			LOG.debug("# Response error: {} {}"+ response.getStatusCode() + " : " + response.getStatusText());
		}
	}

}
