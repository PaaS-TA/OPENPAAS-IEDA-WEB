package org.openpaas.ieda.web.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
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
		return new ResponseEntity<>(HttpStatus.OK);
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
		return new ResponseEntity<>(deployLogMsg, HttpStatus.OK);
	}

	@RequestMapping(value = "/common/downloadDeploymentFile/{fileName}", method = RequestMethod.GET)
	public void downloadDeploymentFile(
			@PathVariable("fileName") String fileName,
			HttpServletRequest request, HttpServletResponse response
			){
		try {
	        File file = new File(LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") +fileName +".yml");
	        
	        if( file.exists() ){
		        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
		        if( StringUtils.isEmpty(mimeType) ){
		        	mimeType = "application/octet-stream";
		        }
	
		        response.setContentType(mimeType);
		        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".yml"); 
	
		        response.setContentLength((int)file.length());
		        InputStream is = new BufferedInputStream(new FileInputStream(file));
		        FileCopyUtils.copy(is, response.getOutputStream());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
	}
}
