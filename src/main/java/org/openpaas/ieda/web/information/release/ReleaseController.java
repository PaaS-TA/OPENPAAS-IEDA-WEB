/**
 * @Author Cheolho Moon
 */
package org.openpaas.ieda.web.information.release;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.api.ReleaseFile;
import org.openpaas.ieda.api.ReleaseInfo;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.BaseController;
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

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class ReleaseController extends BaseController {
	
	@Autowired
	private ReleaseService releaseService;
	
	@Autowired
	private ReleaseUploadAsyncService releaseUploadService;
	
	@Autowired
	private ReleaseDeleteAsyncService releaseDeleteService;
	
	@RequestMapping(value="/information/listRelease", method=RequestMethod.GET)
	public String List() {
		return "/information/listRelease";
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
	
	@RequestMapping( value="/release/localBoshList", method =RequestMethod.GET)
	public ResponseEntity listLocalBoshRelease(){
		List<String> contents = releaseService.listLocalBoshRelease();
		return new ResponseEntity( contents, HttpStatus.OK);
	}
	
	@RequestMapping( value="/release/localBoshAwsCpiList", method =RequestMethod.GET)
	public ResponseEntity listLocalBoshAwsCpiRelease(){
		List<String> contents = releaseService.listLocalBoshAwsCpiRelease();
		return new ResponseEntity( contents, HttpStatus.OK);
	}
	
	@RequestMapping( value="/release/localBoshOpenstackCpiList", method =RequestMethod.GET)
	public ResponseEntity listLocalBoshOpenstackCpiRelease(){
		List<String> contents = releaseService.listLocalBoshOpenstackCpiRelease();
		return new ResponseEntity( contents, HttpStatus.OK);
	}
	
	@RequestMapping( value="/release/getReleaseList/{filterName}", method =RequestMethod.GET)
	public ResponseEntity listLocalFilterReleaseList(@PathVariable  String filterName){
		List<ReleaseInfo> contents = releaseService.getReleasesFilter(filterName);
		Map<String, Object> result = new HashMap<>();
		result.put("records", contents);
		result.put("total", (contents == null) ? 0:contents.size());
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
		releaseUploadService.uploadReleaseAsync(LocalDirectoryConfiguration.getReleaseDir(), dto.getFileName());
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
		
		releaseDeleteService.deleteReleaseAsync(dto.getFileName(), dto.getVersion());
		return new ResponseEntity(HttpStatus.OK);
	}
	
	/**
	 * 릴리즈 삭제
	 * @param dto
	 * @return
	 */
	@RequestMapping( value="/deleteLocalRelease", method=RequestMethod.DELETE)
	public ResponseEntity doDeleteLocalRelease(@RequestBody @Valid ReleaseContentDto.DeleteLocal dto) {
		
		releaseService.deleteLocalRelease(dto.getFileName());
		return new ResponseEntity(HttpStatus.OK);
	}
	
}
