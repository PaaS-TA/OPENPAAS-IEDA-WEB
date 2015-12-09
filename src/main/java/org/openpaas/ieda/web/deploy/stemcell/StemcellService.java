package org.openpaas.ieda.web.deploy.stemcell;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.openpaas.ieda.web.config.stemcell.IEDAStemcellContentRepository;
import org.openpaas.ieda.web.config.stemcell.StemcellContent;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StemcellService {
	
	@Autowired
	private IEDAStemcellContentRepository stemcellContentRepository;

	@Autowired
	private StemcellManagementService stemcellManagementService;
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;
	
	private String filterString = null;

	public List<Stemcell> listStemcell() {

		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();
		
		HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());

		GetMethod get = new GetMethod(DirectorRestHelper.getStemcellsURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
		get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);

		Stemcell[] stemcells = null;
		try {
			int status = client.executeMethod(get);
			
			ObjectMapper mapper = new ObjectMapper();
			stemcells = mapper.readValue(get.getResponseBodyAsString(), Stemcell[].class);
			log.info("# Stemcell List : " + get.getResponseBodyAsString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IEDACommonException("notfound.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
		// 스템셀 버전 역순으로 정렬
		Comparator<Stemcell> byStemcellVersion = Collections.reverseOrder(Comparator.comparing(Stemcell::getVersion));
		return Arrays.asList(stemcells).stream().sorted(byStemcellVersion).collect(Collectors.toList());
	}

	public List<StemcellContent> listLocalStemcells() {
		
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();
		
		// 디럭터의 CPI에 맞는 로컬 스템셀 목록만 출력
		if ( defaultDirector.getDirectorCpi().toUpperCase().contains("AWS") ) filterString = "AWS";
		if ( defaultDirector.getDirectorCpi().toUpperCase().contains("OPENSTACK") ) filterString = "OPENSTACK";

		List<StemcellContent> returnList = null;
		List<String> localStemcellList = stemcellManagementService.getLocalStemcellList();

		if ( filterString != null && filterString.length() > 0 )
			returnList = stemcellContentRepository.findByStemcellFileNameInOrderByStemcellVersionDesc(localStemcellList).stream()
			.filter(t -> t.getIaas().equalsIgnoreCase(filterString))
			.collect(Collectors.toList());
		else
			returnList = stemcellContentRepository.findByStemcellFileNameInOrderByStemcellVersionDesc(localStemcellList);

		filterString = null;
					
		return returnList;
	}
}
