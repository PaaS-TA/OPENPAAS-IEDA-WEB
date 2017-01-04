package org.openpaas.ieda.web.management.user.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.management.auth.controller.AuthManagementController;
import org.openpaas.ieda.web.management.user.dao.UserManagementVO;
import org.openpaas.ieda.web.management.user.dto.UserManagementDTO;
import org.openpaas.ieda.web.management.user.service.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserManagementController extends BaseController{
	
	@Autowired UserManagementService service;
	private final static Logger LOG = LoggerFactory.getLogger(AuthManagementController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 관리 화면 이동
	 * @title               : goUserManagement
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/admin/user", method=RequestMethod.GET)
	public String goUserManagement(){
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 관리 화면 이동 요청");
		}
		return "/management/user/userManagement";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 목록 정보 요청
	 * @title               : getUserInfoList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/admin/user/list", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getUserInfoList(){
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 목록 조회 요청");
		}
		List<UserManagementVO> userList = service.getUserInfoList();		
		HashMap<String, Object> list = new HashMap<String, Object>();
		list.put("total", userList.size());
		list.put("records", userList);
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 목록 조회 성공");
		}
		return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
	};
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 등록
	 * @title               : saveUserInfo
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/admin/user/add", method=RequestMethod.POST)
	public  ResponseEntity<?> saveUserInfo(@RequestBody @Valid UserManagementDTO.Regist dto){
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 등록 요청");
		}
		service.savaUserInfo(dto);
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 등록 요청 성공");
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 수정
	 * @title               : updateUserInfo
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/admin/user/update/{userId}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateUserInfo(@RequestBody @Valid UserManagementDTO.Regist dto, @PathVariable String userId){
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 수정 요청");
		}
		try {
			service.updateUserInfo(dto,userId);
		} catch (SQLException e) {
			throw new CommonException("sql.user.exception",
					"사용자 정보 수정 중 에러가 발생 했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 수정 요청 성공");
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 삭제
	 * @title               : deleteUserInfo
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/admin/user/delete/{userId}", method = RequestMethod.DELETE)
	public  ResponseEntity<?> deleteUserInfo(@PathVariable String userId){
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 삭제 요청");
		}
		service.deleteUserInfo(userId);
		if (LOG.isInfoEnabled()) {
			LOG.info("================================================> 사용자 삭제 요청 성공");
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
