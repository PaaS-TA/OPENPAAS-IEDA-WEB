package org.openpaas.ieda.web.common;

import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class BaseController {

	@ExceptionHandler(IEDACommonException.class)
	public ResponseEntity handleIEDACommonException(IEDACommonException e) {
		
		IEDAErrorResponse errorResponse = new IEDAErrorResponse();
		
		errorResponse.setCode(e.getCode());
		errorResponse.setMessage(e.getMessage());
		
		return new ResponseEntity<>(errorResponse, e.getStatusCode());
	}

}
