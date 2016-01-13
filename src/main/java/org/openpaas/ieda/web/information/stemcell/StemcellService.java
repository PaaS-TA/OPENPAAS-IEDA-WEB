package org.openpaas.ieda.web.information.stemcell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.Stemcell;
import org.openpaas.ieda.api.StemcellInfo;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.openpaas.ieda.web.config.stemcell.IEDAStemcellContentRepository;
import org.openpaas.ieda.web.config.stemcell.StemcellContent;
import org.openpaas.ieda.web.config.stemcell.StemcellManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

	public List<StemcellInfo> listStemcell() {

		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

		List<StemcellInfo> stemcellInfoList = null;
		try {
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());

			GetMethod get = new GetMethod(DirectorRestHelper.getStemcellsURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);

			client.executeMethod(get);
		
			if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
			
				ObjectMapper mapper = new ObjectMapper();
				Stemcell[] stemcells = mapper.readValue(get.getResponseBodyAsString(), Stemcell[].class);
				
				int idx = 0;
				for ( Stemcell stemcell : stemcells ) {
					if ( stemcellInfoList == null ) 
						stemcellInfoList = new ArrayList<StemcellInfo>();
					
					StemcellInfo stemcellInfo = new StemcellInfo();
					
					stemcellInfo.setRecid(idx++);
					stemcellInfo.setName(stemcell.getName());
					stemcellInfo.setOperatingSystem(stemcell.getOperatingSystem());
					stemcellInfo.setVersion(stemcell.getVersion());
					stemcellInfo.setCid(stemcell.getCid());
					
					String deploymentInfo = "";
					for ( HashMap<String, String> deployment : stemcell.getDeployments() ) {
						deploymentInfo = deploymentInfo + deployment.get("name") + "<br>";
					}
					stemcellInfo.setDeploymentInfo(deploymentInfo);
					
					stemcellInfoList.add(stemcellInfo);
				}
				
				// 스템셀 버전 역순으로 정렬
				if ( stemcellInfoList != null && stemcellInfoList.size() > 0 ) {
					Comparator<StemcellInfo> byStemcellVersion = Collections.reverseOrder(Comparator.comparing(StemcellInfo::getVersion));
					stemcellInfoList = stemcellInfoList.stream()
							.sorted(byStemcellVersion)
							.collect(Collectors.toList());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new IEDACommonException("notfound.stemcell.exception", " 스템셀 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		
		return stemcellInfoList;
	}

	public List<StemcellContent> listLocalStemcells() {
		
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();
		
		if ( defaultDirector != null ) {
			// 디럭터의 CPI에 맞는 로컬 스템셀 목록만 출력
			if ( defaultDirector.getDirectorCpi().toUpperCase().contains("AWS") ) filterString = "AWS";
			if ( defaultDirector.getDirectorCpi().toUpperCase().contains("OPENSTACK") ) filterString = "OPENSTACK";
		}
		
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
	
	public List<String> localAwsStemcells(){
		File file = new File(LocalDirectoryConfiguration.getStemcellDir());
		File[] localFiles = file.listFiles();
		String type= "lightAws";
		List<String> localStemcells = null; 
		String regx = "";
		if( type.equals("lightAwsHvm") ){
			regx = "\\A(light-bosh-stemcell-)\\d{4}(-aws-xen-hvm)";
		}
		else if( type.equals("lightAws") ){
			regx = "\\A(light-bosh-stemcell-)\\d{4}(-aws)";
		}
		else if( type.equals("boshOpenstackUbuntu") ){
			regx = "\\A(bosh-stemcell-)\\d{4}(-openstack-kvm-ubuntu)";
		}
		
		for (File fileInfo : localFiles) {
			if ( localStemcells == null )
				localStemcells = new ArrayList<String>();
			
			Pattern pattern = Pattern.compile(regx);
	        Matcher matcher = pattern.matcher(fileInfo.getName().toLowerCase());
	        if(matcher.find()){
	        	localStemcells.add(fileInfo.getName());
	        }
		}		
		
		return localStemcells;
	}

	public java.util.List<String> localOpenstackStemcells() {
		File file = new File(LocalDirectoryConfiguration.getStemcellDir());
		File[] localFiles = file.listFiles();
		//String type= "boshOpenstackUbuntu";
		List<String> localStemcells = null; 
		String regx = "\\A(bosh-stemcell-)\\d{4}(-openstack-kvm-ubuntu)";
//		if( type.equals("lightAwsHvm") ){
//			regx = "\\A(light-bosh-stemcell-)\\d{4}(-aws-xen-hvm)";
//		}
//		else if( type.equals("lightAws") ){
//			regx = "\\A(light-bosh-stemcell-)\\d{4}(-aws)";
//		}
//		else if( type.equals("boshOpenstackUbuntu") ){
//			regx = "\\A(bosh-stemcell-)\\d{4}(-openstack-kvm-ubuntu)";
//		}
		
		for (File fileInfo : localFiles) {
			if ( localStemcells == null )
				localStemcells = new ArrayList<String>();
			
			Pattern pattern = Pattern.compile(regx);
	        Matcher matcher = pattern.matcher(fileInfo.getName().toLowerCase());
	        if(matcher.find()){
	        	localStemcells.add(fileInfo.getName());
	        }
		}		
		
		return localStemcells;
	}
	
}
