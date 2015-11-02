package org.openpaas.ieda.web.information.task;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.openpaas.ieda.api.DirectorClient;
import org.openpaas.ieda.api.DirectorClientBuilder;
import org.openpaas.ieda.api.Task;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskConfigService  {
	
	@Autowired
	private IEDADirectorConfigRepository directorConfigRepository;


	public List<Task> listTask(){
		// get default director
		// 추가할 디렉터가 이미 존재하는지 여부 확인
		IEDADirectorConfig defaultDirector = directorConfigRepository
				.findOneByDefaultYn("Y");
		
		try {
			DirectorClient client = new DirectorClientBuilder()
					.withHost(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort())
					.withCredentials(defaultDirector.getUserId(), defaultDirector.getUserPassword()).build();
			System.out.println("########111111111111");
			URI tasksUri = UriComponentsBuilder.fromUri(client.getRoot())
					.pathSegment("tasks").build().toUri();
/*			ResponseEntity<ArrayList<Task>> response = client.getRestTemplate()
					.getForEntity(infoUri, (Class<? extends ArrayList<Task>)ArrayList.class);*/
			System.out.println("########22222222222222  : " + tasksUri);
			
			ParameterizedTypeReference<List<Task>> myBean = new ParameterizedTypeReference<List<Task>>() {};

			System.out.println("########33333333333333  : " + myBean);
			
//			ResponseEntity<List<Task>> rateResponse =
//			        client.getRestTemplate().exchange( tasksUri ,
//			                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Task>>() {});
			
			ResponseEntity<Task[]> rateResponse = client.getRestTemplate().getForEntity(tasksUri, Task[].class);
			
//			List<Task> tasksList = rateResponse.getBody();
			Task[] tasks = rateResponse.getBody();
			
			System.out.println(tasks.length);
			for (Task task : tasks) {
				log.info("id : " + task.getId() + ", desc : " + task.getDescription());
			}
			

		} catch (ResourceAccessException e) {
			e.printStackTrace();
			throw new IEDACommonException("notfound.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			throw new IEDACommonException("notfound.tasks.exception",
					"요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		return null;
	}
	
}
