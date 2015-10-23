package org.openpaas.ieda.web.code;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.IEDAErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

@Controller
@Slf4j
public class IEDACommonCodeController {
	
	@Autowired
	private IEDACommonCodeService service;
	
	@Autowired
	private IEDACommonCodeRepository repository; 
	
	@Autowired
	private ModelMapper modelMapper;
	
	@RequestMapping(value="/codes", method=RequestMethod.GET)
	public ResponseEntity getCodes(Pageable pageable) {

		Page<IEDACommonCode> page = repository.findAll(pageable);
		
		log.info("page.getSize() : " + page.getSize());
		log.info("page.getTotalElements() : " + page.getTotalElements());
		
		List<IEDACommonCodeDto.Response> contents = page.getContent().stream()
													.map(code -> modelMapper.map(code, IEDACommonCodeDto.Response.class))
													.collect(Collectors.toList());
		log.info("list count   : " + contents.size());
		PageImpl<IEDACommonCodeDto.Response> result = new PageImpl<>(contents, pageable, page.getTotalElements());
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/codes/{codeIdx}", method=RequestMethod.GET)
	//@ResponseStatus(HttpStatus.OK)
	//public IEDACommonCodeDto.Response getCode(@PathVariable int codeIdx) {
	public ResponseEntity getCode(@PathVariable int codeIdx) {
		
		IEDACommonCode content = service.getCode(codeIdx);

		return new ResponseEntity(content, HttpStatus.OK);
		//modelMapper.map(commonCode, IEDACommonCodeDto.Response.class);
	}
	
	@RequestMapping(value="/codes/child/{parentCodeIdx}", method=RequestMethod.GET)
	public ResponseEntity getChildCodeList(@PathVariable int parentCodeIdx) {
		log.info("1111");
		List<IEDACommonCode> contents = service.getChildCodeList(parentCodeIdx);
		log.info("controller contens size : " + contents.size());
		
		return new ResponseEntity<>(contents, HttpStatus.OK);
	}
	 

	/*	@RequestMapping(value="/codes", method=RequestMethod.POST)
	public ResponseEntity createCode(@RequestBody @Valid IEDACommonCodeDto.Create commonCode,
									BindingResult result) {
		if (result.hasFieldErrors()) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setCode("bad.request");
			errorResponse.setMessage("잘못된 요청입니다.");
			
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		IEDACommonCode newCommonCode = service.createCode(commonCode);
		log.debug("==> Code Name : " + commonCode.getCodeName());
		
		return new ResponseEntity<>(modelMapper.map(newCommonCode, IEDACommonCodeDto.Response.class), HttpStatus.CREATED);
	}*/	
	
/*	// 전체 업데이트(PUT) vs 부분 업데이트(PATCH) rest api best practice
	@RequestMapping(value="/codes/{codeKey}", method=RequestMethod.PUT)
	public ResponseEntity updateCode(@PathVariable String codeKey,
										@RequestBody @Valid IEDACommonCodeDto.Update updateDto,
										BindingResult result) {
		if ( result.hasErrors() ) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		IEDACommonCode updatedCode = service.updateCode(codeKey, updateDto);
		
		return new ResponseEntity<>(modelMapper.map(updatedCode, IEDACommonCodeDto.Response.class), HttpStatus.OK);
	}
	
	@RequestMapping(value="/codes/{codeKey}", method=RequestMethod.DELETE)
	public ResponseEntity deleteCode(@PathVariable String codeKey) {
		service.deleteCode(codeKey);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}*/


}

