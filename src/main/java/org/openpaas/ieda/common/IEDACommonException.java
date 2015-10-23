package org.openpaas.ieda.common;

import org.springframework.http.HttpStatus;

import lombok.Data;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Data
public class IEDACommonException extends RuntimeException implements IEDAException{
	private String code;
	private String message;
	private HttpStatus statusCode;
	
/*	public IEDACommonException (String code, String message) {
		this.code = code;
		this.message = message;
	}*/
	
	public IEDACommonException (String code, String message, HttpStatus statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}

	
}
