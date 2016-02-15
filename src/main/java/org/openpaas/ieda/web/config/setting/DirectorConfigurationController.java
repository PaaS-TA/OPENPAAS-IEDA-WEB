package org.openpaas.ieda.web.config.setting;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.IEDAErrorResponse;
import org.openpaas.ieda.web.common.BaseController;
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
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class DirectorConfigurationController extends BaseController { 
	
	@Autowired
	private IEDADirectorConfigService service;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@RequestMapping(value="/config/listDirector", method=RequestMethod.GET)
	public String List() {
		return "/config/listDirector";
	}
	
	@RequestMapping(value="/directors/default", method=RequestMethod.GET)
	public ResponseEntity getDefaultDirector() {
		IEDADirectorConfig content = service.getDefaultDirector();
		return new ResponseEntity<>(content, HttpStatus.OK);
	}

	@RequestMapping(value="/directors", method=RequestMethod.POST)
	public ResponseEntity createDirector(@RequestBody @Valid IEDADirectorConfigDto.Create directorDto, BindingResult result) {
		if (result.hasErrors()) {
			IEDAErrorResponse errorResponse = new IEDAErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		IEDADirectorConfig newDirector = service.createDirector(directorDto);
		
		return new ResponseEntity<>(modelMapper.map(newDirector, IEDADirectorConfigDto.Response.class), HttpStatus.CREATED);
	}
	
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

	@RequestMapping(value="/directors/{seq}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public IEDADirectorConfigDto.Response getDirector(@PathVariable int seq) {
		IEDADirectorConfig directorConfig = service.getDirectorConfig(seq);
		
		return modelMapper.map(directorConfig, IEDADirectorConfigDto.Response.class);  
	}
	
	@RequestMapping(value="/directors/{seq}", method=RequestMethod.PUT)
	public ResponseEntity updateDirector(@PathVariable int seq,
			@RequestBody @Valid IEDADirectorConfigDto.Update updateDto) {
		
		IEDADirectorConfig directorConfig = service.updateDirectorConfig(updateDto);
		
		return new ResponseEntity<> (directorConfig, HttpStatus.OK); 
	}
	
	@RequestMapping(value="/director/{seq}", method=RequestMethod.DELETE)
	public ResponseEntity deleteDirector(@PathVariable int seq) {
		service.deleteDirectorConfig(seq);
		return new ResponseEntity<> (HttpStatus.NO_CONTENT); 
	}
	
	@RequestMapping(value="/director/default/{seq}", method=RequestMethod.PUT)
	public ResponseEntity setDefaultDirector(@PathVariable int seq) {
		IEDADirectorConfig directorConfig = service.setDefaultDirector(seq);
		
		IEDADirectorConfigDto.Response response = modelMapper.map(directorConfig, IEDADirectorConfigDto.Response.class);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}