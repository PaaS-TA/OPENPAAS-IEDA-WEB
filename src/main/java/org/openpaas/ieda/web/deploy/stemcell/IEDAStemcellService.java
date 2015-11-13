package org.openpaas.ieda.web.deploy.stemcell;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.openpaas.ieda.api.DirectorClient;
import org.openpaas.ieda.api.DirectorClientBuilder;
import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigRepository;
import org.openpaas.ieda.web.config.stemcell.IEDAStemcellContentRepository;
import org.openpaas.ieda.web.config.stemcell.StemcellContent;
import org.openpaas.ieda.web.config.stemcell.StemcellContentDto;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDAStemcellService {

	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;

	@Autowired
	private IEDAStemcellContentRepository stemcellContentRepository;

	@Autowired
	private StemcellManagementService stemcellManagementService;

	public List<Stemcell> listStemcell() {

		Stemcell[] stemcells = null;
		IEDADirectorConfig defaultDirector = directorConfigRepository.findOneByDefaultYn("Y");

		try {

			DirectorClient client = new DirectorClientBuilder()
					.withHost(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort())
					.withCredentials(defaultDirector.getUserId(), defaultDirector.getUserPassword()).build();

			URI stemcellsUri = UriComponentsBuilder.fromUri(client.getRoot()).pathSegment("stemcells").build().toUri();

			stemcells = client.getRestTemplate().getForObject(stemcellsUri, Stemcell[].class);
			log.info("Lenth : " + stemcells.length);
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			log.info("getMessage : " + e.getMessage());
			log.info("getLocalizedMessage : " + e.getLocalizedMessage());
			throw new IEDACommonException("notfound.stemcells.exception", " Stemcell정보 조회중 오류가 발생하였습니다.",
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.info("getMessage : " + e.getMessage());
			log.info("getLocalizedMessage : " + e.getLocalizedMessage());
			throw new IEDACommonException("notfound.stemcells.exception", "요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		return Arrays.asList(stemcells);
	}

	public List<StemcellContent> listLocalStemcells() {

		List<String> localStemcellList = stemcellManagementService.getLocalStemcellList();
		List<StemcellContent> stemcellList = stemcellContentRepository
				.findByStemcellFileNameInOrderByOsVersionDesc(localStemcellList);
		return stemcellList;
	}

}
