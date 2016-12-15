package org.openpaas.ieda.web.information.vms.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.unitils.thirdparty.org.apache.commons.io.IOUtils;

@Service
public class VmsLogDownloadService {
	
	@Autowired private DirectorConfigService directorConfigService;
	
	final private static int THREAD_SLEEP_TIME = 2 * 1000;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Agent 및 Job 로그 다운로드 요청
	 * @title               : doDownloadLog
	 * @return            : void
	***************************************************/
	public void  doDownloadLog(String jobName, String index, String deploymentName, String type, HttpServletRequest request, HttpServletResponse response){
		
		int statusCode = 0;
		String taskId = "";
		String logFile = "";
		try{
			//설치 관리자 정보 조회
			DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
			if ( defaultDirector == null ) {
				throw new CommonException("notfound.dirctor.exception",
						"설치 관리자 정보가 존재하지 않습니다..", HttpStatus.NOT_FOUND);
			}
			
			//Create Agent/Job Log
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod getLogMethod = new GetMethod(DirectorRestHelper.createLogURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(),  deploymentName, jobName, index, type));
			getLogMethod = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getLogMethod);
			statusCode = httpClient.executeMethod(getLogMethod);
			
			//get taskId
			String[] segments  = getLogMethod.getPath().split("/");
			taskId = segments[segments.length - 1];
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			//Task status Info
			GetMethod getTaskStaus = new GetMethod(DirectorRestHelper.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
			getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
			statusCode = httpClient.executeMethod(getTaskStaus);
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			if ( statusCode == HttpStatus.OK.value() ){
				JSONObject obj = new JSONObject(getTaskStaus.getResponseBodyAsString());
				String result = obj.get("result").toString();
				// download log by result
				GetMethod getResultOutput = new GetMethod(DirectorRestHelper.getResultOutputURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), result));
				getResultOutput = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getResultOutput);
				String range = "bytes=" + 0 + "-";
				getResultOutput.setRequestHeader("Range", range);
				
				statusCode = httpClient.executeMethod(getResultOutput);
				
				if ( statusCode == HttpStatus.OK.value() || statusCode == HttpStatus.PARTIAL_CONTENT.value()){
						
					byte[] content = getResultOutput.getResponseBody();
					Date now = new Date();
					SimpleDateFormat dataformat = new SimpleDateFormat("yyyymmdd_HHmmss", Locale.KOREA);
					logFile = jobName+"_"+index+"_"+dataformat.format(now)+"_"+type;
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename=" + logFile+".tgz");
					IOUtils.write(content, response.getOutputStream());

				}else{
					throw new CommonException("notfound.logDownload.exception",
							"리소스를 다운로드 할 수 없습니다. ", HttpStatus.NOT_FOUND);
				}
			}
		}catch(RuntimeException e){
			throw new CommonException("runtime.logDownload.exception",
					"해당 로그 다운로드 요청 중 문제가 발생했습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
				throw new CommonException("ioFileRead.logDownload.exception",
						"해당 로그를 읽어오는 중 문제가 발생했습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JSONException e) {
			throw new CommonException("json.logDownload.exception",
					"해당 로그를 읽어오는 중 문제가 발생했습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InterruptedException e) {
			throw new CommonException("interrupted.logDownload.exception",
					"해당 로그 다운로드 요청 중 문제가 발생했습니다. ", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
