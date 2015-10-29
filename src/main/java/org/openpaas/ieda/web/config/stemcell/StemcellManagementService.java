package org.openpaas.ieda.web.config.stemcell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	final private String PUBLIC_STEMCELLS_BASE_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com?prefix=bosh-stemcell";
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;
	
	private String key;
	
	private String getXMLNodeToString(Node node, String tagName) {
		NodeList nodeList = ((Element)node).getElementsByTagName(tagName);
		Element element = (Element)nodeList.item(0);
		return element.getTextContent();
	}
	
	private List<StemcellContent> makePublicStemcells(List<StemcellContent> publicStemcells, NodeList contents) {
		
		for (int i = 0; i < contents.getLength(); i++) {
			Node node = contents.item(i);
			
			key = getXMLNodeToString(node, "Key");
			
			StemcellContent stemcell = new StemcellContent();
			
			stemcell.setKey(key);
			stemcell.setLastModified(getXMLNodeToString(node, "LastModified"));
			stemcell.setEtag(getXMLNodeToString(node, "ETag"));
			stemcell.setSize(getXMLNodeToString(node, "Size"));
			stemcell.setStorageClass(getXMLNodeToString(node, "StorageClass"));
			
			publicStemcells.add(stemcell);
		}

		
		return publicStemcells;
	}
	
	private List<StemcellContent> getAllPublicStemcell() {
		List<StemcellContent> publicStemcells = null;
		
		try {
			String isTruncated = "true";
			publicStemcells = new ArrayList<StemcellContent>();
			
			int idx = 1;
			
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			DocumentBuilder b = f.newDocumentBuilder();
			
			while (isTruncated.equals("true")) {
			
				String url = PUBLIC_STEMCELLS_BASE_URL;
				if ( key != null && !key.isEmpty() ) {
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

	public List<StemcellContent> getPublicStemcell() {
		
		List<StemcellContent> publicStemcells = getAllPublicStemcell();
		
		for (StemcellContent stemcell : publicStemcells) {
			
			String keyInfo = stemcell.getKey();
			
			if (!keyInfo.contains("go_agent") || keyInfo.contains("latest")) continue;
			
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
			if ( splited.length >= 6 )
				stemcellVersion = splited[splited.length-5];
			
			// Stemcell 이름과 버전
			stemcell.setStemcellFileName(stemcellName);
			stemcell.setStemcellVersion(stemcellVersion);

			// OS 구분
			if ( stemcellName.contains("ubuntu") ) stemcell.setOs("Ubuntu");
			if ( stemcellName.contains("centos") ) stemcell.setOs("CentOS");

			// OS 버전 구분
			if ( !stemcellName.contains("centos7") && stemcellName.contains("centos")) stemcell.setOsVersion("6.x");
			if ( stemcellName.contains("centos7") ) stemcell.setOsVersion("7.x");
			
			if ( stemcellName.contains("trusty") ) stemcell.setOsVersion("Trusty");
			if ( stemcellName.contains("lucid") ) stemcell.setOsVersion("lucid");
			
			// IaaS 구분
			if ( stemcellName.contains("aws")) stemcell.setIaas("aws");
			if ( stemcellName.contains("openastck")) stemcell.setIaas("openastck");
			if ( stemcellName.contains("vsphere")) stemcell.setIaas("vsphere");			
		}
		
		Comparator<StemcellContent> byLastModified = 
				Collections.reverseOrder(Comparator.comparing(StemcellContent::getLastModified));
		
		return publicStemcells
				.stream()
				.filter(t -> t.getOs()!= null && t.getOs().length() > 0 )
				.filter(t -> t.getOsVersion() != null && t.getOsVersion().length() > 0 )
				.filter(t -> t.getIaas() != null && t.getIaas().length() > 0 )
				.sorted(byLastModified)
				.collect(Collectors.toList());
	}
}
