package org.openpaas.ieda.web.information.property.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.information.property.dto.PropertyDTO;
import org.openpaas.ieda.web.information.property.service.PropertyService;
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
public class PropertyController extends BaseController{
	
	@Autowired PropertyService propertyService;
	final private static Logger LOGGER = LoggerFactory.getLogger(PropertyController.class);
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 화면 이동
	 * @title               : goProperty
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/info/property", method=RequestMethod.GET)
	public String goProperty(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 화면 요청"); }
		return "/information/listProperty";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 목록 정보 조회
	 * @title               : getPropertyList
	 * @return            : ResponseEntity<Map<String,Object>>
	***************************************************/
	@RequestMapping(value="/info/property/list/{deployment}", method=RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> getPropertyList(@PathVariable String deployment){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 조회 요청"); }
		List<PropertyDTO> propertyList = propertyService.getPropertyList(deployment);
		HashMap<String, Object> result = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.OK;
		if( propertyList != null ){
			result.put("records", propertyList);
			result.put("total", propertyList.size());
		}else{
			status = HttpStatus.NO_CONTENT;
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 조회 성공"); }
		return new ResponseEntity<Map<String, Object>>(result, status);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 상세 정보 확인
	 * @title               : getPropertyDetailInfo
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/info/property/list/detailInfo", method=RequestMethod.POST)
	public ResponseEntity<HashMap<String, Object>>  getPropertyDetailInfo(@RequestBody @Valid PropertyDTO dto){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 상세 조회 요청"); }
		PropertyDTO propertyList = propertyService.getPropertyDetailInfo(dto);
		HashMap<String, Object> result = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.OK;
		if( propertyList != null ){
			result.put("records", propertyList);
		}else{
			status = HttpStatus.NO_CONTENT;
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 상세 조회 성공"); }
		return new ResponseEntity<HashMap<String, Object>>(result, status);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 정보 생성
	 * @title               : createPropertyInfo
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/info/property/modify/createProperty", method=RequestMethod.POST)
	public ResponseEntity<?> createPropertyInfo(@RequestBody @Valid PropertyDTO dto, Principal principal){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 생성 요청"); }
		propertyService.createProperyInfo(dto,principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 생성 성공"); }
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 정보 수정
	 * @title               : updatePropertyInfo
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/info/property/modify/updateProperty", method=RequestMethod.PUT)
	public ResponseEntity<?> updatePropertyInfo(@RequestBody @Valid PropertyDTO dto, Principal principal){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 수정 요청"); }
		propertyService.updateProperyInfo(dto,principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 수정 성공"); }
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 삭제
	 * @title               : deletePropertyInfo
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/info/property/modify/deleteProperty", method=RequestMethod.DELETE)
	public ResponseEntity<?> deletePropertyInfo(@RequestBody @Valid PropertyDTO dto, Principal principal){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 삭제 요청"); }
		propertyService.deleteProperyInfo(dto,principal);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 삭제 성공"); }
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
