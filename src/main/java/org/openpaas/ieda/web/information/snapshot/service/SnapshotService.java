package org.openpaas.ieda.web.information.snapshot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.task.TaskInfoDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.information.snapshot.dto.SnapshotListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SnapshotService {
	
	@Autowired private DirectorConfigService directorConfigService;
	final private static Logger LOGGER = LoggerFactory.getLogger(SnapshotService.class);
	final private static int THREAD_SLEEP_TIME = 2 * 1000;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 정보 목록 조회
	 * @title               : getSnapshotList
	 * @return            : List<SnapshotListDTO>
	***************************************************/
	public List<SnapshotListDTO> getSnapshotList(String deploymentName){
		//1.1 git director Info
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("nofound.director.exception",
					"설치 관리자 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		HttpClient client = null;
		GetMethod getMethod= null;
		List<SnapshotListDTO> snapshotInfoList = new ArrayList<SnapshotListDTO>();
		try{
			//1.1 snapshot list
			client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			getMethod = new GetMethod(DirectorRestHelper.getSnapshotListURI(defaultDirector.getDirectorUrl(), 
					defaultDirector.getDirectorPort(), deploymentName));
			getMethod = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)getMethod);
			int statusCode = client.executeMethod(getMethod);
			
			if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
				throw new CommonException("notfound.snapshot.exception", 
						" 스냅샷 조회 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
			}
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			ObjectMapper mapper = new ObjectMapper();
			SnapshotListDTO[] snapshotList = mapper.readValue(getMethod.getResponseBodyAsString(), SnapshotListDTO[].class);
			int idx = 0;
			for ( SnapshotListDTO snapshot : snapshotList ) {
				
				snapshot.setRecid(idx++);
				snapshot.setJob( snapshot.getJob()  +"/" + snapshot.getIndex() );
				snapshot.setDeploymentName(deploymentName);
				snapshotInfoList.add(snapshot);
			}
		}catch (HttpException e){
			throw new CommonException("HttpException.snapshot.exception", " 스냅샷 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (InterruptedException e) {
			throw new CommonException("InterruptedException.snapshot.exception", " 스냅샷 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonParseException e) {
			throw new CommonException("JsonParseException.snapshot.exception", " 스냅샷 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonMappingException e) {
			throw new CommonException("JsonMappingException.snapshot.exception", " 스냅샷 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("IOException.snapshot.exception", " 스냅샷 정보 조회중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally{
			if( getMethod  != null){
				getMethod.releaseConnection();
			}
		}
		return snapshotInfoList; 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 삭제 요청
	 * @title               : deleteSnapshots
	 * @return            : String
	***************************************************/
	public String deleteSnapshots(String type, SnapshotListDTO dto){
		
		//1.1 git director Info
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"설치 관리자 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		String status = "";
		String taskId = "";
		HttpClient client = null;
		DeleteMethod deleteMethod= null;
		GetMethod getTaskStaus = null;
		try{
			client = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			if( "all".equals(type) ){
				deleteMethod = new DeleteMethod(DirectorRestHelper.deleteAllSnapshotURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName()));
			}else{
				deleteMethod = new DeleteMethod(DirectorRestHelper.deleteSnapshotURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName(),  dto.getSnapshotCid()));
			}
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod);
			
			int statusCode = client.executeMethod(deleteMethod);
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				taskId = DirectorRestHelper.getTaskId(location.getValue());
				
				Thread.sleep(THREAD_SLEEP_TIME);
				
				ObjectMapper mapper = new ObjectMapper();
				while (true) {
					if( getTaskStaus != null ){
						getTaskStaus.releaseConnection();
					}
					//1. Task 상태 정보 요청
					getTaskStaus = new GetMethod(DirectorRestHelper
							.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
					getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(),
							defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
					
					statusCode = client.executeMethod(getTaskStaus);
					
					if (HttpStatus.valueOf(statusCode) == HttpStatus.OK) {
					
						TaskInfoDTO taskInfo = mapper.readValue(getTaskStaus.getResponseBodyAsString(), TaskInfoDTO.class);
						if( LOGGER.isDebugEnabled()){
							LOGGER.debug(taskInfo.getState());
						}
						Thread.sleep(THREAD_SLEEP_TIME);
						
						if (taskInfo.getState().equalsIgnoreCase("done")) {
							status = "done";
							break;
						}else if (taskInfo.getState().equalsIgnoreCase("error")) {
							status =  "error";
							break;
						} else if (taskInfo.getState().equalsIgnoreCase("cancelled")) {
							status = "cancelled";
							break;
						}
					}
					Thread.sleep(THREAD_SLEEP_TIME);
				}
			} else {
				throw new CommonException("badRequest.snapshot.exception", " 스냅샷 삭제 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
			}
		}catch (IOException e) {
			throw new CommonException("ioFileRead.snapshot.exception", 
					" 스냅샷 삭제 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch (InterruptedException e){
			throw new CommonException("Interrupted.snapshot.exception", 
					" 스냅샷 삭제 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(RuntimeException e){
			throw new CommonException("runtime.snapshot.exception", 
					" 스냅샷 삭제 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally{
			if( deleteMethod  != null){
				deleteMethod.releaseConnection();
			}
			if( getTaskStaus != null ){
				getTaskStaus.releaseConnection();
			}
		}
		return status;
	}
}
