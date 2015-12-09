package org.openpaas.ieda.web.deploy.stemcell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.web.config.stemcell.StemcellContent;
import org.openpaas.ieda.web.config.stemcell.StemcellContentDto;
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

@Slf4j
@Controller
public class StemcellController {

	@Autowired
	private StemcellService service;

	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private DeleteStemcellAsyncService deleteStemcellAsyncService;
	
	@Autowired
	private UploadStemcellAsyncService uploadStemcellAsyncService;

	@RequestMapping(value = "/deploy/listStemcell", method = RequestMethod.GET)
	public String List() {
		return "/deploy/listStemcell";
	}
	
	// 스템셀 목록 조회
	@RequestMapping(value = "/stemcells", method = RequestMethod.GET)
	public ResponseEntity listStemcell() {
		List<Stemcell> contents = service.listStemcell();
		int recid = 0;
		if (contents != null) {
			for (Stemcell stemcell : contents) {
				stemcell.setRecid(recid++);
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		return new ResponseEntity(result, HttpStatus.OK);
	}

	// 다운로드받은 로컬 스템셀 목록 조회
	@RequestMapping(value = "/localStemcells", method = RequestMethod.GET)
	public ResponseEntity listLocalStemcells() {
		List<StemcellContent> contents = service.listLocalStemcells();

		HashMap<String, Object> d = new HashMap<String, Object>();
		d.put("total", contents.size());
		d.put("records", contents);

		return new ResponseEntity<>(d, HttpStatus.OK);
	}

	// 스템셀 업로드
	@MessageMapping("/stemcellUploading")
    @SendTo("/socket/uploadStemcell")
	public ResponseEntity doUploadStemcell(@RequestBody @Valid StemcellContentDto.Upload dto) {
		uploadStemcellAsyncService.uploadStemcellAsync(iedaConfiguration.getStemcellDir(), dto.getFileName());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 스템셀 삭제
	 * @param StemcellContentDto.Delete
	 * @param result
	 * @return
	 */
	@MessageMapping("/stemcellDelete")
    @SendTo("/socket/deleteStemcell")
	public ResponseEntity doDeleteStemcell(@RequestBody @Valid StemcellContentDto.Delete dto) {
		deleteStemcellAsyncService.deleteStemcellAsync(dto.getStemcellName(), dto.getVersion());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
