package org.openpaas.ieda.web.config.setting;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.api.DirectorClient;
import org.openpaas.ieda.api.DirectorClientBuilder;
import org.openpaas.ieda.api.DirectorEndPoint;
import org.openpaas.ieda.api.Info;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.common.IEDAConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */

@Service
@Transactional
@Slf4j
public class IEDADirectorConfigService {
	
	private DirectorEndPoint directorEndPoint = DirectorEndPoint.getInstance();

	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private IEDAConfiguration iedaConfiguration;
	
	public IEDADirectorConfig getDefaultDirector() {
		
		log.info("getDeploymentDir : " + iedaConfiguration.getDeploymentDir());
		log.info("getReleaseDir    : " + iedaConfiguration.getReleaseDir());
		log.info("getStemcellDir   : " + iedaConfiguration.getStemcellDir());
		
		IEDADirectorConfig directorConfig = directorConfigRepository.findOneByDefaultYn("Y");

		if ( directorConfig == null ) {
			throw new IEDACommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}

		return directorConfig;
	}
	
	public List<IEDADirectorConfig> listDirector() {
		
		List<IEDADirectorConfig> directorConfigList = directorConfigRepository.findAll();
		if ( directorConfigList.size() == 0 ) {
			throw new IEDACommonException("nocontent.director.exception",
					"디렉터 정보가 존재하지 않습니다.", HttpStatus.NO_CONTENT);
		}
		
		return directorConfigList;
	}

	public IEDADirectorConfig createDirector(
			IEDADirectorConfigDto.Create createDto) {

		// 추가할 디렉터가 이미 존재하는지 여부 확인
		List<IEDADirectorConfig> directorConfigList = directorConfigRepository
				.findByDirectorUrl(createDto.getDirectorUrl());

		if (directorConfigList.size() > 0) {
			throw new IEDACommonException("duplicated.director.exception", "["
					+ createDto.getDirectorUrl() + "] 데이터가 이미 존재합니다.", HttpStatus.BAD_REQUEST);
		}

		Info info = null;

		try {
			DirectorClient client = new DirectorClientBuilder()
					.withHost(createDto.getDirectorUrl(), createDto.getDirectorPort())
					.withCredentials(createDto.getUserId(), createDto.getUserPassword()).build();

			URI infoUri = UriComponentsBuilder.fromUri(client.getRoot())
					.pathSegment("info").build().toUri();
			ResponseEntity<Info> response = client.getRestTemplate()
					.getForEntity(infoUri, Info.class);

			info = response.getBody();

			log.info(info.toString());

		} catch (ResourceAccessException e) {
			throw new IEDACommonException("notfound.director.exception", "["
					+ createDto.getDirectorUrl() + "] 디렉터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("notfound.director.exception",
					"요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}

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

		director.setUpdatedDate(now);
		director.setCreatedDate(now);

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

	public IEDADirectorConfig updateDirectorConfig(int seq,
			IEDADirectorConfigDto.Update updateDto) {

		IEDADirectorConfig directorConfig = directorConfigRepository
				.findByIedaDirectorConfigSeq(seq);

		if (directorConfig == null) {
			throw new IEDACommonException("illigalArgument.director.exception",
					"해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}

		Info info = null;

		try {
			DirectorClient client = new DirectorClientBuilder()
					.withHost(directorConfig.getDirectorUrl(),
							directorConfig.getDirectorPort())
					.withCredentials(updateDto.getUserId(),
							updateDto.getUserPassword()).build();

			URI infoUri = UriComponentsBuilder.fromUri(client.getRoot())
					.pathSegment("info").build().toUri();
			ResponseEntity<Info> response = client.getRestTemplate()
					.getForEntity(infoUri, Info.class);

			info = response.getBody();

			log.info(info.toString());

		} catch (ResourceAccessException e) {
			throw new IEDACommonException("notfound.director.exception", "["
					+ directorConfig.getDirectorUrl() + "] 디렉터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("unknown.director.exception",
					"수정 중 알수없는 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}

		directorConfig.setUserId(updateDto.getUserId());
		directorConfig.setUserPassword(updateDto.getUserPassword());

		return directorConfigRepository.save(directorConfig);
	}

	public void deleteDirectorConfig(int seq) {
		IEDADirectorConfig directorConfig = directorConfigRepository
				.findByIedaDirectorConfigSeq(seq);

		if (directorConfig == null) {
			throw new IEDACommonException("illigalArgument.director.exception",
					"해당하는 디렉터가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}

//		directorConfigRepository.delete(seq);
	}

}
