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
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StemcellService {
	
	@Autowired private DirectorConfigService directorConfigService;
	
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


}
