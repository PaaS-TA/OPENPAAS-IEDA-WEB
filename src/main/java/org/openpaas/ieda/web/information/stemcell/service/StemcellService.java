package org.openpaas.ieda.web.information.stemcell.service;

import java.net.NoRouteToHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.stemcell.StemcellListDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StemcellService {

	@Autowired private StemcellManagementService stemcellManagementService;
	@Autowired private DirectorConfigService directorConfigService;
	@Autowired private StemcellManagementDAO stemcellDao;
	@Autowired private DirectorConfigDAO dao;

	private String filterString;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드된 스템셀 조회 요청
	 * @title               : listStemcell
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	public List<StemcellManagementVO> listStemcell(String iaas) {

		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();

		List<StemcellManagementVO> stemcellInfoList = null;
		try {
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getStemcellsURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			client.executeMethod(get);

			if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {

				ObjectMapper mapper = new ObjectMapper();
				StemcellListDTO[] stemcells = mapper.readValue(get.getResponseBodyAsString(), StemcellListDTO[].class);
				int idx = 0;
				for ( StemcellListDTO stemcell : stemcells ) {
					if ( stemcellInfoList == null ) 
						stemcellInfoList = new ArrayList<StemcellManagementVO>();

					StemcellManagementVO stemcellInfo = new StemcellManagementVO();

					stemcellInfo.setRecid(idx++);
					stemcellInfo.setStemcellFileName(stemcell.getName());
					stemcellInfo.setOs(stemcell.getOperatingSystem());
					stemcellInfo.setStemcellVersion(stemcell.getVersion());
					stemcellInfoList.add(stemcellInfo);
				}

				// 스템셀 버전 역순으로 정렬
				if ( stemcellInfoList != null && !stemcellInfoList.isEmpty() ) {
					Comparator<StemcellManagementVO> byStemcellVersion = Collections.reverseOrder(Comparator.comparing(StemcellManagementVO::getStemcellVersion));
					stemcellInfoList = stemcellInfoList.stream()
							.sorted(byStemcellVersion)
							.collect(Collectors.toList());
				}
			}
		}catch(RuntimeException e){
			throw new CommonException("server.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (NoRouteToHostException e){
			throw new CommonException("server.stemcell.exception", "네트워크 연결에 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (Exception e) {
			throw new CommonException("notfound.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}

		return stemcellInfoList;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬에 다운로드된 스템셀 조회
	 * @title               : listLocalStemcells
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	public List<StemcellManagementVO> listLocalStemcells() {

		DirectorConfigVO directorConfig = dao.selectDirectorConfigByDefaultYn("Y");
		
		if ( directorConfig != null ) {
			// 디럭터의 CPI에 맞는 로컬 스템셀 목록만 출력
			if ( directorConfig.getDirectorCpi().toUpperCase().contains("AWS") ) filterString = "AWS";
			if ( directorConfig.getDirectorCpi().toUpperCase().contains("OPENSTACK") ) filterString = "OPENSTACK";
		}

		List<StemcellManagementVO> returnList = null;
		List<String> localStemcellList = stemcellManagementService.getLocalStemcellList();

		if(!localStemcellList.isEmpty()){
			if ( filterString != null && filterString.length() > 0 ){
				returnList = stemcellDao.selectStemcellFileNameOrderByStemcellVersion(localStemcellList).stream()
						.filter(t -> t.getIaas().equalsIgnoreCase(filterString))
						.collect(Collectors.toList());
			}
			else{
				returnList = stemcellDao.selectStemcellFileNameOrderByStemcellVersion(localStemcellList);
			}
		}
		
		return returnList;
	}

}
