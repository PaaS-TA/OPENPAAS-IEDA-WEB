package org.openpaas.ieda.web.config.systemStemcell;
 
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openpaas.ieda.common.LocalDirectoryConfiguration;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementDAO;
import org.openpaas.ieda.web.config.stemcell.dao.StemcellManagementVO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO.Download;
import org.openpaas.ieda.web.config.stemcell.service.StemcellManagementDownloadAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
 
@Service
@Transactional
@TestPropertySource(locations="classpath:application_test.properties")
public class StemcellManagementServiceTest {
    @Autowired private StemcellManagementDAO dao;
    final static private String PUBLIC_STEMCELLS_BASE_URL = "https://bosh-jenkins-artifacts.s3.amazonaws.com";
    final static private int BUFFER_SIZE = 500; 
    final private static String SEPARATOR = System.getProperty("file.separator");
    final private static String STEMCELL_PATH = LocalDirectoryConfiguration.getStemcellDir() + SEPARATOR + "dummy-stemcell.tgz";
    private final static Logger LOGGER = LoggerFactory.getLogger(StemcellManagementDownloadAsyncService.class);
    final private static String PUBLIC_STEMCELLS_PATH = System.getProperty("user.dir") + SEPARATOR + "src/test/java/org/openpaas/ieda/web/assets/bosh-stemcell.xml";
    
    
    
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 스템셀 다운로드
     * @title               : testDoDownload
     * @return            : void
    ***************************************************/
    @Rollback(true)
    public void testDoDownload(Download dto) {
         
        String downloadLink = PUBLIC_STEMCELLS_BASE_URL + "/" + dto.getSublink();
        BufferedInputStream bufferIs = null;
        FileOutputStream fout = null;
         
        int percentage = 0;
        double received = 0;
        double stemcellSize = Double.parseDouble(dto.getFileSize());
        Boolean isError = Boolean.FALSE;
        try {
            bufferIs = new BufferedInputStream(new URL(downloadLink).openStream());
            fout = new FileOutputStream(STEMCELL_PATH);
             
            final byte data[] = new byte[BUFFER_SIZE];
            int count;
            while ((count = bufferIs.read(data, 0, BUFFER_SIZE)) != -1) {
                fout.write(data, 0, count);
                received += count;
                if(percentage != (int)((received/stemcellSize) *100)){ 
                    percentage = (int)((received/stemcellSize) *100);
                    if(LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Public Stemcell Downloading...." + percentage); 
                    }
                }
            }
             
            File stemcellFile = new File(STEMCELL_PATH);
            int fileLength = Integer.parseInt(dto.getFileSize());
            if(stemcellFile.exists() && stemcellFile.length() == fileLength){
                dto.setUpdateUserId("admin");
                dto.setDownloadStatus("Y");
                dao.updateDownloadStatusById(dto.getId().toString());
            }
        } catch (FileNotFoundException e) {
            isError = Boolean.TRUE;
        } catch (MalformedURLException e) {
            isError = Boolean.TRUE;
        } catch (IOException e) {
            isError = Boolean.TRUE;
        } finally {
            if (bufferIs != null) {
                try {
                    bufferIs.close();
                } catch (IOException e) {
                	if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );  
					}
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                    if(isError){//에러발생시 파일 삭제
                        File targetFile = new File(STEMCELL_PATH);
                        boolean result = targetFile.delete();
                        if(!result){
                            LOGGER.info("파일 삭제 성공");
                        }
                    }
                } catch (IOException e) {
                	if( LOGGER.isErrorEnabled() ){
						LOGGER.error( e.getMessage() );  
					}
                }
            }
             
        }
    }
     
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 스템셀 목록 동기화
     * @title               : syncPublicStemcell
     * @return            : List<StemcellManagementVO>
    ***************************************************/
    public List<StemcellManagementVO> syncPublicStemcell() {
         
        //1. 데이터 전체 삭제
        dao.deletePublicStemcells();
         
        //2. 외부에서 Public_Stemcells 목록을 가져온다
        List<StemcellManagementVO> publicStemcells = getAllPublicStemcell();
         
        for (StemcellManagementVO stemcell : publicStemcells) {
            stemcell.setUpdateUserId("admin");
            stemcell.setCreateUserId("admin");
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
            publicStemcells = new ArrayList<StemcellManagementVO>();
            File file = new File(PUBLIC_STEMCELLS_PATH);
            //1. DocumentBuilderFactory 객체 생성
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 2. DocumentBuilder 객체 생성
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();
            publicStemcells = makePublicStemcells(publicStemcells, doc);
 
             
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
     * @description   : Public_Stemcells 정보 설정
     * @title               : makePublicStemcells
     * @return            : List<StemcellManagementVO>
    ***************************************************/
    private List<StemcellManagementVO> makePublicStemcells(List<StemcellManagementVO> publicStemcells, Document doc) {
        NodeList contents = doc.getElementsByTagName("Contents");
        for (int i = 0; i < contents.getLength(); i++) {
            Node contentsNode = contents.item(i);
            Element contentsElmnt = (Element) contentsNode;
            NodeList keysList= contentsElmnt.getElementsByTagName("Key");
            Element keysElmnt = (Element) keysList.item(0);
            NodeList lastModifiedsList= contentsElmnt.getElementsByTagName("LastModified");
            Element lastModifieds = (Element) lastModifiedsList.item(0);
            NodeList eTagsList= contentsElmnt.getElementsByTagName("ETag");
            Element eTagsListElmnt = (Element) eTagsList.item(0);
            NodeList sizeList= contentsElmnt.getElementsByTagName("Size");
            Element sizeElmnt = (Element) sizeList.item(0);
            
            StemcellManagementVO stemcell = new StemcellManagementVO();
            stemcell.setSublink(keysElmnt.getTextContent());
            stemcell.setLastModified(lastModifieds.getTextContent());
            stemcell.setEtag(eTagsListElmnt.getTextContent());
            stemcell.setSize(sizeElmnt.getTextContent());
 
            publicStemcells.add(stemcell);
        }
 
        return publicStemcells;
    }
 
}