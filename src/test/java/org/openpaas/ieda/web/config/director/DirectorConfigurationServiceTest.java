package org.openpaas.ieda.web.config.director;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openpaas.ieda.api.director.dto.DirectorInfoDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.dto.DirectorConfigDTO;
import org.openpaas.ieda.web.config.setting.dto.DirectorConfigDTO.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

@Service
@Transactional
@TestPropertySource(locations="classpath:application_test.properties")
public class DirectorConfigurationServiceTest {
	
	@Autowired private DirectorConfigDAO dao;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 관리자 설정 추가
	 * @title               : createDirector
	 * @return            : int
	***************************************************/
	@Rollback(true)
	public int createDirector(DirectorConfigDTO.Create createDto) {
		List<DirectorConfigVO> resultList = dao.selectDirectorConfigByDirectorUrl(createDto.getDirectorUrl());
		if ( !resultList.isEmpty() ) {
			throw new CommonException("duplicated.director.exception",
					"이미 등록되어 있는 디렉터 URL입니다.", HttpStatus.BAD_REQUEST);
		}
		DirectorInfoDTO info = getDirectorInfo();
		if ( info == null || StringUtils.isEmpty(info.getUser())) {
			throw new CommonException("unauthenticated.director.exception",
					"디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		DirectorConfigVO director = new DirectorConfigVO();
		director.setIedaDirectorConfigSeq(createDto.getIedaDirectorConfigSeq());
		director.setUserId(createDto.getUserId());
		director.setUserPassword(createDto.getUserPassword());
		director.setDirectorUrl(createDto.getDirectorUrl());
		director.setDirectorPort(createDto.getDirectorPort());
		director.setDirectorName(info.getName());
		director.setDirectorUuid(info.getUuid());
		director.setDirectorCpi(info.getCpi());
		director.setDirectorVersion(info.getVersion());
		director.setCreateUserId("admin");
		director.setUpdateUserId("admin");
		
		//기존에 기본 관리자가 존재한다면 N/ 존재하지않는다면 기본 관리자로 설정
		DirectorConfigVO directorConfig = dao.selectDirectorConfigByDefaultYn("Y");
		director.setDefaultYn((directorConfig == null ) ? "Y":"N");
		setBoshConfigFile(director);
		//입력된 설치관리자 정보를 데이터베이스에 저장한다.
		return dao.insertDirector(director);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 설정 수정
	 * @title               : updateDirector
	 * @return            : int
	***************************************************/
	@Rollback(true)
	public int updateDirector(Update updateDto) {
		//1. 해당 설치관리자가 존재하는지 확인한다.
		DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(updateDto.getIedaDirectorConfigSeq());
		
		if ( directorConfig == null )
			throw new CommonException("notfound.director_update.exception",
					"디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		
		//2. 설치관리자 정보를 확인한다.
		DirectorInfoDTO info = getDirectorInfo();
		
		if ( info == null || StringUtils.isEmpty(info.getUser()) )
			throw new CommonException("badRequest.director.exception",
					"디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);

		//3. 입력된 설치관리자 정보를 데이터베이스에 저장한다.
		
		directorConfig.setUserId(updateDto.getUserId());
		directorConfig.setUserPassword(updateDto.getUserPassword());
		
		
		setBoshConfigFile(directorConfig);
		directorConfig.setUpdateUserId("admin");

		return dao.updateDirector(directorConfig);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 삭제
	 * @title               : deleteDirector
	 * @return            : void
	***************************************************/
	@SuppressWarnings("unchecked")
	@Rollback(true)
	public void deleteDirector(int seq) {
		DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);
		if (directorConfig == null) {
			throw new CommonException("notfound.director.exception",
					"해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		InputStream input = null;
		FileWriter fileWriter = null;
		try {
			String directorLink = "https://" + directorConfig.getDirectorUrl() + ":" + directorConfig.getDirectorPort();
			
			input = new FileInputStream(new File(getBoshConfigLocation()));
			Yaml yaml = new Yaml();
			Map<String, Object> object = (Map<String, Object>)yaml.load(input);

			Map<String, Object> authMap = (Map<String,Object>)object.get("auth");
			authMap.put(directorLink, null);
			fileWriter = new FileWriter(getBoshConfigLocation());
			StringWriter stringWriter = new StringWriter();
			yaml.dump(object, stringWriter);
			fileWriter.write(stringWriter.toString());
			fileWriter.close();
			
			//설치관리자 삭제를 수행한다.
			dao.deleteDirecotr(seq);
			
		}catch(RuntimeException e){
			throw new CommonException("notfound.director.exception",
					"설치관리자 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}catch (Exception e) {
			throw new CommonException("notfound.director.exception",
					"설치관리자 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}finally {
			try {
				if(input != null)
					input.close();
				if(fileWriter != null)
					fileWriter.close();
				
			} catch (IOException e) {
				throw new CommonException("ioFileRead.director.exception",
						"읽어오는중 오류가 발생했습니다!", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : setDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	@Rollback(true)
	public DirectorConfigVO setDefaultDirector(int seq) {
			//1. 설치관리자가 존재하는지 확인한다.
			DirectorConfigVO directorConfig = dao.selectDirectorConfigBySeq(seq);
			if (directorConfig == null) {
				throw new CommonException("notfound.director.exception",
						"해당하는 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
			}
			
			//2.	설치관리자 정보를 확인한다
			DirectorInfoDTO info = getDirectorInfo();
			if ( info == null || StringUtils.isEmpty(info.getUser()) ) {
				throw new CommonException("badRequest.director.exception",
						"해당 디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
			}
			
			directorConfig.setDefaultYn("Y");
			directorConfig.setDirectorName(info.getName());
			directorConfig.setDirectorUuid(info.getUuid());
			directorConfig.setDirectorCpi(info.getCpi());
			directorConfig.setDirectorVersion(info.getVersion());
			directorConfig.setUpdateUserId("admin");
			
			setBoshConfigFile(directorConfig);
			dao.updateDirector(directorConfig);
			
			return directorConfig;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Http에서 읽어온 정보 설정
	 * @title               : getDirectorInfo
	 * @return            : DirectorInfoDTO
	***************************************************/
	public DirectorInfoDTO getDirectorInfo() {
		DirectorInfoDTO dto = new DirectorInfoDTO();
		dto.setCpi("openstack-cpi");
		dto.setName("my-bosh");
		dto.setUser("admin");
		dto.setUuid("bb46aab-a571-4f98-9d7a-61c027a4");
		dto.setVersion("2222v");
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  bosh_config 파일이 있는지 여부에 따라 설치 관리자 타겟을 설정하거나 파일을 생성
	 * @title               : setBoshConfigFile
	 * @return            : void
	***************************************************/
	@SuppressWarnings("unchecked")
	public void setBoshConfigFile(DirectorConfigVO directorConfig) {
		
		String directorLink = "https://" + directorConfig.getDirectorUrl() + ":" + directorConfig.getDirectorPort();
		
		//bosh_config 파일이 있는지 여부
		if ( isExistBoshConfigFile() ) {
			InputStream input = null;
			FileWriter fileWriter = null;
			try {
				input = new FileInputStream(new File(getBoshConfigLocation()));
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
				 
				//1. bosh_config 파일을 출력하기 위한  FileWriter 객체 생성
				fileWriter = new FileWriter(getBoshConfigLocation());
				//2. StringWriter 객체 생성
				StringWriter stringWriter = new StringWriter();
				//3. 
				yaml.dump(object, stringWriter);
				fileWriter.write(stringWriter.toString());
				fileWriter.close();
				
			} catch (IOException e) {
				throw new CommonException("notfound.director.exception",
						"설치관리자 타겟 설정 중 오류 발생하였습니다.", HttpStatus.NOT_FOUND);
			}finally {
				try {
					if(input != null)
						input.close();
					if(fileWriter != null)
						fileWriter.close();
					
				} catch (IOException e) {
					throw new CommonException("ioFileRead.director.exception",
							"읽어오는중 오류가 발생했습니다!", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}else {
			FileWriter fileWriter = null;
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
				
				fileWriter = new FileWriter(getBoshConfigLocation());
				StringWriter stringWriter = new StringWriter();
				yaml.dump(newConfig, stringWriter);
				fileWriter.write(stringWriter.toString());
				fileWriter.close();
			} catch (IOException e) {
				throw new CommonException("notfound.director.exception",
						"설치관리자 설정 파일 생성 중 오류 발생하였습니다.", HttpStatus.NOT_FOUND);
			} finally {
				if(fileWriter!=null){
					try {
						fileWriter.close();
					} catch (IOException e) {
						throw new CommonException("ioFileRead.director.exception",
								"읽어오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
					}
				}
			}
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : bosh_config 파일 설정
	 * @title               : getBoshConfigLocation
	 * @return            : String
	***************************************************/
	public String getBoshConfigLocation() {
		String homeDir = System.getProperty("user.home"); //User's home directory
		String fileSeperator = System.getProperty("file.separator");//File separator ("/" on UNIX)
		String boshConfigFile = homeDir + fileSeperator + ".bosh_config_test"; //
		return boshConfigFile;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 정보 조회
	 * @title               : getDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO getDefaultDirector() {
		
		//기본 설치 관리자 존재 여부 조회 -> 기본 설치 관리자 조회
		DirectorConfigVO directorConfig = dao.selectDirectorConfigByDefaultYn("Y");
		
		if ( directorConfig == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}

		return directorConfig;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : bosh_config 파일 존재 여부
	 * @title               : isExistBoshConfigFile
	 * @return            : boolean
	***************************************************/
	public boolean isExistBoshConfigFile() {
		File file = new File(getBoshConfigLocation());
		return file.exists();
	}

}
