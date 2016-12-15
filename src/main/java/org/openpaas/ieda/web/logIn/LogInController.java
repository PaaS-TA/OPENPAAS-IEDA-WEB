package org.openpaas.ieda.web.logIn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openpaas.ieda.api.config.security.SecurityUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LogInController {
	
	private static final Logger LOGGER = Logger.getLogger(LogInController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로그인 화면으로 이동
	 * @title               : goLogIn
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String goLogIn() {
		return "/login/login";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 부적절한 접근 시도시 로그아웃
	 * @title               : abuse
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/abuse", method=RequestMethod.GET)
	public String abuse(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				
		// Token에서 사용자아이디 정보를 얻는다.
		SecurityUserDetails userDetails = (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
		
		if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
		
		if(LOGGER.isInfoEnabled()){
			LOGGER.info("권한없는 경로에 대한 부적절한 접근 시도가 있었습니다. user id : " + userDetails.getUserId() + request.getRequestURL());
		}
		
	    return "redirect:/login?code=abuse";
	}
}