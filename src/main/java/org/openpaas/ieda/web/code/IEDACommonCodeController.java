package org.openpaas.ieda.web.code;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

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
		
		List<IEDACommonCodeDto.Response> contents = page.getContent().stream()
													.map(code -> modelMapper.map(code, IEDACommonCodeDto.Response.class))
													.collect(Collectors.toList());
		log.debug("list count   : " + contents.size());
		PageImpl<IEDACommonCodeDto.Response> result = new PageImpl<>(contents, pageable, page.getTotalElements());
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/codes/{codeIdx}", method=RequestMethod.GET)
	public ResponseEntity getCode(@PathVariable int codeIdx) {
		
		IEDACommonCode content = service.getCode(codeIdx);

		return new ResponseEntity<>(content, HttpStatus.OK);
	}
	
	@RequestMapping(value="/codes/child/{parentCodeIdx}", method=RequestMethod.GET)
	public ResponseEntity getChildCodeList(@PathVariable int parentCodeIdx) {
		
		List<IEDACommonCode> contents = service.getChildCodeList(parentCodeIdx);
		
		return new ResponseEntity<>(contents, HttpStatus.OK);
	}
	 
}

