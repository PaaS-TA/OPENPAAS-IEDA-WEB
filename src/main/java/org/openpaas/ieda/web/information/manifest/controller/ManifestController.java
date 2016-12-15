package org.openpaas.ieda.web.information.manifest.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.controller.BaseController;
import org.openpaas.ieda.web.information.manifest.dao.ManifestVO;
import org.openpaas.ieda.web.information.manifest.dto.ManifestParamDTO;
import org.openpaas.ieda.web.information.manifest.service.ManifestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
public class ManifestController extends BaseController{
	
	@Autowired ManifestService manifestService;
	final private static Logger LOGGER = LoggerFactory.getLogger(ManifestController.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 화면 호출
	 * @title               : goListManifest
	 * @return            : String
	***************************************************/
	@RequestMapping(value="/info/manifest", method=RequestMethod.GET)
	public String goListManifest() {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 화면 요청"); }
		return "/information/listManifest";
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 목록 조회
	 * @title               : getManifestList
	 * @return            : ResponseEntity<HashMap<String,Object>>
	***************************************************/
	@RequestMapping(value="/info/manifest/list", method=RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> getManifestList(){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 요청"); }
		List<ManifestVO> manifestList = manifestService.getManifestList();
		
		HashMap<String, Object> list = new HashMap<String, Object>();
		list.put("total", manifestList.size());
		list.put("records", manifestList);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 성공"); }
		
		return new ResponseEntity<HashMap<String, Object>>(list, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 Manifest 파일 정보 조회
	 * @title               : getManifestInfo
	 * @return            : ResponseEntity<String>
	***************************************************/
	@RequestMapping(value="/info/manifest/update/{id}", method=RequestMethod.GET)
	public ResponseEntity<String> getManifestInfo(@PathVariable int id){
		
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 요청"); }
		String content = manifestService.getManifestInfo(id);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 조회 성공"); }
		
		return new ResponseEntity<String>(content, HttpStatus.OK);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 업로드
	 * @title               : uploadManifest
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/info/manifest/upload/{test}", method=RequestMethod.POST)
	public ResponseEntity<?> uploadManifest( @PathVariable String test, MultipartHttpServletRequest request  ){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 요청"); }
		manifestService.uploadManifestFile(request, test);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 성공"); }
	
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 파일 다운로드
	 * @title               : downloadManifestFile
	 * @return            : void
	***************************************************/
	@RequestMapping(value="/info/manifest/download/{id}", method=RequestMethod.GET)
	public void downloadManifestFile( @PathVariable int id, HttpServletRequest request, HttpServletResponse response){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 요청"); }
		manifestService.downloadManifestFile(id, request, response);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 성공"); }
	
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 내용 수정
	 * @title               : updateManifest
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/info/manifest/update", method=RequestMethod.PUT)
	public ResponseEntity<?> updateManifest( @RequestBody @Valid ManifestParamDTO dto){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 요청"); }
		manifestService.updateManifestContent(dto);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 파일 업로드 성공"); }
	
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 삭제
	 * @title               : deleteManifest
	 * @return            : ResponseEntity<?>
	***************************************************/
	@RequestMapping(value="/info/manifest/delete/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteManifest( @PathVariable int id) {
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 삭제 요청"); }
		try {
			manifestService.deleteManifest(id);
		} catch (SQLException e) {
			throw new CommonException("sqlException.manifest.exception",
					"해당 Manifest를 삭제할 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Manifest 삭제 성공"); }
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
