package org.openpaas.ieda.web.common;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	@RequestMapping(value="/common/getDeployInfo", method=RequestMethod.POST)
	public ResponseEntity getBoshAwsDeployInfo(@RequestBody @Valid String  deploymentFile){
		HttpStatus status = HttpStatus.OK;
		String content = commonService.getDeploymentInfo(deploymentFile);
		if(StringUtils.isEmpty(content) ) {
			status = HttpStatus.NO_CONTENT;
		}		
		return new ResponseEntity<>(content, status);
	}
	
	@RequestMapping(value="/common/getDeployLogMsg", method=RequestMethod.POST)
	public ResponseEntity getDeployLogMsg(@RequestBody @Valid CommonParam.DeployLog param){
		String deployLogMsg = commonService.getDeployMsg(param);
		return new ResponseEntity(deployLogMsg, HttpStatus.OK);
	}
}
