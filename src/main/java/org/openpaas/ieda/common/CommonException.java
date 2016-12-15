package org.openpaas.ieda.common;

import org.springframework.http.HttpStatus;


public class CommonException extends RuntimeException implements IEDAException{

	
	private static final long serialVersionUID = -1737917585846809753L;
	private String code; //예외 code
	private String message; //예외 message
	private HttpStatus statusCode; //상태코드
	
	public CommonException (String code, String message, HttpStatus statusCode) {
		super();
		this.code = code;
		this.message = message;
		this.statusCode = statusCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}
	
}
