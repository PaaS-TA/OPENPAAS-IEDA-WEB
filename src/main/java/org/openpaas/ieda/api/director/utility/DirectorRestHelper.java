package org.openpaas.ieda.api.director.utility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.tomcat.util.codec.binary.Base64;
import org.openpaas.ieda.api.director.dto.ResponseTaskOuput;
import org.openpaas.ieda.api.task.TaskInfoDTO;
import org.openpaas.ieda.api.task.TaskOutputDTO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DirectorRestHelper {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DirectorRestHelper.class);
	final private static int THREAD_SLEEP_TIME = 2 * 1000;
	final private static String HTTPS = "https";
	final private static String CANCELLED = "cancelled";
	final private static String STARTED = "started";
	final private static String ERROR = "error";  
	final private static String DONE = "done";
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 프로토콜을 등록하고 HTTP 클라이언트 객체를 응답
	 * @title               : getHttpClient
	 * @return            : HttpClient
	***************************************************/
	public static HttpClient getHttpClient(int port) {
		//프로토컬 등록
		Protocol.registerProtocol(HTTPS, new Protocol(HTTPS, new ExSSLSocketFactory(), port));
		return new HttpClient();
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 계정과 비밀번호를 인코딩하여 Header를 정의하고 응답
	 * @title               : setAuthorization
	 * @return            : HttpMethodBase
	***************************************************/
	public static HttpMethodBase setAuthorization(String userId, String password, HttpMethodBase methodBase) {
		//HttpMethodBase abstract base implementation of HttpMethod.
		String auth = userId + ":" + password;
		/* base64 encoding */
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
		String authHeader = "Basic " + new String(encodedAuth); //encoding text

		methodBase.setRequestHeader("Authorization", authHeader);
		return methodBase;
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 URI 생성
	 * @title               : getInfoURI
	 * @return            : String
	***************************************************/
	public static String getInfoURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("info").build().toUri()
				.toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 정보 목록 조회  URI 생성
	 * @title               : getStemcellsURI
	 * @return            : String
	***************************************************/
	public static String getStemcellsURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("stemcells").build()
				.toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 업로드 URI 생성
	 * @title               : getUploadStemcellURI
	 * @return            : String
	***************************************************/
	public static String getUploadStemcellURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("stemcells").build()
				.toUri().toString();
	}

	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 삭제URI 생성
	 * @title               : getDeleteStemcellURI
	 * @return            : String
	***************************************************/
	public static String getDeleteStemcellURI(String host, int port, String stemcellName, String stemcellVersion) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port)
				.path("/stemcells/{name}/{version}").queryParam("force", "true").build()
				.expand(stemcellName, stemcellVersion).toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 URI 생성
	 * @title               : getReleaseListURI
	 * @return            : String
	***************************************************/
	public static String getReleaseListURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("releases").build().toUri()
				.toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 업로드 URI 생성
	 * @title               : getUploadReleaseURI
	 * @return            : String
	***************************************************/
	public static String getUploadReleaseURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("releases").build().toUri()
				.toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 삭제 릴리즈 URI 생성
	 * @title               : getDeleteReleaseURI
	 * @return            : String
	***************************************************/
	public static String getDeleteReleaseURI(String host, int port, String releaseName, String releaseVersion) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("/releases/{name}")
				.queryParam("force", "true").queryParam("version", releaseVersion).build().expand(releaseName).toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼 설치 URI 생성
	 * @title               : getDeployURI
	 * @return            : String
	***************************************************/
	public static String getDeployURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments")
				.build().toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포삭제 URI 생성
	 * @title               : getDeleteDeploymentURI
	 * @return            : String
	***************************************************/
	public static String getDeleteDeploymentURI(String host, int port, String deploymentName) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{name}")
				.queryParam("force", "true").build().expand(deploymentName).toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포정보 URI 생성
	 * @title               : getDeploymentListURI
	 * @return            : String
	***************************************************/
	public static String getDeploymentListURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments").build()
				.toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Task 정보 URI 생성
	 * @title               : getTaskListURI
	 * @return            : String
	***************************************************/
	public static String getTaskListURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("tasks").build()
				.toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Task Id 추출
	 * @title               : getTaskId
	 * @return            : String
	***************************************************/
	public static String getTaskId(String taskUrl) {
		String taskId = "";
		try {
			URL url = new URL(taskUrl);
			String[] segments = url.getPath().split("/");
			taskId = segments[segments.length - 1];

		} catch (MalformedURLException e) {
			if( LOGGER.isErrorEnabled() ){
				LOGGER.error(e.getMessage());
			}
		}
		return taskId;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : taskId에 따른 Task상태 URI 생성
	 * @title               : getTaskStatusURI
	 * @return            : String
	***************************************************/
	public static String getTaskStatusURI(String host, int port, String taskId) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("tasks/{id}")
				.queryParam("type", "event").build().expand(taskId).toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : taskId에 따른 디버그 로그 URI 생성
	 * @title               : getTaskOutputURI
	 * @return            : String
	***************************************************/
	public static String getTaskOutputURI(String host, int port, String taskId, String type) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("tasks/{id}/output")
				.queryParam("type", type).build().expand(taskId).toUri().toString();
	}
	
	/***************************************************
	 * @project 			: Paas 플랫폼 설치 자동화
	 * @description 	: 해당 배포명의 VM List 정보 URI 생성
	 * @title 				: getVmURI
	 * @return 			: String
	***************************************************/
	public static String getVmListURI(String host, int port, String deploymentName) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{name}/vms")
				.queryParam("format", "full").build().expand(deploymentName).toUri().toString();
	}
	
	/***************************************************
	 * @project 			: Paas 플랫폼 설치 자동화
	 * @description 	: Agent 및 Job 로그 생성
	 * @title 				: createLogURI
	 * @return 			: String
	***************************************************/
	public static String createLogURI(String host, int port, String deploymentName, String jobName, String index, String type) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/jobs/{job_name}/{index}/logs")
				.queryParam("type", type).build().expand(deploymentName, jobName, index).toUri().toString();
	}
	
	/***************************************************
	 * @project 			: Paas 플랫폼 설치 자동화
	 * @description 	: result 값을 이용하여 로그 다운로드
	 * @title 				: getResultOutputURI
	 * @return 			: String
	***************************************************/
	public static String getResultOutputURI(String host, int port, String result) {
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("resources/{result}")
				.build().expand(result).toUri().toString();
	}
	
	/***************************************************
	 * @project 			: Paas 플랫폼 설치 자동화
	 * @description 	: 해당 배포명의 Manifest 정보 조회
	 * @title 				: getManifestURI
	 * @return 			: String
	***************************************************/
	public static String getManifestURI(String host, int port, String deployment_nm ){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}")
				.build().expand(deployment_nm).toUri().toString();
	}

	
	/***************************************************
	 * @project 			: Paas 플랫폼 설치 자동화
	 * @description 	: Job 상태 관리 URI
	 * @title 				: getJobStateURI
	 * @return 			: String
	***************************************************/
	public static String getJobStateURI(String host, int port, String deployment_name, String job, String index, String state ){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/jobs/{job}/{index_or_id}")
				.queryParam("state", state).build().expand(deployment_name, job, index).toUri().toString();
	}

	/***************************************************
	 * @project 			: Paas 플랫폼 설치 자동화
	 * @description 	: 스냅샷 생성 URI 
	 * @title 				: getTakeSnapshotURI
	 * @return 			: String
	***************************************************/
	public static String getTakeSnapshotURI(String host, int port, String deployment_name, String job, String index ){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/jobs/{job}/{index_or_id}/snapshots")
				.build().expand(deployment_name, job, index).toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 배포 명의 Property 목록 정보 조회
	 * @title               : getPropertyListURI
	 * @return            : String
	***************************************************/
	public static String getPropertyListURI(String host, int port,String deploymentName){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/properties")
				.build().expand(deploymentName).toUri().toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 배포 명의 Property 상세 정보 조회
	 * @title               : getPropertyDetailInfoURI
	 * @return            : String
	***************************************************/
	public static String getPropertyDetailInfoURI(String host, int port, String deploymentName, String propertyName){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/properties/{property_name}")
				.build().expand(deploymentName, propertyName).toUri().toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 배포명의 Property 생성 URI 생성
	 * @title               : createPropertURI
	 * @return            : String
	***************************************************/
	public static String createPropertURI(String host, int port, String deploymentName){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/properties")
				.build().expand(deploymentName).toUri().toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 배포명의 Property 수정 URI 생성
	 * @title               : updatePropertURI
	 * @return            : String
	***************************************************/
	public static String updatePropertURI(String host, int port, String deploymentName, String propertyName){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/properties/{property_name}")
				.build().expand(deploymentName, propertyName).toUri().toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 배포명의 Property 삭제 URI 생성
	 * @title               : deletePropertURI
	 * @return            : String
	***************************************************/
	public static String deletePropertURI(String host, int port, String deploymentName, String propertyName){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/properties/{property_name}")
				.build().expand(deploymentName, propertyName).toUri().toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 정보 목록 조회 URI 생성
	 * @title               : getSnapshotListURI
	 * @return            : String
	***************************************************/
	public static String getSnapshotListURI(String host, int port, String deploymentName){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/snapshots")
				.build().expand(deploymentName).toUri().toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 전체 삭제
	 * @title               : deleteAllSnapshotURI
	 * @return            : String
	***************************************************/
	public static String deleteAllSnapshotURI(String host, int port, String deploymentName){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/snapshots")
				.build().expand(deploymentName).toUri().toString();
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 부분 삭제
	 * @title               : deleteSnapshotURI
	 * @return            : String
	***************************************************/
	public static String deleteSnapshotURI(String host, int port, String deploymentName, String snapshotCid){
		return UriComponentsBuilder.newInstance().scheme(HTTPS).host(host).port(port).path("deployments/{deployment_name}/snapshots/{snapshot_cid}")
				.build().expand(deploymentName, snapshotCid).toUri().toString();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 로그를 subscribe에 전달하고 최종 설치상태 응답
	 * @title               : trackToTask
	 * @return            : String
	***************************************************/
	public static String trackToTask(DirectorConfigVO defaultDirector, SimpMessagingTemplate messageTemplate,
		String messageEndpoint, HttpClient client, String taskId, String logType, String userId) {
		
		String status = "";
		String eventLog = "";
		try {
			sendTaskOutput(userId, messageTemplate, messageEndpoint, STARTED, Arrays.asList("Director task " + taskId));

			ObjectMapper mapper = new ObjectMapper();

			String lastStage = null;
			int offset = 0;
			while (true) {
				
				//1. Task 상태 정보 요청
				GetMethod getTaskStaus = new GetMethod(DirectorRestHelper
						.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
				getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
				
				int statusCode = client.executeMethod(getTaskStaus);
				
				if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
					sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
							Arrays.asList("Task " + taskId + " : 상태 조회 중 오류가 발생하였습니다."));
					break;
				}
				//응답 결과 task 상태 정보를 가져온다.
				TaskInfoDTO taskInfo = mapper.readValue(getTaskStaus.getResponseBodyAsString(), TaskInfoDTO.class);
				
				GetMethod getTaskOutput = new GetMethod(DirectorRestHelper.getTaskOutputURI(
						defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, logType));
				getTaskOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutput);
				String range = "bytes=" + offset + "-";
				getTaskOutput.setRequestHeader("Range", range);
				statusCode = client.executeMethod(getTaskOutput);

				if (statusCode == HttpStatus.NO_CONTENT.value()) {
					Thread.sleep(THREAD_SLEEP_TIME);
					continue;
				}

				if (statusCode == HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()) {
					if (taskInfo.getState().equalsIgnoreCase("done")) {
						sendTaskOutput(userId, messageTemplate, messageEndpoint, DONE, Arrays.asList("", "Task " + taskId + " done"));
						status = DONE;
						break;
					}else if (taskInfo.getState().equalsIgnoreCase("error")) {
						sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR, Arrays.asList("", "An error occurred while executing the task " + taskId));
						status = ERROR;
						break;
					} else if (taskInfo.getState().equalsIgnoreCase("cancelled")) {
						sendTaskOutput(userId, messageTemplate, messageEndpoint, CANCELLED, Arrays.asList("", "Canceled Task " + taskId));
						status = CANCELLED;
						break;
					} else{
						Thread.sleep(THREAD_SLEEP_TIME);
						continue;
					}
				}

				if (statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()) {
					eventLog=DONE;
					Header contentRange = getTaskOutput.getResponseHeader("Content-Range");
					if (contentRange == null) {
						Thread.sleep(THREAD_SLEEP_TIME);
						continue;
					}
					
					if ( !StringUtils.isEmpty(contentRange) ) {
						String[] splited = contentRange.getValue().split("/");
						offset = Integer.parseInt(splited[1]);
					}

					if ( "debug".equals(logType) ) {
						String[] outputs = getTaskOutput.getResponseBodyAsString().split("\n");
						
						for ( String output : outputs ) {
							Thread.sleep(10);
							sendTaskOutput(userId, messageTemplate, messageEndpoint, STARTED, Arrays.asList(output));
						}
					} else {
						
						String outputs1 = getTaskOutput.getResponseBodyAsString();
						String outputs2 = outputs1.substring(0, outputs1.length() - 1).replace("\n", ",");
						String outputs = "[" + outputs2 + "]";
						
						List<TaskOutputDTO> taskOutputList = mapper.readValue(outputs, new TypeReference<List<TaskOutputDTO>>() {
						});
	
						List<String> responseMessage = new ArrayList<String>();
						for (TaskOutputDTO output : taskOutputList) {
	
							if (output.getStage() != null && (lastStage == null || !lastStage.equals(output.getStage()))) {
									responseMessage.add("");
									responseMessage.add("  Started    " + output.getStage());
							}
	
							if (output.getStage() != null) {
	
								if (output.getState().equals("started")) {
									responseMessage.add("  Started    " + output.getStage() + " > " + output.getTask());
								} else if (output.getState().equals("finished")) {
									responseMessage.add("  Done       " + output.getStage() + " > " + output.getTask());
								} else if (output.getState().equals("failed")) {
									responseMessage.add("  Failed      " + output.getStage() + " > " + output.getTask());
								} else {
									responseMessage.add("  Processing " + output.getStage() + " > " + output.getTask() + " " + output.getProgress() + "%");
								}
							} else {
								HashMap<String, String> error = output.getError();
								if (error != null) {
									responseMessage.add( "  Error Code : " + error.get("code") + ", Message :" + error.get("message"));
								}
							}
							lastStage = output.getStage();
						}
						Thread.sleep(THREAD_SLEEP_TIME);
						sendTaskOutput(userId, messageTemplate, messageEndpoint, STARTED, responseMessage);
					}
				}
				
				if(messageEndpoint.equals("/info/task/list/eventLog/socket")){
					if(eventLog.equals("done")){
						sendTaskOutput(userId, messageTemplate, messageEndpoint, eventLog, Arrays.asList("", "Task " + taskId));
						break;
					}else{
						sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR, Arrays.asList("", "Task " + taskId));
						break;
					}
				}

				if (taskInfo.getState().equalsIgnoreCase("done")) {
					sendTaskOutput(userId, messageTemplate, messageEndpoint, DONE, Arrays.asList("", "Task " + taskId + " done"));
					status = DONE;
					break;
				} 
				else if (taskInfo.getState().equalsIgnoreCase("error")) {
					sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR, Arrays.asList("", "An error occurred while executing the task " + taskId));
					status = ERROR;					
				} 
				else if (taskInfo.getState().equalsIgnoreCase("cancelled")) {
					sendTaskOutput(userId, messageTemplate, messageEndpoint, DONE, Arrays.asList("", "Task " + taskId + ""));
					status = DONE;
				}

				Thread.sleep(THREAD_SLEEP_TIME);
			}
		}catch (IOException e) {
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}catch (InterruptedException e){
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}catch (RuntimeException e) {
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}catch(Exception e){
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}
		
		return status;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Task 정보
	 * @title               : trackToTaskLineOne
	 * @return            : String
	***************************************************/
	public static String trackToTaskLineOne(DirectorConfigVO defaultDirector, SimpMessagingTemplate messageTemplate,
		String messageEndpoint, HttpClient client, String taskId, String logType, String userId) {
		String status = "";

		try {
			sendTaskOutput(userId, messageTemplate, messageEndpoint, STARTED, Arrays.asList("Director task " + taskId));

			ObjectMapper mapper = new ObjectMapper();

			int offset = 0;
			while (true) {
				
				//1. Task 상태 정보 요청
				GetMethod getTaskStaus = new GetMethod(DirectorRestHelper
						.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
				getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
				
				int statusCode = client.executeMethod(getTaskStaus);
				
				if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
					sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
							Arrays.asList("Task " + taskId + " : 상태 조회 중 오류가 발생하였습니다."));
					break;
				}
				
				TaskInfoDTO taskInfo = mapper.readValue(getTaskStaus.getResponseBodyAsString(), TaskInfoDTO.class);
				GetMethod getTaskOutput = new GetMethod(DirectorRestHelper.getTaskOutputURI(
						defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, logType));
				getTaskOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutput);
				String range = "bytes=" + offset + "-";
				getTaskOutput.setRequestHeader("Range", range);
				statusCode = client.executeMethod(getTaskOutput);

				if ( statusCode == HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()) {
					if (taskInfo.getState().equalsIgnoreCase("done")) {
						sendTaskOutput(userId, messageTemplate, messageEndpoint, DONE, Arrays.asList("", "Task " + taskId + " done"));
						status = DONE;
						break;
					}else if (taskInfo.getState().equalsIgnoreCase("error")) {
						sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR, Arrays.asList("", "An error occurred while executing the task " + taskId));
						status = ERROR;
						break;
					} else if (taskInfo.getState().equalsIgnoreCase("cancelled")) {
						sendTaskOutput(userId, messageTemplate, messageEndpoint, CANCELLED, Arrays.asList("", "Canceled Task " + taskId));
						status = CANCELLED;
						break;
					}
					Thread.sleep(THREAD_SLEEP_TIME);
				}
			}
		}catch (IOException e) {
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}catch (InterruptedException e){
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}catch (RuntimeException e) {
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}catch(Exception e){
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}
		
		return status;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 파일 업로드 로그 및 최종 설치 상태 응답
	 * @title               : trackToTaskWithTag
	 * @return            : String
	***************************************************/
	public static String trackToTaskWithTag(DirectorConfigVO defaultDirector, SimpMessagingTemplate messageTemplate, 
			String messageEndpoint, String tag, HttpClient client, String taskId, String logType, String userId) {
		
		String status = "";

		try {
			sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, STARTED, tag, Arrays.asList("Director task " + taskId));

			ObjectMapper mapper = new ObjectMapper();

			String lastStage = null;
			int offset = 0;
			while (true) {
				GetMethod getTaskStaus = new GetMethod(DirectorRestHelper
						.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
				getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
				int statusCode = client.executeMethod(getTaskStaus);
				
				if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
					sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, ERROR, tag, 
							Arrays.asList("Task " + taskId + " : 상태 조회 중 오류가 발생하였습니다."));
					break;
				}

				TaskInfoDTO taskInfo = mapper.readValue(getTaskStaus.getResponseBodyAsString(), TaskInfoDTO.class);

				GetMethod getTaskOutput = new GetMethod(DirectorRestHelper.getTaskOutputURI(
						defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, logType));
				getTaskOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutput);
				String range = "bytes=" + offset + "-";
				getTaskOutput.setRequestHeader("Range", range);
				statusCode = client.executeMethod(getTaskOutput);

				if (statusCode == HttpStatus.NO_CONTENT.value()) {
					Thread.sleep(THREAD_SLEEP_TIME);
					continue;
				}

				if (statusCode == HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()) {
					Thread.sleep(THREAD_SLEEP_TIME);
					continue;
				}

				if (statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()) {

					Header contentRange = getTaskOutput.getResponseHeader("Content-Range");
					if (contentRange == null) {
						Thread.sleep(THREAD_SLEEP_TIME);
						continue;
					}
					if ( !StringUtils.isEmpty(contentRange) ) {
						String[] splited = contentRange.getValue().split("/");
						offset = Integer.parseInt(splited[1]);
					}

					if ( "debug".equals(logType) ) {
						String[] outputs = getTaskOutput.getResponseBodyAsString().split("\n");
						
						for ( String output : outputs ) {
							Thread.sleep(10);
							sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, STARTED, tag, Arrays.asList(output));
						}
					} else {
						
						String outputs1 = getTaskOutput.getResponseBodyAsString();
						String outputs2 = outputs1.substring(0, outputs1.length() - 1).replace("\n", ",");
						String outputs = "[" + outputs2 + "]";
	
						List<TaskOutputDTO> taskOutputList = mapper.readValue(outputs, new TypeReference<List<TaskOutputDTO>>() {
						});
	
						List<String> responseMessage = new ArrayList<String>();
						for (TaskOutputDTO output : taskOutputList) {
	
							if (output.getStage() != null && ( lastStage == null || !lastStage.equals(output.getStage()) )) {
								responseMessage.add("");
								responseMessage.add("  Started    " + output.getStage());
							}
	
							if (output.getStage() != null) {
	
								if (output.getState().equals("started")) {
									responseMessage.add("  Started    " + output.getStage() + " > " + output.getTask());
								} else if (output.getState().equals("finished")) {
									responseMessage.add("  Done       " + output.getStage() + " > " + output.getTask());
								} else if (output.getState().equals("failed")) {
									responseMessage.add("  Failed      " + output.getStage() + " > " + output.getTask());
								} else {
									responseMessage.add("  Processing " + output.getStage() + " > " + output.getTask() + " " + output.getProgress() + "%");
								}
							}else {
								HashMap<String, String> error = output.getError();
								if (error != null) {
									responseMessage.add(
											"  Error Code : " + error.get("code") + ", Message :" + error.get("message"));
								}
							}
							lastStage = output.getStage();
						}
						//업로드 상태 및  결과 정보를 담아 subscribe에 보낸다.
						sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, STARTED, tag, responseMessage);
					}
				}

				if (taskInfo.getState().equalsIgnoreCase("done")) {
					sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, DONE, tag,
							Arrays.asList("", "Task " + taskId + " done"));
					status = DONE;
					break;
				}
				else if (taskInfo.getState().equalsIgnoreCase("error")) {
					sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, ERROR, tag, 
							Arrays.asList("", "An error occurred while executing the task " + taskId));
					status = ERROR;
					break;
				}
				else if (taskInfo.getState().equalsIgnoreCase("cancelled")) {
					sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, CANCELLED, tag, 
							Arrays.asList("", "Canceled Task " + taskId));
					status = CANCELLED;
					break;
				}

				Thread.sleep(THREAD_SLEEP_TIME);
			}
		}catch (RuntimeException e) {
			sendTaskOutput(userId, messageTemplate, messageEndpoint, ERROR,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}catch (Exception e) {
			sendTaskOutputWithTag(userId, messageTemplate, messageEndpoint, ERROR, tag,
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
			status = ERROR;
		}
		
		return status;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치 상태와 메시지를 설정하여 응답
	 * @title               : sendTaskOutput
	 * @return            : void
	***************************************************/
	public static void sendTaskOutput(String userId, SimpMessagingTemplate messageTemplate, String messageEndpoint, String status, List<String> messages) {
		ResponseTaskOuput response = new ResponseTaskOuput();
		response.setState(status);
		response.setMessages(messages);

		messageTemplate.convertAndSendToUser(userId, messageEndpoint, response);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 업로드 상태와 메시지를 설정하여 응답
	 * @title               : sendTaskOutputWithTag
	 * @return            : void
	***************************************************/
	public static void sendTaskOutputWithTag(String userId, SimpMessagingTemplate messageTemplate, String messageEndpoint, String status, String tag, List<String> messages) {
		ResponseTaskOuput response = new ResponseTaskOuput();
		response.setState(status);
		response.setTag(tag);
		response.setMessages(messages);

		messageTemplate.convertAndSendToUser(userId, messageEndpoint, response);
	}

	
}
