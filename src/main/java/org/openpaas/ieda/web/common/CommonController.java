package org.openpaas.ieda.web.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

	//	@RequestMapping(value = "/common/downloadDeploymentFile", method = RequestMethod.POST)
	//	public void downloadDeploymentFile(
	//			//@PathVariable("fileName") String fileName
	//			@RequestBody CommonParam.Download param
	//			, HttpServletRequest request, HttpServletResponse response
	//			){
	//
	//		String filePath = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + param.getDeployFileName();
	//		// get absolute path of the application
	//		ServletContext context = request.getServletContext();
	//
	//		try {
	//			File downloadFile = new File(filePath);
	//			FileInputStream inputStream = new FileInputStream(downloadFile);
	//
	//			String mimeType = context.getMimeType(filePath);
	//			if (mimeType == null) {
	//				mimeType = "application/octet-stream";
	//			}
	//
	//
	//			// set content attributes for the response
	//			response.setContentType(mimeType);
	//			response.setContentLength((int) downloadFile.length());
	//
	//			// set headers for the response
	////			String headerKey = "Content-Disposition";
	////			String headerValue = String.format("attachment; filename=\"%s\"", param.getDeployFileName() + "");
	////			response.setHeader(headerKey, headerValue);
	//			
	//			if(request.getHeader("User-Agent").contains("Firefox")) {
	//		        response.setHeader("Content-Disposition",
	//		                "attachment;filename=\"" + new String(param.getDeployFileName().getBytes("UTF-8"), "ISO-8859-1") + "\";");
	//		    } else {
	//		        response.setHeader("Content-Disposition",
	//		                "attachment;filename=\"" + URLEncoder.encode(param.getDeployFileName(), "utf-8") + "\";");
	//		    }
	//			
	//			// get output stream of the response
	//			OutputStream outStream;
	//			outStream = response.getOutputStream();
	//			byte[] buffer = new byte[8192];
	//			int bytesRead = -1;
	//
	//			// write bytes read from the input stream into the output stream
	//			//while ((bytesRead = inputStream.read(buffer)) != -1) {
	//			while (inputStream.read(buffer, 0, 8192) != -1) {
	//				outStream.write(buffer, 0, 8192);
	//			}
	//			outStream.flush();
	//			outStream.close();
	//			inputStream.close();
	//
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		//return new ResponseEntity<>(content,  HttpStatus.OK);
	//	}

	@RequestMapping(value = "/common/downloadDeploymentFile/{fileName}", method = RequestMethod.GET)
	public void downloadDeploymentFile(
			@PathVariable("fileName") String fileName
			//@RequestBody CommonParam.Download param
			, HttpServletRequest request, HttpServletResponse response
			){

		String filePath = LocalDirectoryConfiguration.getDeploymentDir() + System.getProperty("file.separator") + fileName+".yml";//param.getDeployFileName();
		// get absolute path of the application
		ServletContext context = request.getServletContext();

		try {
			File downloadFile = new File(filePath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			String mimeType = context.getMimeType(filePath);
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}


			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			if(request.getHeader("User-Agent").contains("Firefox")) {
		        response.setHeader("Content-Disposition",
		                "attachment;filename=\"" + new String((fileName+".yml").getBytes("UTF-8"), "ISO-8859-1") + "\";");
		    } else {
		        response.setHeader("Content-Disposition",
		                "attachment;filename=\"" + URLEncoder.encode(fileName+".yml", "utf-8") + "\";");
		    }
			
			// get output stream of the response
			OutputStream outStream;
			outStream = response.getOutputStream();
			byte[] buffer = new byte[8192];
			int bytesRead = -1;
			int bufferRead = 8192;

			while (inputStream.read(buffer, 0, bufferRead) != -1) {
				outStream.write(buffer, 0, bufferRead);
			}
			outStream.flush();
			outStream.close();
			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
