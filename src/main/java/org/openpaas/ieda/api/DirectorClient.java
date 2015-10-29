package org.openpaas.ieda.api;


import java.net.URI;

import org.springframework.web.client.RestTemplate;

import lombok.Data;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Data
public class DirectorClient {

	private URI			root;
	private RestTemplate restTemplate;
	
	DirectorClient(URI root, RestTemplate restTemplate) {
		this.root = root;
		this.restTemplate = restTemplate;
	}

}
