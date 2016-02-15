package org.openpaas.ieda.web.config.stemcell;

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

import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StemcellManagementService {

	final private String PUBLIC_STEMCELLS_BASE_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";

	@Autowired
	private IEDAStemcellManagementRepository repository;

	private String key;
	
	public List<String> getLocalStemcellList() {
		File dir = new File(LocalDirectoryConfiguration.getStemcellDir());
		File[] localFiles = dir.listFiles();
		List<String> localStemcells = new ArrayList<>();
		for (File file : localFiles) {
			localStemcells.add(file.getName());
		}
		 
		return localStemcells;
	}

	private String getXMLNodeToString(Node node, String tagName) {
		NodeList nodeList = ((Element) node).getElementsByTagName(tagName);
		Element element = (Element) nodeList.item(0);
		return element.getTextContent();
	}

	private List<StemcellManagementConfig> makePublicStemcells(List<StemcellManagementConfig> publicStemcells, NodeList contents) {

		for (int i = 0; i < contents.getLength(); i++) {
			Node node = contents.item(i);

			key = getXMLNodeToString(node, "Key");

			StemcellManagementConfig stemcell = new StemcellManagementConfig();

			stemcell.setKey(key);
			stemcell.setLastModified(getXMLNodeToString(node, "LastModified"));
			stemcell.setEtag(getXMLNodeToString(node, "ETag"));
			stemcell.setSize(getXMLNodeToString(node, "Size"));
			stemcell.setStorageClass(getXMLNodeToString(node, "StorageClass"));

			publicStemcells.add(stemcell);
		}

		return publicStemcells;
	}

	private List<StemcellManagementConfig> getAllPublicStemcell() {
		List<StemcellManagementConfig> publicStemcells = null;
		
		try {
			String isTruncated = "true";
			publicStemcells = new ArrayList<StemcellManagementConfig>();

			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();

			while (isTruncated.equals("true")) {

				String url = PUBLIC_STEMCELLS_BASE_URL + "?prefix=bosh-stemcell";
				if (key != null && !key.isEmpty()) {
					url += "&marker=" + key;
				}

				Document doc = b.parse(url);
				doc.getDocumentElement().normalize();

				NodeList isTruncatedNodeList = doc.getElementsByTagName("IsTruncated");
				Node isTruncatedNode = isTruncatedNodeList.item(0);
				isTruncated = isTruncatedNode.getTextContent();

				publicStemcells = makePublicStemcells(publicStemcells, doc.getElementsByTagName("Contents"));
			}

			key = "";

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return publicStemcells;
	}

	public void syncPublicStemcell() {
		
		repository.deleteAll();
		
		List<StemcellManagementConfig> publicStemcells = getAllPublicStemcell();

		for (StemcellManagementConfig stemcell : publicStemcells) {

			String keyInfo = stemcell.getKey();

			if (!keyInfo.contains("go_agent") || keyInfo.contains("latest") || keyInfo.contains("raw")
					|| keyInfo.contains("centos-6"))
				continue;

			StringTokenizer st = new StringTokenizer(keyInfo, "/");
			st.nextToken();
			String cpi = st.nextToken();
			String stemcellName = st.nextToken();

			String replaceStemcellName = stemcellName.replace("ubuntu-lucid", "ubuntulucid");
			replaceStemcellName = replaceStemcellName.replace("ubuntu-trusty", "ubuntutrusty");
			replaceStemcellName = replaceStemcellName.replace("centos-7", "centos7");
			replaceStemcellName = replaceStemcellName.replace("xen-hvm", "xenhvm");
			replaceStemcellName = replaceStemcellName.replace("light-bosh", "lightbosh");

			String[] splited = replaceStemcellName.split("-");

			String stemcellVersion = null;
			if (splited.length >= 6)
				stemcellVersion = splited[splited.length - 5];

			stemcell.setStemcellFileName(stemcellName);
			stemcell.setStemcellVersion(stemcellVersion);

			if (replaceStemcellName.contains("ubuntu"))
				stemcell.setOs("Ubuntu");
			if (replaceStemcellName.contains("centos"))
				stemcell.setOs("CentOS");

			if (replaceStemcellName.contains("centos7"))
				stemcell.setOsVersion("7.x");
			if (!replaceStemcellName.contains("centos7") && replaceStemcellName.contains("centos"))
				stemcell.setOsVersion("6.x");

			if (replaceStemcellName.contains("trusty"))
				stemcell.setOsVersion("Trusty");
			if (replaceStemcellName.contains("lucid"))
				stemcell.setOsVersion("Lucid");

			if (replaceStemcellName.contains("aws"))
				stemcell.setIaas("AWS");
			if (replaceStemcellName.contains("openstack"))
				stemcell.setIaas("openstack");
			if (replaceStemcellName.contains("vsphere"))
				stemcell.setIaas("vSphere");
		}
		repository.save(publicStemcells);

	}
	
	public List<StemcellManagementConfig> getStemcellList(String os, String osVersion, String iaas) {
		if ( repository.count() == 0 ) {
			syncPublicStemcell();
		}
		
		List<StemcellManagementConfig> stemcellList = repository.findByOsAndOsVersionAndIaasAllIgnoreCaseOrderByOsVersionDesc(os, osVersion, iaas);
		
		List<String> localStemcells = getLocalStemcellList();
		List<Map<String, String>> localStemcellFileInfos = getLocalStemcellFileList();

		for (StemcellManagementConfig stemcell : stemcellList) {
			stemcell.setIsExisted((existStemcells(localStemcells, (t) -> t.equals(stemcell.getStemcellFileName()))) ? "Y" : "N");
			stemcell.setIsDose((doseStemcells(localStemcellFileInfos, (t) -> t.equals(stemcell.getStemcellFileName()), (x) -> x.equals(stemcell.getSize()) )) ? "Y" : "N");
		}
		
		Comparator<StemcellManagementConfig> byStemcellVersion = Collections.reverseOrder(Comparator.comparing(StemcellManagementConfig::getStemcellVersion));
		return stemcellList.stream().sorted(byStemcellVersion).collect(Collectors.toList());
	}
	
	public boolean existStemcells(List<String> localStemcells, Predicate<String> predicate) {
		for ( String localStemcell : localStemcells) {
			if ( predicate.test(localStemcell) ) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean doseStemcells(List<Map<String, String>> localStemcellFileInfos, Predicate<String> fileName,Predicate<String> fileSize){
		for ( Map<String, String> localStemcell : localStemcellFileInfos) {
			if ( fileName.test(localStemcell.get("fileName")) && fileSize.test(localStemcell.get("fileSize"))) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> doDownloadStemcell(String subLink, String stemcellFile, BigDecimal fileSize) {
		log.debug("stemcell Dir     : " + LocalDirectoryConfiguration.getStemcellDir());
		log.debug("Stemcell Name    : " + PUBLIC_STEMCELLS_BASE_URL + "/"  + stemcellFile);
		log.debug("Stemcell Size    : " + fileSize);
		log.debug("downloaded  file : " + LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator") +stemcellFile);
		
		String downloadLink = PUBLIC_STEMCELLS_BASE_URL + "/" + subLink;
		
	    BufferedInputStream in = null;
	    FileOutputStream fout = null;
	    double received = 0;
	    try {
	        in = new BufferedInputStream(new URL(downloadLink).openStream());
	        fout = new FileOutputStream(LocalDirectoryConfiguration.getStemcellDir()+ System.getProperty("file.separator") +stemcellFile);

	        final byte data[] = new byte[4096];
	        int count;
	        double total = fileSize.doubleValue();
	        while ((count = in.read(data, 0, 4096)) != -1) {
	            fout.write(data, 0, count);
	            received += count;
	            log.debug("progress : " + (int)((received/total) *100));
	        }
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        if (in != null) {
	            try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        if (fout != null) {
	            try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }		

		return null;
	}
	
	public void doDeleteStemcell(String stemcellFile) {
		final String stemcellToDelete = LocalDirectoryConfiguration.getStemcellDir() + System.getProperty("file.separator")  + stemcellFile;
		try {
			
			File file = new File(stemcellToDelete);
			
			if ( file.delete() ) {
				return;
			} else {
				throw new IEDACommonException("failedDeleteOperation.publicStemcell.exception",
						"스템셀 삭제를 실패하였습니다.", HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			throw new IEDACommonException("failedDeleteOperation.publicStemcell.exception",
					"스템셀 삭제 중에 오류가 발생하였습니다.", HttpStatus.NO_CONTENT);
		}
		
	}
	
	public List<Map<String, String>> getLocalStemcellFileList(){
		List<Map<String, String>> fileInfos = new ArrayList<>();
		
		File dir = new File(LocalDirectoryConfiguration.getStemcellDir());
		File[] localFiles = dir.listFiles();
		 
		for (File file : localFiles) {
			Map<String, String> fileInfo = new HashMap<>();
			fileInfo.put("fileName", file.getName());
			fileInfo.put("fileSize", String.valueOf(file.length()));
			fileInfos.add(fileInfo);
		}
		return fileInfos;
	}
}
