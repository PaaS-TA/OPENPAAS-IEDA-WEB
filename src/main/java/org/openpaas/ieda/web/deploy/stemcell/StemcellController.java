package org.openpaas.ieda.web.deploy.stemcell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.common.IEDAErrorResponse;
import org.openpaas.ieda.web.config.stemcell.StemcellContent;
import org.openpaas.ieda.web.config.stemcell.StemcellContentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
public class StemcellController {

	@Autowired
	private IEDAStemcellService service;

	@Autowired
	private IEDAConfiguration iedaConfiguration;

	@Autowired
	private UploadStemcellAsyncByScriptService uploadStemcellService;

	@Autowired
	private DeleteStemcellAsyncByScriptService deleteStemcellService;

	@RequestMapping(value = "/deploy/listStemcell", method = RequestMethod.GET)
	public String List() {
		return "/deploy/listStemcell";
	}

	@RequestMapping(value = "/stemcells", method = RequestMethod.GET)
	public ResponseEntity listStemcell() {
		List<Stemcell> contents = service.listStemcell();
		int recid = 0;
		if (contents.size() > 0) {
			for (Stemcell stemcell : contents) {
				stemcell.setRecid(recid++);
				log.info("### OS : " + stemcell.getOperatingSystem());
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", contents.size());
		result.put("records", contents);
		return new ResponseEntity(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/localStemcells", method = RequestMethod.GET)
	public ResponseEntity listLocalStemcells() {
		List<StemcellContent> contents = service.listLocalStemcells();

		HashMap<String, Object> d = new HashMap<String, Object>();
		d.put("total", contents.size());
		d.put("records", contents);

		return new ResponseEntity<>(d, HttpStatus.OK);
	}

	// 스템셀 업로드
	@RequestMapping(value = "/uploadStemcell", method = RequestMethod.POST)
	public ResponseEntity doUploadStemcell(@RequestBody @Valid StemcellContentDto.Upload dto, BindingResult result) {
		if (result.hasErrors()) {
			IEDAErrorResponse errorResponse = new IEDAErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		log.info("### Upload Stemcell : " + dto.getFileName());

		uploadStemcellService.uploadStemcellAsync(iedaConfiguration.getStemcellDir(), dto.getFileName());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// 스템셀 삭제
	@RequestMapping(value = "/deleteStemcell", method = RequestMethod.POST)
	public ResponseEntity doDeleteStemcell(@RequestBody @Valid StemcellContentDto.Delete dto, BindingResult result) {
		if (result.hasErrors()) {
			IEDAErrorResponse errorResponse = new IEDAErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		log.info("### Delete Stemcell : " + dto.getFileName() + ", version : " + dto.getVersion());

		deleteStemcellService.deleteStemcellAsync(iedaConfiguration.getStemcellDir(), dto.getFileName(),
				dto.getVersion());

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
