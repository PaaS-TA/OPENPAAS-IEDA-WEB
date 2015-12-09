package org.openpaas.ieda.api.director;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectorResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		log.info("# DirectorResponseErrorHandler STATUS CODE : " + response.getStatusCode());
		return RestErrorUtil.isError(response.getStatusCode());
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		log.info("# Response error: {} {}", response.getStatusCode(), response.getStatusText());
	}

}
