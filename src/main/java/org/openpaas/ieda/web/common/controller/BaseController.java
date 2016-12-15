package org.openpaas.ieda.web.common.controller;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class BaseController{

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 예외가 발생했을 때 예외처리 관리
	 * @title               : handleCommonException
	 * @return            : ResponseEntity<?>
	***************************************************/
	@ExceptionHandler(CommonException.class)
	public ResponseEntity<?> handleCommonException(CommonException commonE) {
		
		ErrorResponse errorResponse = new ErrorResponse();
		
		errorResponse.setCode(commonE.getCode());
		errorResponse.setMessage(commonE.getMessage());
		
		return new ResponseEntity<>(errorResponse, commonE.getStatusCode());
	}
	
}
