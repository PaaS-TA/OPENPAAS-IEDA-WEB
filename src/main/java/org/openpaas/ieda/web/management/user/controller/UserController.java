package org.openpaas.ieda.web.management.user.controller;

import javax.validation.Valid;

import org.openpaas.ieda.common.ErrorResponse;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.management.user.dto.UserDTO;
import org.openpaas.ieda.web.management.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
public class UserController extends BaseController {

	@Autowired private UserService service;

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 패스워드 재설정 화면으로 이동한다.
	 * @title               : goResetPassword
	 * @return            : String
	***************************************************/
	@RequestMapping(value = "/common/user/resetPassword", method = RequestMethod.GET)
	public String goResetPassword() {
		return "/login/resetPassword";
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 패스워드를 저장한다.
	 * @title               : savePassword
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping(value = "/common/user/savePassword", method = RequestMethod.POST)
	public ResponseEntity<Object> savePassword(@RequestBody @Valid UserDTO.SavePassword savePasswordDto,
			BindingResult result) {

		if (result.hasErrors()) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		service.savePassword(savePasswordDto);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
