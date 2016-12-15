package org.openpaas.ieda.web.management.code.controller;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.web.management.code.dto.CommonCodeDTO;
import org.openpaas.ieda.web.management.code.service.CommonCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class CommonCodeController extends BaseController {
	
	@Autowired private CommonCodeService service;
	@Autowired private CommonCodeDAO dao;
	
	
	private final static String SUB_CODE_TYPE_1 = "1";
	
	private final static Logger LOGGER= LoggerFactory.getLogger(CommonCodeController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 관리 화면을 호출하여 이동
	 * @title               : goCodeManagement
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/admin/code", method=RequestMethod.GET)
	public String goCodeManagement() {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 코드 관리 화면 이동");  }
		return "/management/code/codeManagement";
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 조회
	 * @title               : getCodeGroups
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/admin/code/groupList", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getCodeGroups() {

		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 코드 그룹 조회 요청");  }
		List<CommonCodeVO> page = dao.selectParentCodeIsNull();
		HashMap<String, Object> list = new HashMap<String, Object>();
		list.put("total", page.size());
		list.put("records", page);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 코드 그룹 조회 성공");  }
		
		return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 조회
	 * @title               : getCodeList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/admin/code/codeList/{parentCode}", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getCodeList(@PathVariable String parentCode) {

		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 코드 조회 요청");  }
		List<CommonCodeVO> page = service.getSubGroupCodeList(parentCode, "", SUB_CODE_TYPE_1);
		
		HashMap<String, Object> list = new HashMap<String, Object>();
		
		int count = 0;
		if (page != null) count = page.size();
		list.put("total", count);
		list.put("records", page);
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 코드 조회 성공");  }
		
		return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드그룹 및 코드 추가
	 * @title               : createCode
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/admin/code/add", method=RequestMethod.POST)
	public ResponseEntity<?> createCode( @RequestBody @Valid CommonCodeDTO.Regist codeDto,  BindingResult result) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 코드그룹 및 코드 추가 요청");  }
		if(codeDto.getParentCode() == null){
			service.createCode(codeDto);
		} else{
			service.createSubCode(codeDto);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 코드그룹 및 코드 추가 성공");  }

		return new ResponseEntity<>(HttpStatus.NO_CONTENT, HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 코드 수정
	 * @title               : updateCode
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/admin/code/update/{codeIdx}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateCode(@PathVariable int codeIdx,
			@RequestBody @Valid CommonCodeDTO.Regist updateCodeDto) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 해당 코드 수정 요청");  }
		updateCodeDto.setCodeIdx(codeIdx);
		
		service.updateCode(updateCodeDto);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 해당 코드 수정 성공");  }
		
		return new ResponseEntity<> (HttpStatus.NO_CONTENT, HttpStatus.OK); 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 코드 삭제
	 * @title               : deleteCode
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/admin/code/delete/{codeIdx}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteCode(@PathVariable int codeIdx) {
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 해당 코드 삭제 요청");  }
		service.deleteCode(codeIdx);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================================> 해당 코드 삭제 성공");  }
		
		return new ResponseEntity<> (HttpStatus.NO_CONTENT,  HttpStatus.OK); 
	}
	 
}

