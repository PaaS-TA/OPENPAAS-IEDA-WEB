package org.openpaas.ieda.web.config.setting.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.dto.DirectorConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DirectorConfigService  {
	
	@Autowired private DirectorConfigDAO dao;
	private final static Logger LOGGER = LoggerFactory.getLogger(DirectorConfigService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 정보 조회
	 * @title               : getDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO getDefaultDirector() {
		
		//기본 설치 관리자 존재 여부 조회
		DirectorConfigVO directorConfig = dao.selectDirectorConfigByDefaultYn("Y");
		if( directorConfig != null ){
			boolean flag = checkDirectorConnect(directorConfig.getDirectorUrl(), directorConfig.getDirectorPort(), directorConfig.getUserId(), directorConfig.getUserPassword());
			directorConfig.setConnect(flag);
		}
		return directorConfig;
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 정보 목록을 DefaultYn를 기준으로 역 정렬하여 전체 조회한 값을 응답
	 * @title               : listDirector
	 * @return            : List<DirectorConfigVO>
	***************************************************/
	public List<DirectorConfigVO> listDirector() {
		
		//설치관리자 목록 전체조회
		List<DirectorConfigVO> resultList = dao.selectDirectorConfig();
		
		//DefaultYn을 비교하여 역정렬
		Comparator<DirectorConfigVO> byDefaultYN = Collections.reverseOrder(Comparator.comparing(DirectorConfigVO::getDefaultYn));
		
		if ( resultList.size() == 0 ) {
			throw new CommonException("nocontent.director.exception",
					"디렉터 정보가 존재하지 않습니다.", HttpStatus.NO_CONTENT);
		}
	
		
		return resultList.stream().sorted(byDefaultYN).collect(Collectors.toList());
	}
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : HttpClient에 요청하여 설치관리자 정보를 읽어옴
	 * @title               : getDirectorInfo
	 * @return            : DirectorInfoDTO
	***************************************************/
	public DirectorInfoDTO getDirectorInfo(String directorUrl, int port, String userId, String password) {
		DirectorInfoDTO info = null;
		try {
			HttpClient client = DirectorRestHelper.getHttpClient(port);
			GetMethod get = new GetMethod(DirectorRestHelper.getInfoURI(directorUrl, port)); 
			get = (GetMethod)DirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
			client.executeMethod(get);
		
			ObjectMapper mapper = new ObjectMapper();
			info = mapper.readValue(get.getResponseBodyAsString(), DirectorInfoDTO.class);
		} catch (ResourceAccessException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		} catch(NoRouteToHostException e){
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		} catch (Exception e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error( e.getMessage() );
			}
		}
		
		return info;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 조회
	 * @title               : checkDirectorConnect
	 * @return            : boolean
	***************************************************/
	public boolean checkDirectorConnect(String directorUrl, int port, String userId, String password) {
		boolean flag = true;
		try {
			
			HttpClient client = DirectorRestHelper.getHttpClient(port);
			GetMethod get = new GetMethod(DirectorRestHelper.getInfoURI(directorUrl, port)); 
			get = (GetMethod)DirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get); 
			client.executeMethod(get);
		
		} catch (ResourceAccessException e) {
			return false;
		} catch(NoRouteToHostException e){
			return false;
		}catch(UnknownHostException e){
			return false;
		}catch(IOException e){
			return false;
		}
		
		return flag;
	}



	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : 설치관리자 설정 추가
	 * @title :  createDirector
	 * @return : DirectorConfigVO
	 ***************************************************/
	public int createDirector(DirectorConfigDTO.Create createDto) {

		//해당 설치관리자가 존재하는지 확인한다
		List<DirectorConfigVO> resultList = dao.selectDirectorConfigByDirectorUrl(createDto.getDirectorUrl());
		//세션 정보를 가져온다.
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		if ( !resultList.isEmpty() ) {
			throw new CommonException("duplicated.director.exception",
					"이미 등록되어 있는 디렉터 URL입니다.", HttpStatus.CONFLICT);
		}
		
		//설치관리자 정보를 확인한다.
		DirectorInfoDTO info = getDirectorInfo(createDto.getDirectorUrl()
								, createDto.getDirectorPort()
								, createDto.getUserId()
								, createDto.getUserPassword());
		
		
		if ( info == null || StringUtils.isEmpty(info.getUser())) {
			throw new CommonException("unauthenticated.director.exception",
					"디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		DirectorConfigVO director = new DirectorConfigVO();

		director.setUserId(createDto.getUserId());
		director.setUserPassword(createDto.getUserPassword());
		director.setDirectorUrl(createDto.getDirectorUrl());
		director.setDirectorPort(createDto.getDirectorPort());
		director.setDirectorName(info.getName());
		director.setDirectorUuid(info.getUuid());
		if(info.getCpi().indexOf("_cpi") == -1){
			info.setCpi(info.getCpi()+"_cpi");
		}
		director.setDirectorCpi(info.getCpi());
		director.setDirectorVersion(info.getVersion());
		director.setCreateUserId(sessionInfo.getUserId());
		director.setUpdateUserId(sessionInfo.getUserId());
		
		//기존에 기본 관리자가 존재한다면 N/ 존재하지않는다면 기본 관리자로 설정
		DirectorConfigVO directorConfig = dao.selectDirectorConfigByDefaultYn("Y");
		
		director.setDefaultYn((directorConfig == null ) ? "Y":"N");
		
		if( director.getDefaultYn().equals("Y") ) {
			setBoshConfigFile(director);
		}	

		//입력된 설치관리자 정보를 데이터베이스에 저장한다.
		return dao.insertDirector(director);
	}
	
	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : 설치관리자 설정 조회
	 * @title :  getDirectorConfig
	 * @return : DirectorConfigVO
	 ***************************************************/
	public DirectorConfigVO getDirectorConfig(int seq) {
		DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);

		if (directorConfig == null) {
			throw new CommonException("notfonud.director.exception",
					"해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		return directorConfig;
	}

	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : 설치관리자 설정 수정 
	 * @title :  updateDirectorConfig
	 * @return : DirectorConfigVO
	 ***************************************************/
	public int updateDirectorConfig(DirectorConfigDTO.Update updateDto) {
		//1. 해당 설치관리자가 존재하는지 확인한다.
		SessionInfoDTO session = new SessionInfoDTO();
		DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(updateDto.getIedaDirectorConfigSeq());
		
		if ( directorConfig == null )
			throw new CommonException("notfound.director_update.exception",
					"디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		
		//2. 설치관리자 정보를 확인한다.
		DirectorInfoDTO info = getDirectorInfo(directorConfig.getDirectorUrl()
				, directorConfig.getDirectorPort()
				, updateDto.getUserId()
				, updateDto.getUserPassword());
		
		if ( info == null || StringUtils.isEmpty(info.getUser()) )
			throw new CommonException("unauthenticated.director.exception",
					"디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);

		//3. 입력된 설치관리자 정보를 데이터베이스에 저장한다.
		
		directorConfig.setUserId(updateDto.getUserId());
		directorConfig.setUserPassword(updateDto.getUserPassword());
		

		setBoshConfigFile(directorConfig);
		directorConfig.setUpdateUserId(session.getUserId());

		return dao.updateDirector(directorConfig);
	}

	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : 설치관리자 설정 삭제
	 * @title :  deleteDirectorConfig
	 * @return : void
	 ***************************************************/
	@SuppressWarnings("unchecked")
	public void deleteDirectorConfig(int seq) {
		//1. 해당 설치관리자가 존재하는지 확인한다.
		DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);
		if (directorConfig == null) {
			throw new CommonException("notfound.director.exception",
					"해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		InputStream input = null;
		OutputStreamWriter fileWriter = null;
		try {
			//설치관리자 삭제를 수행한다.
			dao.deleteDirecotr(seq);
			String directorLink = "https://" + directorConfig.getDirectorUrl() + ":" + directorConfig.getDirectorPort();
			
			input = new FileInputStream(new File(getBoshConfigLocation()));
			Yaml yaml = new Yaml();
			Map<String, Object> object = (Map<String, Object>)yaml.load(input);

			Map<String, Object> authMap = (Map<String,Object>)object.get("auth");
			authMap.put(directorLink, null);
			Charset.forName("UTF-8").newEncoder();
			String boshConfigFile= getBoshConfigLocation();
			fileWriter = new OutputStreamWriter(new FileOutputStream(boshConfigFile),"UTF-8");
			StringWriter stringWriter = new StringWriter();
			yaml.dump(object, stringWriter);
			fileWriter.write(stringWriter.toString());
		}catch (FileNotFoundException e){
			throw new CommonException("notfound.director.exception",
					"설치관리자 삭제 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(UnsupportedEncodingException e){
			throw new CommonException("unsupportedencoding.director.exception",
					"설치관리자 설정 파일의 인코딩이 서포트되고 있지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(IOException e){
			throw new CommonException("unsupportedencoding.director.exception",
					"설치관리자 설정 파일에 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(NullPointerException e){
			throw new CommonException("nullpoint.director.exception",
					"설치관리자 설정 파일에 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		finally {
			try {
				if(fileWriter != null){
					fileWriter.close();
				}
				if(input != null) input.close();
			} catch (IOException e) {
				throw new CommonException("server.director.exception",
						"읽어오는중 오류가 발생했습니다!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : 기본 설치 관리자 설정
	 * @title :  setDefaultDirector
	 * @return : DirectorConfigVO
	 ***************************************************/
	public DirectorConfigVO setDefaultDirector(int seq) {
		//1. 설치관리자가 존재하는지 확인한다.
		DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);
		if (directorConfig == null) {
			throw new CommonException("notfound.director.exception",
					"해당하는 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		//2.	설치관리자 정보를 확인한다
		DirectorInfoDTO info = getDirectorInfo(directorConfig.getDirectorUrl(), directorConfig.getDirectorPort(), directorConfig.getUserId(), directorConfig.getUserPassword());
		if ( info == null || StringUtils.isEmpty(info.getUser()) ) {
			throw new CommonException("unauthenticated.director.exception",
					"해당 디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		//3. 기존 기본관리자의 상태를 N으로 바꾸고 새로운 기본 관리자를 셋팅한다.
		DirectorConfigVO oldDefaultDiretor = dao.selectDirectorConfigByDefaultYn("Y");
		//4. 세션 정보를 가져온다.
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
				
		if (oldDefaultDiretor != null) {
			oldDefaultDiretor.setDefaultYn("N");
			oldDefaultDiretor.setUpdateUserId(sessionInfo.getUserId());
			dao.updateDirector(oldDefaultDiretor);
		}
		
		directorConfig.setDefaultYn("Y");
		directorConfig.setDirectorName(info.getName());
		directorConfig.setDirectorUuid(info.getUuid());
		if(info.getCpi().indexOf("_cpi") == -1){
			info.setCpi(info.getCpi()+"_cpi");
		}
		directorConfig.setDirectorCpi(info.getCpi());
		directorConfig.setDirectorVersion(info.getVersion());
		directorConfig.setUpdateUserId(sessionInfo.getUserId());
		
		setBoshConfigFile(directorConfig);
		dao.updateDirector(directorConfig);
		
		return directorConfig;
	}

	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : bosh_config 파일이 있는지 여부에 따라 설치 관리자 타겟을 설정하거나 파일을 생성
	 * @title :  setBoshConfigFile
	 * @return : void
	 ***************************************************/
	@SuppressWarnings("unchecked")
	public void setBoshConfigFile(DirectorConfigVO directorConfig) {
		
		String directorLink = "https://" + directorConfig.getDirectorUrl() + ":" + directorConfig.getDirectorPort();

		//bosh_config 파일이 있는지 여부
		if ( isExistBoshConfigFile() ) {
			InputStream input = null;
			OutputStreamWriter fileWriter = null;
			try {
				String boshConfigFile= getBoshConfigLocation();
				input = new FileInputStream(new File( boshConfigFile));
				Yaml yaml = new Yaml();
				//bosh_config 파일을 로드하여 Map<String, Object>에 parse한다.
				Map<String, Object> object = (Map<String, Object>)yaml.load(input);
				
				if ( directorConfig.getDefaultYn().equals("Y")) {
					object.put("target", directorLink);
					object.put("target_name", directorConfig.getDirectorName());
					object.put("target_version", directorConfig.getDirectorVersion());
					object.put("target_uuid", directorConfig.getDirectorUuid());
				}
				
				Map<String, String> certMap = (Map<String,String>)object.get("ca_cert");
				certMap.put(directorLink, null);
				
				Map<String, Object> aliasMap = (Map<String,Object>)object.get("aliases");
				Map<String, Object> targetMap = (Map<String,Object>)aliasMap.get("target");
				targetMap.put(directorConfig.getDirectorUuid(), directorLink);
				
				Map<String, String> accountMap = new HashMap<String, String>();
				accountMap.put("username", directorConfig.getUserId());
				accountMap.put("password", directorConfig.getUserPassword());
				
				Map<String, Object> authMap = (Map<String,Object>)object.get("auth");
				authMap.put(directorLink, accountMap);
				 Charset.forName("UTF-8").newEncoder();
				//1. bosh_config 파일을 출력하기 위한  FileWriter 객체 생성
				fileWriter = new OutputStreamWriter(new FileOutputStream(boshConfigFile),"UTF-8");
				//2. StringWriter 객체 생성
				StringWriter stringWriter = new StringWriter();
				yaml.dump(object, stringWriter);
				fileWriter.write(stringWriter.toString());
			
			} catch (IOException e) {
				throw new CommonException("taretDirector.director.exception",
						"설치관리자 타겟 설정 중 오류 발생하였습니다.", HttpStatus.NOT_FOUND);
			} catch (NullPointerException e){
				throw new CommonException("notfound.directorFile.exception",
						"설치관리자 관리 파일을 읽어오는 중 오류가 발생했습니다.", HttpStatus.NOT_FOUND);
			}finally {
				try {
					if(fileWriter != null) fileWriter.close();
					if(input != null) input.close();
				} catch (IOException e) {
					throw new CommonException("taretDirector.director.exception",
							"읽어오는중 오류가 발생했습니다!", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}else {
			OutputStreamWriter osw = null;
			try {
				Map<String, Object> newConfig = new HashMap<String, Object>();
				
				if ( directorConfig.getDefaultYn().equals("Y")) {
					newConfig.put("target", directorLink);
					newConfig.put("target_name", directorConfig.getDirectorName());
					newConfig.put("target_version", directorConfig.getDirectorVersion());
					newConfig.put("target_uuid", directorConfig.getDirectorUuid());
				}
				else {
					DirectorConfigVO directorVo = getDefaultDirector();
					newConfig.put("target", "https://" + directorVo.getDirectorUrl() + ":" + directorVo.getDirectorPort());
					newConfig.put("target_name", directorVo.getDirectorName());
					newConfig.put("target_version", directorVo.getDirectorVersion());
					newConfig.put("target_uuid", directorVo.getDirectorUuid());
				}
				
				Map<String, Object> certMap = new HashMap<String, Object>();
				certMap.put(directorLink, null);
				newConfig.put("ca_cert", certMap);
				
				Map<String, Object> aliasesMap = new HashMap<String, Object>();
				aliasesMap.put(directorConfig.getDirectorUuid(), directorLink);
	
				Map<String, Object> targetMap = new HashMap<String, Object>();
				targetMap.put("target", aliasesMap);
				
				newConfig.put("aliases", targetMap);
				
				Map<String, String> accountInfo = new HashMap<String, String>();
				accountInfo.put("username", directorConfig.getUserId());
				accountInfo.put("password", directorConfig.getUserPassword());
				
				Map<String, Object> authMap = new HashMap<String, Object>();
				authMap.put(directorLink, accountInfo);
				newConfig.put("auth", authMap);
				
				Yaml yaml = new Yaml();
				String boshConfigFile= getBoshConfigLocation();
				osw = new OutputStreamWriter(new FileOutputStream(boshConfigFile),"UTF-8");
				StringWriter stringWriter = new StringWriter();
				yaml.dump(newConfig, stringWriter);
				osw.write(stringWriter.toString());
				
			} catch (IOException e) {
				throw new CommonException("taretDirector.director.exception",
						"설치관리자 설정 파일 생성 중 오류 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				try {
					if(osw!=null) osw.close();
				} catch (IOException e) {
					throw new CommonException("taretDirector.director.exception",
							"읽어오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
	}
	
	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : bosh_config 파일 설정
	 * @title :  getBoshConfigLocation
	 * @return : String
	 ***************************************************/
	public String getBoshConfigLocation() {
		String homeDir = System.getProperty("user.home"); //User's home directory
		String fileSeperator = System.getProperty("file.separator");//File separator ("/" on UNIX)
		String boshConfigFile = homeDir + fileSeperator + ".bosh_config"; //

		return boshConfigFile;
	}
	
	
	/***************************************************
	 * @project : OpenPaas 플랫폼 설치 자동
	 * @description : bosh_config 파일 존재 여부
	 * @title :  isExistBoshConfigFile
	 * @return : boolean
	 ***************************************************/
	public boolean isExistBoshConfigFile() {
		File file = new File(getBoshConfigLocation());
		return file.exists();
	}
}
