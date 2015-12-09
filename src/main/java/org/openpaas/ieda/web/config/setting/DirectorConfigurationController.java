/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.setting;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class DirectorConfigurationController {
	
	private static final int Object = 0;

	@Autowired
	private IEDADirectorConfigRepository repository;
	
	@Autowired
	private IEDADirectorConfigService service;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * 설치관리자 설정 화면
	 * @return
	 */
	@RequestMapping(value="/config/listDirector", method=RequestMethod.GET)
	public String List() {
		return "/config/listDirector";
	}
	
	@RequestMapping(value="/directors/default", method=RequestMethod.GET)
	public ResponseEntity getDefaultDirector() {
		IEDADirectorConfig content = service.getDefaultDirector();
		return new ResponseEntity(content, HttpStatus.OK);
	}

	/**
	 * 관리자 등록
	 * @param directorDto
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/directors", method=RequestMethod.POST)
	public ResponseEntity createDirector(@RequestBody @Valid IEDADirectorConfigDto.Create directorDto, BindingResult result) {
		log.info("#### Director Regist###" + directorDto.toString());
		if (result.hasErrors()) {
			// 잘못된 요청입니다.
			IEDAErrorResponse errorResponse = new IEDAErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		IEDADirectorConfig newDirector = service.createDirector(directorDto);
		
		return new ResponseEntity<>(modelMapper.map(newDirector, IEDADirectorConfigDto.Response.class), HttpStatus.CREATED);
	}
	
/*	@RequestMapping(value="/directors", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public PageImpl<IEDADirectorConfigDto.Response> listDirector(Pageable pageable) {
		Page<IEDADirectorConfig> page = repository.findAll(pageable);
		
		List<IEDADirectorConfigDto.Response> content = page.getContent().stream()
											.map(code -> modelMapper.map(code, IEDADirectorConfigDto.Response.class))
											.collect(Collectors.toList());
		return new PageImpl<>(content, pageable, page.getTotalElements());
	}*/
	
	/**
	 * 관리자 리스트 조회
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value="/directors", method=RequestMethod.GET)
	public ResponseEntity listDirector(Pageable pageable) {
		List<IEDADirectorConfig> contents = service.listDirector();
		
		List<IEDADirectorConfigDto.Response> result = contents.stream()
											.map(code -> modelMapper.map(code, IEDADirectorConfigDto.Response.class))
											.collect(Collectors.toList());
		
		int recid = 0;
		for (IEDADirectorConfigDto.Response directionConfig : result) {
			directionConfig.setRecid(recid++);
		}
		
		HashMap<String, Object> d = new HashMap<String, Object>();
		
		d.put("total", result.size());
		d.put("records", result);
		
		return new ResponseEntity<>(d, HttpStatus.OK);
	}
	
/*	@RequestMapping(value="/directors", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public PageImpl<IEDADirectorConfigDto.Response> listDirector(Pageable pageable) {
		Page<IEDADirectorConfig> page = repository.findAll(pageable);
		
		List<IEDADirectorConfigDto.Response> content = page.getContent().stream()
											.map(code -> modelMapper.map(code, IEDADirectorConfigDto.Response.class))
											.collect(Collectors.toList());
		return new PageImpl<>(content, pageable, page.getTotalElements());
	}*/
	

	/**
	 * 관리자 기본정보
	 * @param seq
	 * @return
	 */
	@RequestMapping(value="/directors/{seq}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public IEDADirectorConfigDto.Response getDirector(@PathVariable int seq) {
		IEDADirectorConfig directorConfig = service.getDirectorConfig(seq);
		
		return modelMapper.map(directorConfig, IEDADirectorConfigDto.Response.class);  
	}
	
	/**
	 * 관리자 정보 수정
	 * @param seq
	 * @param updateDto
	 * @return
	 */
	@RequestMapping(value="/director/{seq}", method=RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public IEDADirectorConfigDto.Response updateDirector(@PathVariable int seq,
			@RequestBody @Valid IEDADirectorConfigDto.Update updateDto) {
		IEDADirectorConfig directorConfig = service.updateDirectorConfig(seq, updateDto);
		
		return modelMapper.map(directorConfig, IEDADirectorConfigDto.Response.class); 
	}
	
	/**
	 * 관리자 정보 삭제
	 * @param seq
	 * @return
	 */
	@RequestMapping(value="/director/{seq}", method=RequestMethod.DELETE)
	public ResponseEntity deleteDirector(@PathVariable int seq) {
		service.deleteDirectorConfig(seq);
		return new ResponseEntity<> (HttpStatus.NO_CONTENT); 
	}
	
	
	@ExceptionHandler(IEDACommonException.class)
	public ResponseEntity handleIEDACommonException(IEDACommonException e) {
		IEDAErrorResponse errorResponse = new IEDAErrorResponse();
		
		errorResponse.setCode(e.getCode());
		errorResponse.setMessage(e.getMessage());
		
		return new ResponseEntity<>(errorResponse, e.getStatusCode());
	}
	
	// 기본 관리자 로 설정
	@RequestMapping(value="/director/default/{seq}", method=RequestMethod.PUT)
	public ResponseEntity setDefaultDirector(@PathVariable int seq) {
		IEDADirectorConfig directorConfig = service.setDefaultDirector(seq);
		
		IEDADirectorConfigDto.Response response = modelMapper.map(directorConfig, IEDADirectorConfigDto.Response.class);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}