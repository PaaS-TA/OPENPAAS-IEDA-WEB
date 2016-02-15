package org.openpaas.ieda.common;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class IEDACommonException extends RuntimeException implements IEDAException{
	private String code;
	private String message;
	private HttpStatus statusCode;
	
	public IEDACommonException (String code, String message, HttpStatus statusCode) {
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}

	
}
