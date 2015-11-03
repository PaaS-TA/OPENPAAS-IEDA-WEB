package org.openpaas.ieda.web.information.task;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.openpaas.ieda.api.DirectorClient;
import org.openpaas.ieda.api.DirectorClientBuilder;
import org.openpaas.ieda.api.Task;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IEDATaskConfigService  {
	
	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;

//	@Autowired
//	private RestTemplate restTemplate;

	public List<Task> listTask(){
		// get default director
		// 추가할 디렉터가 이미 존재하는지 여부 확인
		
		IEDADirectorConfig defaultDirector = directorConfigRepository.findOneByDefaultYn("Y");
		
		Task[] tasks = null;
		try {
			
			DirectorClient client = new DirectorClientBuilder()
					.withHost(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort())
					.withCredentials(defaultDirector.getUserId(), defaultDirector.getUserPassword()).build();
			
			URI tasksUri = UriComponentsBuilder.fromUri(client.getRoot())
					.pathSegment("tasks").build().toUri();
			
			tasks = client.getRestTemplate().getForObject(tasksUri, Task[].class);
			
			for (Task task : tasks) {
				log.info("id : " + task.getId() + ", desc : " + task.getDescription());
			}

		} catch (ResourceAccessException e) {
			e.printStackTrace();
			log.info("getMessage : " + e.getMessage());
			log.info("getLocalizedMessage : " + e.getLocalizedMessage());
			throw new IEDACommonException("notfound.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("notfound.tasks.exception",
					"요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		return Arrays.asList(tasks);
	}
	
}
