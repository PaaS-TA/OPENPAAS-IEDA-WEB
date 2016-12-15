package org.openpaas.ieda.web.config.setting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.ErrorResponse;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.dto.DirectorConfigDTO;
import org.openpaas.ieda.web.config.setting.dto.DirectorConfigDTO.Response;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class DirectorConfigurationController extends BaseController { 

	@Autowired private DirectorConfigService service;
	@Autowired private ModelMapper modelMapper;
	
	private final static Logger LOG = LoggerFactory.getLogger(DirectorConfigurationController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 설정 화면 이동
	 * @title               : goListDirector
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/config/director", method=RequestMethod.GET)
	public String goListDirector() {
		
		if(LOG.isInfoEnabled()){ LOG.info("=====================> 설치관리자 설정 화면 요청"); }
		return "/config/listDirector";
	}


	/***************************************************
	 * @project 			: Paas 플랫폼 설치 자동화
	 * @description 	: 설치관리자 설정 추가
	 * @title 				: createDirector
	 * @return 			: ResponseEntity<Object>
	***************************************************/
	@RequestMapping(value="/config/director/add", method=RequestMethod.POST)
	public ResponseEntity<Object> createDirector(@RequestBody @Valid DirectorConfigDTO.Create directorDto, BindingResult result) {
		
		if(LOG.isInfoEnabled()){ LOG.info("=====================> 설치 관리자 설정 추가 요청"); }
		if (result.hasErrors()) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		int newDirector = service.createDirector(directorDto);
		if(LOG.isInfoEnabled()){ LOG.info("=====================> 설치 관리자 설정 추가 성공!!"); }
		
		return new ResponseEntity<>(modelMapper.map(newDirector, DirectorConfigDTO.Response.class), HttpStatus.CREATED);
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 정보 목록 조회(전체)
	 * @title               : listDirector
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/config/director/list", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> listDirector(Pageable pageable) {
		
		if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자  정보 목록 조회 요청"); }
		
		//defalutYn을 기준으로 정렬하여 설치 관리자 정보를 가져옴
		List<DirectorConfigVO> contents = service.listDirector();
		
		//contents타입의 요소(DirectorConfigVO)를 DirectorConfigDTO.Response 타입으로 변환
		List<DirectorConfigDTO.Response> result = contents.stream()
											.map(code -> modelMapper.map(code, DirectorConfigDTO.Response.class))
											.collect(Collectors.toList());
		
		int recid = 0;
		for (DirectorConfigDTO.Response directionConfig : result) {
			directionConfig.setRecid(recid++);
		}
		
		HashMap<String, Object> listResult = new HashMap<String, Object>();
		listResult.put("total", result.size());
		listResult.put("records", result);
		
		if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자 정보 목록 조회 성공!"); }
		
		return new ResponseEntity<HashMap<String, Object>>(listResult, HttpStatus.OK);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 설정 수정
	 * @title               : updateDirector
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping(value="/config/director/update/{seq}", method=RequestMethod.PUT)
	public ResponseEntity<Object> updateDirector(@PathVariable int seq, @RequestBody @Valid DirectorConfigDTO.Update updateDto) {
		
		if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자 수정 요청"); }
		service.updateDirectorConfig(updateDto);
		if(LOG.isInfoEnabled()){ LOG.info("=============================> 설치 관리자 수정 성공"); }
		
		return new ResponseEntity<> (HttpStatus.NO_CONTENT, HttpStatus.OK); 
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 설정 삭제
	 * @title               : deleteDirector
	 * @return            : ResponseEntity<Object>
	***************************************************/
	@RequestMapping(value="/config/director/delete/{seq}", method=RequestMethod.DELETE)
	public ResponseEntity<Object> deleteDirector(@PathVariable int seq) {
		
		if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 삭제 요청!!"); }
		service.deleteDirectorConfig(seq);
		if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 삭제 성공!!"); }
		
		return new ResponseEntity<> (HttpStatus.NO_CONTENT); 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : setDefaultDirector
	 * @return            : ResponseEntity<Response>
	***************************************************/
	@RequestMapping(value="/config/director/setDefault/{seq}", method=RequestMethod.PUT)
	public ResponseEntity<Response> setDefaultDirector(@PathVariable int seq) {
		
		if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 설정 요청!!"); }
		DirectorConfigVO directorConfig = service.setDefaultDirector(seq);
		if(LOG.isInfoEnabled()){ LOG.info("================> 기본 설치 관리자 설정 성공!!"); }
		Response response = modelMapper.map(directorConfig, DirectorConfigDTO.Response.class);
		
		return new ResponseEntity<Response>(response, HttpStatus.OK);
	}
	
}