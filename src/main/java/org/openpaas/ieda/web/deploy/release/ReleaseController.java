/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.deploy.release;

import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.openpaas.ieda.api.ReleaseFile;
import org.openpaas.ieda.api.ReleaseInfo;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Slf4j
@Controller
public class ReleaseController {
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private ReleaseService releaseService;
	
	@Autowired
	private UploadReleaseAsyncService uploadReleaseService;
	
	@Autowired
	private DeleteReleaseAsyncService deleteReleaseService;
	
	@RequestMapping(value="/deploy/listRelease", method=RequestMethod.GET)
	public String List() {
		return "/deploy/listRelease";
	}
	
	@RequestMapping( value="/releases", method =RequestMethod.GET)
	public ResponseEntity listRelease(){
		List<ReleaseInfo> contents = releaseService.listRelease();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		return new ResponseEntity( result, HttpStatus.OK);
	}
	
	@RequestMapping( value="/releases/versions", method =RequestMethod.GET)
	public ResponseEntity listReleaseVersion(){
		List<ReleaseInfo> contents = releaseService.listRelease();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		return new ResponseEntity( result, HttpStatus.OK);
	}
	
	@RequestMapping( value="/localReleases", method =RequestMethod.GET)
	public ResponseEntity listLocalRelease(){
		List<ReleaseFile> contents = releaseService.listLocalRelease();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		if ( contents != null ) {
			result.put("total", contents.size());
			result.put("records", contents);
		} else
			result.put("total", 0);
		
		return new ResponseEntity( result, HttpStatus.OK);
	}
	
	/**
	 * 릴리즈 업로드
	 * @param dto
	 * @return
	 */
	@MessageMapping("/releaseUploading")
	@SendTo("/socket/uploadRelease")
	public ResponseEntity doUploadRelease(@RequestBody @Valid ReleaseContentDto.Upload dto) {
		uploadReleaseService.uploadReleaseAsync(iedaConfiguration.getReleaseDir(), dto.getFileName());
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/**
	 * 릴리즈 삭제
	 * @param dto
	 * @return
	 */
	@MessageMapping("/releaseDelete")
	@SendTo("/socket/deleteRelease")
	public ResponseEntity doDeleteRelease(@RequestBody @Valid ReleaseContentDto.Delete dto) {
		
		deleteReleaseService.deleteReleaseAsync(dto.getFileName(), dto.getVersion());
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/**
	 * 릴리즈 삭제
	 * @param dto
	 * @return
	 */
	@RequestMapping( value="/deleteLocalRelease", method=RequestMethod.PUT)
	public ResponseEntity doDeleteLocalRelease(@RequestBody @Valid ReleaseContentDto.DeleteLocal dto) {
		
		releaseService.deleteLocalRelease(dto.getFileName());
		return new ResponseEntity(HttpStatus.OK);
	}
	
}

