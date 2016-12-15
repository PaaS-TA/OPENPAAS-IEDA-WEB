package org.openpaas.ieda.web.information.snapshot;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.api.task.TaskInfoDTO;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.information.snapshot.dto.SnapshotListDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@TransactionConfiguration(defaultRollback=true)
@Transactional
@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class SnapshotServiceTest {

	@Autowired DirectorConfigDAO directorDao;
	
	final private static int THREAD_SLEEP_TIME = 1 * 1000;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 정보 목록 조회
	 * @title               : getSnapshotList
	 * @return            : List<SnapshotListDTO>
	***************************************************/
	public List<SnapshotListDTO> getSnapshotList(String deploymentName){
	
		//1.1 select default director info
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
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
			int statusCode = HttpStatus.OK.value();
			
			if (HttpStatus.valueOf(statusCode) != HttpStatus.OK) {
				throw new CommonException("notfound.snapshot.exception", " 스냅샷 조회 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
			}
			
			Thread.sleep(THREAD_SLEEP_TIME);
			
			ObjectMapper mapper = new ObjectMapper();
			
			SnapshotListDTO[] snapshotList = mapper.readValue(settingSnapshotListInfo(), SnapshotListDTO[].class);
			int idx = 0;
			for ( SnapshotListDTO snapshot : snapshotList ) {
				snapshot.setRecid(idx++);
				snapshot.setDeploymentName(deploymentName);
				snapshotInfoList.add(snapshot);
			}
		}catch(Exception e){
			throw new CommonException("notfound.snapshot.exception", " 스냅샷 정보 조회중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}finally{
			if( getMethod  != null){
				getMethod.releaseConnection();
			}
		}
		return snapshotInfoList; 
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 삭제
	 * @title               : deleteSnapshots
	 * @return            : String
	***************************************************/
	public String deleteSnapshots(String type, SnapshotListDTO dto){
		
		//1.1 select default director info
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
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
			int statusCode = HttpStatus.MOVED_PERMANENTLY.value();
			
			if ( statusCode == HttpStatus.MOVED_PERMANENTLY.value()
			  || statusCode == HttpStatus.MOVED_TEMPORARILY.value()	) {
				
				Header location = deleteMethod.getResponseHeader("Location");
				taskId = String.valueOf(1);
				
				Thread.sleep(THREAD_SLEEP_TIME);
				ObjectMapper mapper = new ObjectMapper();
				while (true) {
					
					if( getTaskStaus != null ){
						getTaskStaus.releaseConnection();
					}
					//1. Task 상태 정보 요청
					getTaskStaus = new GetMethod(DirectorRestHelper.getTaskStatusURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), taskId));
					getTaskStaus = (GetMethod) DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase) getTaskStaus);
					statusCode = HttpStatus.OK.value();
					
					if (HttpStatus.valueOf(statusCode) == HttpStatus.OK) {
					
						TaskInfoDTO taskInfo = mapper.readValue(settingDeleteSnapshot(), TaskInfoDTO.class);
						Thread.sleep(THREAD_SLEEP_TIME);
						
						if (taskInfo.getState().equalsIgnoreCase("done")) {
							status = "done";
							break;
						}else if (taskInfo.getState().equalsIgnoreCase("error")) {
							status = "error";
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
			throw new CommonException("badRequest.snapshot.exception", 
					" 스냅샷 삭제 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}catch (InterruptedException e){
			throw new CommonException("badRequest.snapshot.exception", 
					" 스냅샷 삭제 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}catch(Exception e){
			throw new CommonException("badRequest.snapshot.exception", 
					" 스냅샷 삭제 중 오류가 발생하였습니다.", HttpStatus.BAD_REQUEST);
		}finally{
			if( deleteMethod  != null){
				deleteMethod.releaseConnection();
			}
			if( getTaskStaus != null ){
				getTaskStaus.releaseConnection();
			}
		}
		return status;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : saveDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO saveDefaultDirector(){
		//director 설정
		DirectorConfigVO defaultDirector = directorDao.selectDirectorConfigByDefaultYn("Y");
		if( defaultDirector != null ){
			directorDao.deleteDirecotr(defaultDirector.getIedaDirectorConfigSeq());
		}
		DirectorConfigVO director = settingDefaultDirector();
		assertTrue(directorDao.insertDirector( director) == 1);
		
		return director;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 정보 설정
	 * @title               : settingDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO settingDefaultDirector(){
		DirectorConfigVO vo = new DirectorConfigVO();
		vo.setIedaDirectorConfigSeq(1);
		vo.setDefaultYn("Y");
		vo.setDirectorCpi("openstack-cpi");
		vo.setDirectorName("bosh");
		vo.setDirectorPort(25555);
		vo.setDirectorUrl("10.10.10.10");
		vo.setDirectorUuid("3b623d50-10c1-450a-8aad-test");
		vo.setDirectorVersion("1.3252.0");
		vo.setCreateUserId("tester");
		vo.setUpdateUserId("tester");
		return vo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 조회 결과 정보
	 * @title               : settingSnapshotListInfo
	 * @return            : String
	***************************************************/
	public String settingSnapshotListInfo(){

		String info = "[{\"job\":\"consul_z1\",";
		info += "\"index\":0,";
		info += "\"uuid\":\"bc7216aa-40ca-409e-b7a2-b881a41dtest\",";		
		info += "\"snapshot_cid\":\"7ef75bea-3d3a-45f4-8782-57dcecca7121\",";
		info += "\"created_at\":\"2016-09-22 02:24:40 UTC\",";
		info += "\"clean\":false}]";
		
		return info;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 삭제 요청 결과 정보 설정
	 * @title               : settingDeleteSnapshot
	 * @return            : String
	***************************************************/
	public String settingDeleteSnapshot(){
		String info = "{\"id\":3118,";
		info += "\"state\":\"done\",";
		info += "\"description\":\"delete snapshot\",";
		info += "\"timestamp\":1474524117,";
		info += "\"started_at\":1474524110,";
		info += "\"result\":\"snapshot(s) cb227ec7-b827-4fcd-ac22-8c81c8a-test deleted\",";
		info += "\"user\":\"tester\",";
		info += "\"deployment\":null}";
		
		return info;
	}
}
