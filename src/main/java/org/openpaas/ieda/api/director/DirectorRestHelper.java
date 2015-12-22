package org.openpaas.ieda.api.director;

import java.net.MalformedURLException;
import java.net.URL;
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
import org.openpaas.ieda.api.TaskInfo;
import org.openpaas.ieda.api.TaskOutput;
import org.openpaas.ieda.web.config.setting.IEDADirectorConfig;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectorRestHelper {
	
	final private static int THREAD_SLEEP_TIME = 2 * 1000;
	
	public static HttpClient getHttpClient(int port) {
		Protocol.registerProtocol("https", new Protocol("https", new ExSSLSocketFactory(), port));
		return new HttpClient();
	}

	public static HttpMethodBase setAuthorization(String userId, String password, HttpMethodBase methodBase) {
		String auth = userId + ":" + password;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
		String authHeader = "Basic " + new String(encodedAuth);

		methodBase.setRequestHeader("Authorization", authHeader);
		return methodBase;
	}

	// info
	public static String getInfoURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("info").build().toUri()
				.toString();
	}

	// stemcell
	public static String getStemcellsURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("stemcells").build()
				.toUri().toString();
	}

	public static String getUploadStemcellURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("stemcells").build()
				.toUri().toString();
	}

	public static String getDeleteStemcellURI(String host, int port, String stemcellName, String stemcellVersion) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port)
				.path("/stemcells/{name}/{version}").queryParam("force", "true").build()
				.expand(stemcellName, stemcellVersion).toUri().toString();
	}

	// release
	public static String getReleaseListURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("releases").build().toUri()
				.toString();
	}

	public static String getUploadReleaseURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("releases").build().toUri()
				.toString();
	}

	public static String getDeleteReleaseURI(String host, int port, String releaseName, String releaseVersion) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("/releases/{name}")
				.queryParam("force", "true").queryParam("version", releaseVersion).build().expand(releaseName).toUri().toString();
	}

	// deploy
	public static String getDeployURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("deployments")
				.queryParam("recreate", "true").queryParam("skip_drain", "true").build().toUri().toString();
	}

	public static String getDeleteDeploymentURI(String host, int port, String deploymentName) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("deployments/{name}")
				.queryParam("force", "true").build().expand(deploymentName).toUri().toString();
	}

	public static String getDeploymentListURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("deployments").build()
				.toString();
	}

	// task
	public static String getTaskListURI(String host, int port) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("tasks").build()
				.toString();
	}

	public static String getTaskId(String taskUrl) {
		String taskId = "";
		try {
			URL url = new URL(taskUrl);
			String[] segments = url.getPath().split("/");
			taskId = segments[segments.length - 1];

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return taskId;
	}

	public static String getTaskStatusURI(String host, int port, String taskId) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("tasks/{id}")
				.queryParam("type", "event").build().expand(taskId).toUri().toString();
	}

	public static String getTaskOutputURI(String host, int port, String taskId, String type) {
		return UriComponentsBuilder.newInstance().scheme("https").host(host).port(port).path("tasks/{id}/output")
				.queryParam("type", type).build().expand(taskId).toUri().toString();
	}

	public static void trackToTask(IEDADirectorConfig defaultDirector, SimpMessagingTemplate messageTemplate,
			String messageEndpoint, HttpClient client, String taskId) {

		try {
			sendTaskOutput(messageTemplate, messageEndpoint, "started", Arrays.asList("Director task " + taskId));

			ObjectMapper mapper = new ObjectMapper();

			String lastStage = null;
			int offset = 0;
			while (true) {
				// Task 상태 조회
				GetMethod getTaskStausMethod = new GetMethod(DirectorRestHelper
						.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
				getTaskStausMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStausMethod);
				int statusCode = client.executeMethod(getTaskStausMethod);
				System.out.println("#### status code : " + statusCode);
				if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
					System.out.println("Query Task Status is not ok(" + statusCode + ").");
					sendTaskOutput(messageTemplate, messageEndpoint, "error",
							Arrays.asList("Task " + taskId + " : 상태 조회 중 오류가 발생하였습니다."));
					break;
				}

				// Convert Json to TaskInfo Object
				TaskInfo taskInfo = mapper.readValue(getTaskStausMethod.getResponseBodyAsString(), TaskInfo.class);

				// Task Output 조회
				GetMethod getTaskOutputMethod = new GetMethod(DirectorRestHelper.getTaskOutputURI(
						defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId, "event"));
				getTaskOutputMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
						defaultDirector.getUserPassword(), (HttpMethodBase) getTaskOutputMethod);
				String range = "bytes=" + offset + "-";
				getTaskOutputMethod.setRequestHeader("Range", range);
				statusCode = client.executeMethod(getTaskOutputMethod);

				if (statusCode == HttpStatus.NO_CONTENT.value()) {
					Thread.sleep(THREAD_SLEEP_TIME);
					continue;
				}

				if (statusCode == HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()) {
					Thread.sleep(THREAD_SLEEP_TIME);
					continue;
				}

				if (statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()) {

					Header contentRange = getTaskOutputMethod.getResponseHeader("Content-Range");
					if (contentRange == null) {
						Thread.sleep(THREAD_SLEEP_TIME);
						continue;
					}
					if (contentRange != null && !contentRange.getValue().equals("")) {
						String[] splited = contentRange.getValue().split("/");
						offset = Integer.parseInt(splited[1]);
					}

					String outputs = getTaskOutputMethod.getResponseBodyAsString();

					// Convert output to JSON Format
					outputs = outputs.substring(0, outputs.length() - 1).replace("\n", ",");
					outputs = "[" + outputs + "]";

					// Convert Json to TaskOutput Object
					List<TaskOutput> taskOutputList = mapper.readValue(outputs, new TypeReference<List<TaskOutput>>() {
					});

					List<String> responseMessage = new ArrayList<String>();
					for (TaskOutput output : taskOutputList) {

						if (output.getStage() != null) {
							if (lastStage == null || !lastStage.equals(output.getStage())) {
								responseMessage.add("");
								responseMessage.add("  Started    " + output.getStage());
								System.out.println("");
								System.out.println("  Started    " + output.getStage());
							}
						}

						if (output.getStage() != null) {

							if (output.getState().equals("started")) {
								responseMessage.add("  Started    " + output.getStage() + " > " + output.getTask());
								System.out.println("  Started    " + output.getStage() + " > " + output.getTask());
							} else if (output.getState().equals("finished")) {
								responseMessage.add("  Done       " + output.getStage() + " > " + output.getTask());
								System.out.println("  Done       " + output.getStage() + " > " + output.getTask());
							} else if (output.getState().equals("failed")) {
								responseMessage.add("  Failed      " + output.getStage() + " > " + output.getTask());
								System.out.println("  Failed     " + output.getStage() + " > " + output.getTask());
							} else {
								responseMessage.add("  Processing " + output.getStage() + " > " + output.getTask() + "" + output.getProgress());
								System.out.println("  Processing " + output.getStage() + " > " + output.getTask() + "" + output.getProgress());
							}
						} else {
							HashMap<String, String> error = output.getError();
							if (error != null) {
								responseMessage.add(
										"  Error Code : " + error.get("code") + ", Message :" + error.get("message"));
								System.out.println(
										"  Error Code : " + error.get("error") + ", Message :" + error.get("message"));
							}
						}

						lastStage = output.getStage();
					}

					sendTaskOutput(messageTemplate, messageEndpoint, "started", responseMessage);

				}

				log.info("### task info : " + taskInfo.getState());

				// Task 완료 여부 확인
				if (taskInfo.getState().equalsIgnoreCase("done")) {
					System.out.println("Task " + taskId + " done");
					sendTaskOutput(messageTemplate, messageEndpoint, "done",
							Arrays.asList("", "Task " + taskId + " done"));
					break;
				} else if (taskInfo.getState().equalsIgnoreCase("error")) {
					System.out.println("An error occurred while executing the task " + taskId);
					sendTaskOutput(messageTemplate, messageEndpoint, "error",
							Arrays.asList("", "An error occurred while executing the task " + taskId));
					break;
				} else if (taskInfo.getState().equalsIgnoreCase("cancelled")) {
					System.out.println("Cancelled Task " + taskId);
					sendTaskOutput(messageTemplate, messageEndpoint, "cancelled",
							Arrays.asList("", "Canceled Task " + taskId));
					break;
				}

				Thread.sleep(THREAD_SLEEP_TIME);
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendTaskOutput(messageTemplate, messageEndpoint, "error",
					Arrays.asList("", "An exception occurred while executing the task " + taskId));
		}
	}

	public static void sendTaskOutput(SimpMessagingTemplate messageTemplate, String messageEndpoint, String status, List<String> messages) {
		ResponseTaskOuput response = new ResponseTaskOuput();
		response.setState(status);
		response.setMessages(messages);

		messageTemplate.convertAndSend(messageEndpoint, response);
	}
	
	
}
