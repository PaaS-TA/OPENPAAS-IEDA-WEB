package org.openpaas.ieda.web.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class CommonController {

	@Autowired
	private CommonService commonService; 
	
	@RequestMapping(value="/common/keyPathFileUpload", method=RequestMethod.POST)
	public ResponseEntity doBootstrapKeyPathFileUpload( MultipartHttpServletRequest request){
		commonService.uploadKeyFile(request);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/common/getKeyPathFileList" , method=RequestMethod.GET)
	public ResponseEntity getKeyPathFileList (){
		List<String> keyPathFileList = commonService.getKeyFileList();
		
		return new ResponseEntity<>(keyPathFileList, HttpStatus.OK);
	}
}
