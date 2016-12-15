package org.openpaas.ieda.web.config.stemcell.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class StemcellManagementService {

	@Autowired private StemcellManagementDAO dao;
	
	private String key;
	final private static String PUBLIC_STEMCELLS_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";
	final private static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementService.class);
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬에 저장된 스템셀 목록 조회
	 * @title               : getLocalStemcellList
	 * @return            : List<String>
	***************************************************/
	public List<String> getLocalStemcellList() {
		
		//1.파일객체 생성
		File dir = new File(LocalDirectoryConfiguration.getStemcellDir());
		//2.폴더가 가진 파일객체를 리스트로 받는다.
		File[] localFiles = dir.listFiles();
		List<String> localStemcells = new ArrayList<>();
		if( localFiles != null ){
			for (File file : localFiles) {
				localStemcells.add(file.getName());
			}
		}
		return localStemcells;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : XML 태그에 속하는 정보를 Java에서 읽어옴
	 * @title               : getXMLNodeToString
	 * @return            : String
	***************************************************/
	private String getXMLNodeToString(Node node, String tagName) {
		NodeList nodeList = ((Element) node).getElementsByTagName(tagName);
		Element element = (Element) nodeList.item(0);
		return element.getTextContent();
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Public_Stemcells 정보 설정
	 * @title               : makePublicStemcells
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	private List<StemcellManagementVO> makePublicStemcells(List<StemcellManagementVO> publicStemcells, NodeList contents) {

		for (int i = 0; i < contents.getLength(); i++) {
			Node node = contents.item(i);

			key = getXMLNodeToString(node, "Key");

			StemcellManagementVO stemcell = new StemcellManagementVO();

			stemcell.setSublink(key);
			stemcell.setLastModified(getXMLNodeToString(node, "LastModified"));
			stemcell.setEtag(getXMLNodeToString(node, "ETag"));
			stemcell.setSize(getXMLNodeToString(node, "Size"));
			stemcell.setStorageClass(getXMLNodeToString(node, "StorageClass"));

			publicStemcells.add(stemcell);
		}

		return publicStemcells;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 외부에서 Public_Stemcells 조회
	 * @title               : getAllPublicStemcell
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	private List<StemcellManagementVO> getAllPublicStemcell() {
		List<StemcellManagementVO> publicStemcells = null;
		
		try {
			String isTruncated = "true";
			publicStemcells = new ArrayList<StemcellManagementVO>();

			//1. DocumentBuilderFactory 객체 생성
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 2. DocumentBuilder 객체 생성
			DocumentBuilder builder = factory.newDocumentBuilder();

			while ("true".equals(isTruncated)) {

				String url = PUBLIC_STEMCELLS_URL + "?prefix=bosh-stemcell";
				if (key != null && !key.isEmpty()) {
					url += "&marker=" + key;
				}

				//XML 문서를 파싱해서 java로 데이터 옮김
				Document doc = builder.parse(url);
				//dom tree가 xml 문서의 구조대로 완성
				doc.getDocumentElement().normalize();

				NodeList isTruncatedNodeList = doc.getElementsByTagName("IsTruncated");
				Node isTruncatedNode = isTruncatedNodeList.item(0);
				isTruncated = isTruncatedNode.getTextContent();

				publicStemcells = makePublicStemcells(publicStemcells, doc.getElementsByTagName("Contents"));
			}

			key = "";

		} catch (ParserConfigurationException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		} catch (SAXException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		} catch (IOException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		}

		return publicStemcells;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 목록 동기화
	 * @title               : syncPublicStemcell
	 * @return            : void
	***************************************************/
	public void syncPublicStemcell() {
		
		//1. 데이터 전체 삭제
		dao.deletePublicStemcells();
		
		//2. 외부에서 Public_Stemcells 목록을 가져온다
		List<StemcellManagementVO> publicStemcells = getAllPublicStemcell();
		
		//3. 세션 정보를 가져온다.
		SessionInfoDTO sessionInfo = new SessionInfoDTO();

		for (StemcellManagementVO stemcell : publicStemcells) {

			stemcell.setUpdateUserId(sessionInfo.getUserId());
			stemcell.setCreateUserId(sessionInfo.getUserId());
			
			/**public stemcells 동기화**/
			String keyInfo = stemcell.getSublink();

			if (!keyInfo.contains("go_agent") || keyInfo.contains("latest") || keyInfo.contains("raw")
					|| keyInfo.contains("centos-6"))
				continue;

			// '/'을 기준으로 토큰 추출
			StringTokenizer token = new StringTokenizer(keyInfo, "/");
			String stemcellName = "";
			
			//stemcellName 추출
			while(token.hasMoreElements()){
				String nextToken = token.nextToken();
				if(!(nextToken.contains("tgz"))){ continue; }
				stemcellName = nextToken;
			}
			
			String replaceStemcell = stemcellName.replace("ubuntu-lucid", "ubuntulucid");
			replaceStemcell = replaceStemcell.replace("ubuntu-trusty", "ubuntutrusty");
			replaceStemcell = replaceStemcell.replace("centos-7", "centos7");
			replaceStemcell = replaceStemcell.replace("xen-hvm", "xenhvm");
			replaceStemcell = replaceStemcell.replace("light-bosh", "lightbosh");

			String[] splited = replaceStemcell.split("-");

			String stemcellVersion = null;
			if (splited.length >= 6)
				stemcellVersion = splited[splited.length - 5];

			stemcell.setStemcellFileName(stemcellName);
			stemcell.setStemcellVersion(stemcellVersion);

			if (replaceStemcell.contains("ubuntu"))
				stemcell.setOs("Ubuntu");
			if (replaceStemcell.contains("centos"))
				stemcell.setOs("CentOS");

			if (replaceStemcell.contains("centos7"))
				stemcell.setOsVersion("7.x");
			if (!replaceStemcell.contains("centos7") && replaceStemcell.contains("centos"))
				stemcell.setOsVersion("6.x");

			if (replaceStemcell.contains("trusty"))
				stemcell.setOsVersion("Trusty");
			if (replaceStemcell.contains("lucid"))
				stemcell.setOsVersion("Lucid");

			if (replaceStemcell.contains("aws"))
				stemcell.setIaas("AWS");
			if (replaceStemcell.contains("openstack"))
				stemcell.setIaas("openstack");
			if (replaceStemcell.contains("vsphere"))
				stemcell.setIaas("vSphere");
		}
		dao.insertPublicStemcells(publicStemcells);
		
		List<String> list = getLocalStemcellList();
		for(int i=0;i<list.size();i++){
			dao.updateDownloadStatusByStemcellFileName(list.get(i),"Y");
		}
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : public 스템셀 목록 조회
	 * @title               : getStemcellList
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	public List<StemcellManagementVO> getStemcellList(String os, String osVersion, String iaas) {
		if ( dao.selectCount() == 0 ) {
			syncPublicStemcell();
		}
		
		//디비에 저장된 전체 스템셀
		List<StemcellManagementVO> stemcellList = dao.selectPublicStemcellList(os, osVersion, iaas);
		
		//로컬에 저장된 스템셀
		List<String> localStemcells = getLocalStemcellList();
		
		//로컬 스템셀 파일정보들
		List<Map<String, String>> localStemcellFileInfos = getLocalStemcellFileList();
		
		for (StemcellManagementVO stemcell : stemcellList) {
			if(stemcell.getDownloadStatus()!=null && stemcell.getDownloadStatus().toUpperCase().equals("Y")){
				for(int i=0;i<localStemcells.size();i++){
					File stemcellFile = new File(LocalDirectoryConfiguration.getStemcellDir()+System.getProperty("file.separator")+stemcell.getStemcellFileName());
					if(!stemcellFile.exists()){
						stemcell.setDownloadStatus(null);
						dao.updateDownloadStatusById(stemcell.getId().toString());
					}
				}
				if(localStemcells.size()==0){
					stemcell.setDownloadStatus(null);
					dao.updateDownloadStatusById(stemcell.getId().toString());
				}
			}
			String fileName = stemcell.getStemcellFileName();
			String fileSize = stemcell.getSize();
			stemcell.setIsExisted(existStemcells(localStemcells, (t) -> t.equals(fileName)) ? "Y" : "N");
			stemcell.setIsDose(doseStemcells(localStemcellFileInfos, (t) -> t.equals(fileName), (x) -> x.equals(fileSize) ) ? "Y" : "N");
		}
		Comparator<StemcellManagementVO> byStemcellVersion = Collections.reverseOrder(Comparator.comparing(StemcellManagementVO::getStemcellVersion));
		return stemcellList.stream().sorted(byStemcellVersion).collect(Collectors.toList());
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  전체 스템셀과 로컬 스템셀 비교하여 ture/false 반환
	 * @title               : existStemcells
	 * @return            : boolean
	***************************************************/
	public boolean existStemcells(List<String> localStemcells, Predicate<String> predicate) {
		for ( String localStemcell : localStemcells) {
			if ( predicate.test(localStemcell) ) {
				return true;
			}
		}
		return false;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 로컬의 파일이름과 사이즈를 통해 다운로드 진행 여부를 확인
	 * @title               : doseStemcells
	 * @return            : boolean
	***************************************************/
	public boolean doseStemcells(List<Map<String, String>> localStemcellInfo, Predicate<String> fileName,Predicate<String> fileSize){
		for ( Map<String, String> localStemcell : localStemcellInfo) {
			if ( fileName.test(localStemcell.get("fileName")) && fileSize.test(localStemcell.get("fileSize"))) {
				return true;
			}
		}
		return false;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Public_Stemcell에서 해당하는 스템셀을 다운로드
	 * @title               : doDownloadStemcell
	 * @return            : List<String>
	***************************************************/
	public List<String> doDownloadStemcell(String subLink, String stemcellFile, BigDecimal fileSize) {
		
		String downloadLink = PUBLIC_STEMCELLS_URL + "/" + subLink;
		
	    BufferedInputStream bufferIs = null;
	    FileOutputStream fout = null;
	    try {
	    	bufferIs = new BufferedInputStream(new URL(downloadLink).openStream());
	        fout = new FileOutputStream(LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator") +stemcellFile);

	        final byte data[] = new byte[4096];
	        int count;
	        while ((count = bufferIs.read(data, 0, 4096)) != -1) {
	            fout.write(data, 0, count);
	        }
	    } catch (FileNotFoundException e) {
	    	if( LOGGER.isErrorEnabled() ) {
	    		LOGGER.error( e.getMessage() );
	    	}
		} catch (MalformedURLException e) {
			if( LOGGER.isErrorEnabled() ) {
				LOGGER.error( e.getMessage() );
			}
		} catch (IOException e) {
			if( LOGGER.isErrorEnabled() ) {
				LOGGER.error( e.getMessage() );
			}
		} finally {
			try{
				 if (bufferIs != null) {
					 bufferIs.close();
				 }
			}catch(IOException e){
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
			}
			 try {
				 if (fout != null) {
					 fout.close();
					 fout = null;
				 }
			 } catch (IOException e) {
				if( LOGGER.isErrorEnabled() ){
					LOGGER.error( e.getMessage() );
				}
	        }
	    }		
		return null;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 다운로드된 스템셀 삭제
	 * @title               : doDeleteStemcell
	 * @return            : void
	***************************************************/
	public void doDeleteStemcell(String stemcellFile, String id)  {
		final String stemcellToDelete = LocalDirectoryConfiguration.getStemcellDir() + System.getProperty("file.separator")  + stemcellFile;
		File deleteFile = new File(stemcellToDelete);
		if( deleteFile.exists() ){
			if ( deleteFile.delete() ) {
				dao.updateDownloadStatusById(id);
			} 
		}else{
			throw new CommonException("notfound.publicStemcell.exception", "스템셀 삭제에 문제가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀들의 파일명과 크기 정보 조회
	 * @title               : getLocalStemcellFileList
	 * @return            : List<Map<String,String>>
	***************************************************/
	public List<Map<String, String>> getLocalStemcellFileList(){
		List<Map<String, String>> fileInfos = new ArrayList<>();
		//스템셀 파일 저장위치에 파일 리스트들을 가져온다.
		File dir = new File(LocalDirectoryConfiguration.getStemcellDir());
		File[] localFiles = dir.listFiles();
		 
		if(localFiles != null){
			for (File file : localFiles) {
				Map<String, String> fileInfo = new HashMap<>();
				fileInfo.put("fileName", file.getName());
				fileInfo.put("fileSize", String.valueOf(file.length()));
				fileInfos.add(fileInfo);
			}
		}
		return fileInfos;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 로컬 스템셀 콤보
	 * @title               : listLocalStemcells
	 * @return            : List<StemcellManagementVO>
	***************************************************/
	public List<StemcellManagementVO> listLocalStemcells(String iaas){
		return dao.selectLocalStemcellList(iaas);
	}
}
