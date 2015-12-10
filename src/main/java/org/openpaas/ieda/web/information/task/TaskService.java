package org.openpaas.ieda.web.information.task;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.Task;
import org.openpaas.ieda.api.director.DirectorRestHelper;
import org.openpaas.ieda.common.IEDACommonException;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TaskService {
	@Autowired
	private IEDADirectorConfigService directroConfigService;

	public List<Task> listTask() {
		IEDADirectorConfig defaultDirector = directroConfigService.getDefaultDirector();

		Task[] tasks = null;
		
		try {
			HttpClient client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getTaskListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);

			client.executeMethod(get);
			ObjectMapper mapper = new ObjectMapper();
			tasks = mapper.readValue(get.getResponseBodyAsString(), Task[].class);

		} catch (ResourceAccessException e) {
			e.printStackTrace();
			throw new IEDACommonException("notfound.tasks.exception", " Task정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IEDACommonException("notfound.tasks.exception", "요청정보가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		return Arrays.asList(tasks);
	}
}
