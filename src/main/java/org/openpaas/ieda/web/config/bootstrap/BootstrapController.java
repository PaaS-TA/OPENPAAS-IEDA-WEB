/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.config.bootstrap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class BootstrapController {
	@Autowired
	private IEDABootstrapAwsService awsService;
	
	@Autowired
	private IEDABootstrapOpenstackService openstackService;
	
	@Autowired
	private IEDABootstrapService bootstrapService;
	
	@Autowired
	private StemcellManagementService stemcellService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@RequestMapping(value = "/config/bootstrap", method = RequestMethod.GET)
	public String main() {
		return "/config/bootstrap";
	}
	
	/**
	 * Bootstrap 리스트 조회
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value="/bootstraps", method=RequestMethod.GET)
	public ResponseEntity listBootstrap() {
		List<BootstrapListDto> content = bootstrapService.listBootstrap();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		result.put("total", (content == null) ? 0:content.size());
		result.put("records", content);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/aws/{id}", method=RequestMethod.GET)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	public ResponseEntity getAwsInfo(@PathVariable int id){
		IEDABootstrapAwsConfig config = awsService.getAwsInfo(id);
		
		Map<String, Object> result =  new HashMap<>();
		result.put("contents", config);
		return new ResponseEntity<>(result, HttpStatus.OK);
		//return new ResponseEntity<>(config, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootstrapSetAws", method = RequestMethod.PUT)
	public ResponseEntity doBootSetAwsSave(
			/*@RequestParam("id") String id,
			@RequestParam("iaas") String iaas,
			@RequestParam("awsKey") String awsKey,
			@RequestParam("awsPw") String awsPw,
			@RequestParam("secretGroupName") String secretGroupName,
			@RequestParam("privateKeyName") String privateKeyName,
			@RequestParam("privateKeyPath") String privateKeyPath, 
			@RequestParam("keyPathFile") MultipartFile keyPathFile*/
			@RequestBody  IDEABootStrapInfoDto.Aws data
			){
		 
		log.info("### AwsData : " + data.toString());
		int seq = awsService.saveAwsInfo(data);
		
		return new ResponseEntity(seq, HttpStatus.OK);
	}	
	
	@RequestMapping(value="/bootstrap/keyPathFileUpload", method = RequestMethod.POST)
	public ResponseEntity doBootstrapKeyPathFileUpload(
			MultipartHttpServletRequest request
			){
		log.info("##################### keyPathFileUpload\n"+request.getFileMap().toString()+"\n#####");
		Iterator<String> itr =  request.getFileNames();
		String keyPath = System.getProperty("user.home") + System.getProperty("file.separator") + ".ssh" + System.getProperty("file.separator");
		log.info("# : "+ keyPath);
        if(itr.hasNext()) {
            MultipartFile mpf = request.getFile(itr.next());
            log.info(mpf.getOriginalFilename() +" uploaded!");
            try {
                //just temporary save file info into ufile
                log.info("file length : " + mpf.getBytes().length);
                log.info("file name : " + mpf.getOriginalFilename());
                byte[] bytes = mpf.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(keyPath + mpf.getName())));
                stream.write(bytes);
                stream.close();
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();
            }
        } 
		
		//return new ResponseEntity(seq, HttpStatus.OK);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/bootstrapSetAwsNetwork", method = RequestMethod.PUT)
	public ResponseEntity doBootSetNetworkSave(@RequestBody  IDEABootStrapInfoDto.Network data){
		log.info("### NetworkData : " + data.toString());
		awsService.saveAwsNetworkInfos(data);
		return new ResponseEntity(HttpStatus.OK);
	}	
	
	@RequestMapping(value="/bootstrap/bootSetAwsResources", method = RequestMethod.PUT)
	public ResponseEntity doBootSetResourcesSave(@RequestBody IDEABootStrapInfoDto.Resources data){
		log.info("### ResourcesSave : " + data.toString());
		String deployFileName = awsService.saveAwsResourcesInfos(data);
		return new ResponseEntity(deployFileName, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getLocalStemcellList", method = RequestMethod.GET)
	public ResponseEntity doBootSetStemcellList(){
		log.info("### doBootSetStemcellList : ");
		List<String> contents = stemcellService.getLocalStemcellList();
		
		return new ResponseEntity(contents, HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getBootstrapDeployInfo", method = RequestMethod.POST)
	public ResponseEntity getBootStrapSettingInfo(){
		String content = bootstrapService.getBootStrapSettingInfo();
		HttpStatus status = (content != null) ? HttpStatus.OK: HttpStatus.NO_CONTENT;
		log.info("\n" +status + "\n");
		return new ResponseEntity(content, status);
	}

	@MessageMapping("/bootstrapInstall")
	@SendTo("/bootstrap/bootstrapInstall")
	public ResponseEntity doInstallBootstrap(@RequestBody @Valid IDEABootStrapInfoDto.Install dto){
		log.info("$$$$ SOCKET :  "+ dto.getDeployFileName());
		bootstrapService.installBootstrap(dto.getDeployFileName());
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@MessageMapping("/bootstrapDelete")
	@SendTo("/bootstrap/bootstrapDelete")
	public ResponseEntity deleteBootstrap(@RequestBody @Valid IDEABootStrapInfoDto.Delete dto){
		log.info("$$$$ DELETE Connection :: " + dto.toString());
		if("AWS".equals(dto.getIaas())) awsService.deleteAwsInfo(dto.getId());
		//else openstackService.deleteOpenstackInfo(dto.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/bootstrap/getKeyPathFileList" , method=RequestMethod.GET)
	public ResponseEntity getKeyPathFileList (){
		List<String> keyPathFileList = bootstrapService.getKeyPathFileList();
		
		return new ResponseEntity<>(keyPathFileList, HttpStatus.OK);
	}
	
}