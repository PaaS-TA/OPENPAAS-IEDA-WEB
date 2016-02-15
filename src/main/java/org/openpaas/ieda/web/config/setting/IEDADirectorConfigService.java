package org.openpaas.ieda.web.config.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.Info;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class IEDADirectorConfigService {
	
	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;

	public IEDADirectorConfig getDefaultDirector() {
		
		IEDADirectorConfig directorConfig = directorConfigRepository.findOneByDefaultYn("Y");

		if ( directorConfig == null ) {
			throw new IEDACommonException("notfound.director.exception",
					"기본 설치관리자가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}

		return directorConfig;
	}
	
	public List<IEDADirectorConfig> listDirector() {
		
		List<IEDADirectorConfig> directorConfigList = directorConfigRepository.findAll();
		
		Comparator<IEDADirectorConfig> byDefaultYN = Collections.reverseOrder(Comparator.comparing(IEDADirectorConfig::getDefaultYn));
		
		if ( directorConfigList.size() == 0 ) {
			throw new IEDACommonException("nocontent.director.exception",
					"디렉터 정보가 존재하지 않습니다.", HttpStatus.NO_CONTENT);
		}
		
		return directorConfigList.stream().sorted(byDefaultYN).collect(Collectors.toList());
	}
	
	public Info getDirectorInfo(String directorUrl, int port, String userId, String password) {
		Info info = null;
		
		HttpClient client = DirectorRestHelper.getHttpClient(port);
		GetMethod get = new GetMethod(DirectorRestHelper.getInfoURI(directorUrl, port));
		get = (GetMethod)DirectorRestHelper.setAuthorization(userId, password, (HttpMethodBase)get);
		
		try {
			client.executeMethod(get);
			
			ObjectMapper mapper = new ObjectMapper();
			info = mapper.readValue(get.getResponseBodyAsString(), Info.class);
		}
		catch (ResourceAccessException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return info;
	}

	public IEDADirectorConfig createDirector(IEDADirectorConfigDto.Create createDto) {

		List<IEDADirectorConfig> directorConfigList = directorConfigRepository
				.findByDirectorUrl(createDto.getDirectorUrl());
		
		if ( directorConfigList.size() > 0 ) {
			throw new IEDACommonException("duplicated.director.exception",
					"이미 등록되어 있는 디렉터 URL입니다.", HttpStatus.BAD_REQUEST);
		}
		
		Info info = getDirectorInfo(createDto.getDirectorUrl()
								, createDto.getDirectorPort()
								, createDto.getUserId()
								, createDto.getUserPassword());
		
		
		if ( info == null || StringUtils.isEmpty(info.getUser())) {
			throw new IEDACommonException("unauthenticated.director.exception",
					"디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
		}
		log.debug("User Info ::: " + info.toString() + "\n isUser ::: " + ( info == null || info.getUser() == null || info.getUser().equals("") ) );
		
		Date now = new Date();

		IEDADirectorConfig director = new IEDADirectorConfig();

		director.setUserId(createDto.getUserId());
		director.setUserPassword(createDto.getUserPassword());
		director.setDirectorUrl(createDto.getDirectorUrl());
		director.setDirectorPort(createDto.getDirectorPort());
		director.setDirectorName(info.getName());
		director.setDirectorUuid(info.getUuid());
		director.setDirectorCpi(info.getCpi());
		director.setDirectorVersion(info.getVersion());
		
		IEDADirectorConfig directorConfig = directorConfigRepository.findOneByDefaultYn("Y");
		
		director.setDefaultYn((directorConfig == null ) ? "Y":"N");
		director.setUpdatedDate(now);
		director.setCreatedDate(now);
		
		if( director.getDefaultYn().equals("Y") ) {
			setBoshConfigFile(director);
		}	

		return directorConfigRepository.save(director);
	}
	
	public IEDADirectorConfig getDirectorConfig(int seq) {
		IEDADirectorConfig directorConfig = directorConfigRepository
				.findByIedaDirectorConfigSeq(seq);

		if (directorConfig == null) {
			throw new IEDACommonException("illigalArgument.director.exception",
					"해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		return directorConfig;
	}

	public IEDADirectorConfig updateDirectorConfig(IEDADirectorConfigDto.Update updateDto) {
		IEDADirectorConfig directorConfig = directorConfigRepository.findByIedaDirectorConfigSeq(updateDto.getIedaDirectorConfigSeq());
		if ( directorConfig == null )
			throw new IEDACommonException("notfound.director_update.exception",
					"디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		
		
		Info info = getDirectorInfo(directorConfig.getDirectorUrl()
				, directorConfig.getDirectorPort()
				, updateDto.getUserId()
				, updateDto.getUserPassword());
		
		if ( info == null || StringUtils.isEmpty(info.getUser()) )
			throw new IEDACommonException("unauthenticated.director.exception",
					"디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);

		directorConfig.setUserId(updateDto.getUserId());
		directorConfig.setUserPassword(updateDto.getUserPassword());
		directorConfig = directorConfigRepository.save(directorConfig);

		setBoshConfigFile(directorConfig);

		return directorConfig;
	}

	public void deleteDirectorConfig(int seq) {
		IEDADirectorConfig directorConfig = directorConfigRepository
				.findByIedaDirectorConfigSeq(seq);
		if (directorConfig == null) {
			throw new IEDACommonException("illigalArgument.director.exception",
					"해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		try {
			String directorLink = "https://" + directorConfig.getDirectorUrl() + ":" + directorConfig.getDirectorPort();
			
			InputStream input = new FileInputStream(new File(getBoshConfigLocation()));
			Yaml yaml = new Yaml();
			Map<String, Object> object = (Map<String, Object>)yaml.load(input);

			Map<String, Object> authMap = (Map<String,Object>)object.get("auth");
			authMap.put(directorLink, null);
			
			FileWriter fileWriter = new FileWriter(getBoshConfigLocation());
			StringWriter stringWriter = new StringWriter();
			yaml.dump(object, stringWriter);
			fileWriter.write(stringWriter.toString());
			fileWriter.close();
			
			directorConfigRepository.delete(seq);
			
		} catch (Exception e) {
			throw new IEDACommonException("illigalArgument.director.exception",
					"설치관리자 삭제 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
	}

	public IEDADirectorConfig setDefaultDirector(int seq) {
		IEDADirectorConfig directorConfig = directorConfigRepository
				.findByIedaDirectorConfigSeq(seq);
		if (directorConfig == null) {
			throw new IEDACommonException("illigalArgument.director.exception",
					"해당하는 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		Info info = getDirectorInfo(directorConfig.getDirectorUrl(), directorConfig.getDirectorPort(), directorConfig.getUserId(), directorConfig.getUserPassword());
		if ( info == null || StringUtils.isEmpty(info.getUser()) ) {
			throw new IEDACommonException("unauthenticated.director.exception",
					"해당 디렉터에 로그인 실패하였습니다.", HttpStatus.BAD_REQUEST);
		}
		
		IEDADirectorConfig oldDefaultDiretor = directorConfigRepository.findOneByDefaultYn("Y");
		if (oldDefaultDiretor != null) {
			oldDefaultDiretor.setDefaultYn("N");
			directorConfigRepository.save(oldDefaultDiretor);
		}
		
		directorConfig.setDefaultYn("Y");
		directorConfig.setDirectorName(info.getName());
		directorConfig.setDirectorUuid(info.getUuid());
		directorConfig.setDirectorCpi(info.getCpi());
		directorConfig.setDirectorVersion(info.getVersion());
		directorConfig= directorConfigRepository.save(directorConfig);
		
		setBoshConfigFile(directorConfig);
		
		return directorConfig;
	}

	public void setBoshConfigFile(IEDADirectorConfig directorConfig) {
		
		String directorLink = "https://" + directorConfig.getDirectorUrl() + ":" + directorConfig.getDirectorPort();

		if ( isExistBoshConfigFile() ) {
			try {
				InputStream input = new FileInputStream(new File(getBoshConfigLocation()));
				Yaml yaml = new Yaml();
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
				
				FileWriter fileWriter = new FileWriter(getBoshConfigLocation());
				StringWriter stringWriter = new StringWriter();
				yaml.dump(object, stringWriter);
				fileWriter.write(stringWriter.toString());
				fileWriter.close();
				
			} catch (IOException e) {
				throw new IEDACommonException("taretDirector.director.exception",
						"설치관리자 타겟 설정 중 오류 발생하였습니다.", HttpStatus.NOT_FOUND);
			}
		}
		else {
			try {
				Map<String, Object> newConfig = new HashMap<String, Object>();
				
				if ( directorConfig.getDefaultYn().equals("Y")) {
					newConfig.put("target", directorLink);
					newConfig.put("target_name", directorConfig.getDirectorName());
					newConfig.put("target_version", directorConfig.getDirectorVersion());
					newConfig.put("target_uuid", directorConfig.getDirectorUuid());
				}
				else {
					IEDADirectorConfig defaultDirectorConfig = getDefaultDirector();
					newConfig.put("target", "https://" + defaultDirectorConfig.getDirectorUrl() + ":" + defaultDirectorConfig.getDirectorPort());
					newConfig.put("target_name", defaultDirectorConfig.getDirectorName());
					newConfig.put("target_version", defaultDirectorConfig.getDirectorVersion());
					newConfig.put("target_uuid", defaultDirectorConfig.getDirectorUuid());
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
				
				FileWriter fileWriter = new FileWriter(getBoshConfigLocation());
				StringWriter stringWriter = new StringWriter();
				yaml.dump(newConfig, stringWriter);
				fileWriter.write(stringWriter.toString());
				fileWriter.close();
			} catch (IOException e) {
				throw new IEDACommonException("taretDirector.director.exception",
						"설치관리자 설정 파일 생성 중 오류 발생하였습니다.", HttpStatus.NOT_FOUND);
			}
		}
	}
	
	public String getBoshConfigLocation() {
		String homeDir = System.getProperty("user.home");
		String fileSeperator = System.getProperty("file.separator");
		String boshConfigFile = homeDir + fileSeperator + ".bosh_config";

		return boshConfigFile;
	}
	
	public boolean isExistBoshConfigFile() {
		File f = new File(getBoshConfigLocation());
		return f.exists();
	}
}
