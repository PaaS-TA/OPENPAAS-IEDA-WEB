package org.openpaas.ieda.web.information.task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskService {
	
	@Autowired
	private IEDADirectorConfigService directorConfigService;

	public List<Task> listTask() {
		IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();

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

	public void getDownloadDebugLogFile(String taskId, HttpServletRequest request, HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();
		String content = "";
		String fileName = "";
		int statusCode = 0;
		
		try {
			IEDADirectorConfig defaultDirector = directorConfigService.getDefaultDirector();
			
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			
			// Task Output 조회
			GetMethod getTaskOutputMethod = new GetMethod(DirectorRestHelper.getTaskOutputURI(
					defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, "debug"));
			getTaskOutputMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
					defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutputMethod);
			String range = "bytes=" + 0 + "-";
			getTaskOutputMethod.setRequestHeader("Range", range);
			statusCode = httpClient.executeMethod(getTaskOutputMethod);
			
			content = getTaskOutputMethod.getResponseBodyAsString().replace("\n", "\r\n");
			
			Date now = new Date();
			SimpleDateFormat dataformat = new SimpleDateFormat("yyyymmdd_HHmmss");
			fileName = dataformat.format(now)+"_task_debug_"+ taskId + ".log";
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			IOUtils.write(content, response.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
