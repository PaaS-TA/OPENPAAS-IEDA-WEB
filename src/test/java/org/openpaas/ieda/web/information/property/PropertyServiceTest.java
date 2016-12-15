package org.openpaas.ieda.web.information.property;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.openpaas.ieda.api.deployment.DeploymentDTO;
import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigDAO;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.information.property.dto.PropertyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@TestPropertySource(locations="classpath:application_test.properties")
public class PropertyServiceTest {
	
	@Autowired DirectorConfigDAO dao;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Deployment 목록 정보 조회
	 * @title               : listDeployment
	 * @return            : List<DeploymentInfoDTO>
	***************************************************/
	public List<DeploymentInfoDTO> listDeployment() {
		
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		List<DeploymentInfoDTO> deploymentInfoList = null;
		try {
			//1.1 request client
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			GetMethod get = new GetMethod(DirectorRestHelper.getDeploymentListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			
			String responseBody = setDeploymentResponseBody(); 
			ObjectMapper mapper = new ObjectMapper();
			DeploymentDTO[] deploymentList = mapper.readValue(responseBody, DeploymentDTO[].class);
			
			int idx = 0;
			for ( DeploymentDTO deployment : deploymentList ) {
				if ( deploymentInfoList == null ) 
					deploymentInfoList = new ArrayList<DeploymentInfoDTO>();
				
				DeploymentInfoDTO deploymentInfo = new DeploymentInfoDTO();
				
				deploymentInfo.setRecid(idx++);
				deploymentInfo.setName(deployment.getName());
				String releaseInfo = "";
				for ( HashMap<String, String> release : deployment.getReleases()) {
					releaseInfo = releaseInfo + release.get("name") + " (" + release.get("version") + ")<br>";
				}
				deploymentInfo.setReleaseInfo(releaseInfo);
				
				String stemcellInfo = "";
				for ( HashMap<String, String> stemcell : deployment.getStemcells()) {
					stemcellInfo = stemcellInfo + stemcell.get("name") + " (" + stemcell.get("version") + ")<br>";
				}
				deploymentInfo.setStemcellInfo(stemcellInfo);
				deploymentInfoList.add(deploymentInfo);
			}
		} catch (IOException e) {
			throw new CommonException("notfound.deployment.exception", " 배포 정보 조회 중 오류가 발생하였습니다.", HttpStatus.NOT_FOUND);
		}
		return deploymentInfoList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
	 * @title               : saveDefaultDirector
	 * @return            : DirectorConfigVO
	***************************************************/
	public DirectorConfigVO saveDefaultDirector(){
		//director 설정
		DirectorConfigVO defaultDirector = dao.selectDirectorConfigByDefaultYn("Y");
		if( defaultDirector != null ){
			dao.deleteDirecotr(defaultDirector.getIedaDirectorConfigSeq());
		}
		DirectorConfigVO director = settingDefaultDirector();
		assertTrue(dao.insertDirector( director) == 1);
		
		return director;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치 관리자 설정
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
	 * @description   : 배포명 client 요청 결과 설정
	 * @title               : setDeploymentResponseBody
	 * @return            : String
	***************************************************/
	public String setDeploymentResponseBody(){
		String info = "[{\"name\":\"cf-openstack\",";
				info += "\"releases\":[{\"name\":\"cf\",\"version\":\"236\"}],";
				info += "\"stemcells\":[{\"name\":\"bosh-openstack-kvm-ubuntu-trusty-go_agent\",\"version\":\"3262\"}],";
				info += "\"cloud_config\":\"none\"},";
				info += "{\"name\":\"cf-openstack-diego\",";
				info += "\"releases\":[{\"name\":\"diego\",\"version\":\"0.1481.0\"},";
				info += "{\"name\":\"cflinuxfs2-rootfs\",\"version\":\"1.21.0\"},";
				info += "{\"name\":\"etcd\",\"version\":\"63\"},";
				info += "{\"name\":\"garden-linux\",\"version\":\"0.339.0\"},";
				info += "{\"name\":\"cf\",\"version\":\"236\"}],";
				info += "\"stemcells\":[{\"name\":\"bosh-openstack-kvm-ubuntu-trusty-go_agent\",\"version\":\"3262\"}],";
				info += "\"cloud_config\":\"none\"}]"; 
		return info;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 목록 정보 조회
	 * @title               : getPropertyList
	 * @return            : List<PropertyDTO>
	***************************************************/
	public List<PropertyDTO> getPropertyList(String deploymentName) {
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		//설치가 없을 경우 exception
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		List<PropertyDTO> dtoInfoList= new ArrayList<PropertyDTO>();
		GetMethod get = null;
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			get = new GetMethod(DirectorRestHelper.getPropertyListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(),deploymentName));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			
			PropertyDTO propertyList = settingPropertyListInfo();
			dtoInfoList.add(propertyList);
		}catch(Exception e){
			throw new CommonException("badRequest.property.exception", "Property 목록 정보 조회 오류", HttpStatus.BAD_REQUEST);
		}if( get != null ){
			get.releaseConnection();
		}
		return dtoInfoList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 목록 정보 조회 Httpclient 요청 결과 셋팅
	 * @title               : settingPropertyListInfo
	 * @return            : PropertyDTO
	***************************************************/
	public PropertyDTO settingPropertyListInfo(){
		JSONObject jsonobj = new JSONObject();
		PropertyDTO dto = new PropertyDTO();
		jsonobj.put("name", "PropertyName_JunitTest");
		jsonobj.put("value", "PropertyValue_JunitTest");
		dto.setValue(jsonobj.getString("name"));
		dto.setName(jsonobj.getString("value"));
		return dto;
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 상세 조회
	 * @title               : getPropertyInfo
	 * @return            : PropertyDTO
	***************************************************/
	public PropertyDTO getPropertyInfo(String deploymentName, String name) {
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		//2. 설치가 없을 경우 exception
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		PropertyDTO dtoInfo= new PropertyDTO();
		GetMethod get = null;
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			get = new GetMethod(DirectorRestHelper.getPropertyDetailInfoURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(),deploymentName,name));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			dtoInfo = setPropertyDetailInfo();
		}catch(Exception e){
			throw new CommonException("badRequest.property.exception", "Property 상세 조회 오류", HttpStatus.BAD_REQUEST);
		}if( get != null ){
			get.releaseConnection();
		}
		return dtoInfo;
	}
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 상세 정보 조회 Httpclient 요청 결과 셋팅
	 * @title               : setPropertyDetailInfo
	 * @return            : PropertyDTO
	***************************************************/
	private PropertyDTO setPropertyDetailInfo() {
		JSONObject jsonobj = new JSONObject();
		PropertyDTO dto = new PropertyDTO();
		jsonobj.put("name", "Detail_PropertyName_JunitTest");
		jsonobj.put("value", "Detail_PropertyValue_JunitTest");
		dto.setValue(jsonobj.getString("name"));
		dto.setName(jsonobj.getString("value"));
		
		return dto;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 생성
	 * @title               : createProperty
	 * @return            : void
	***************************************************/
	public void createProperty(String deploymentName, String name, String value) {
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		//2. 설치가 없을 경우 exception
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		PostMethod postMethod = null;
		try {
				HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
				postMethod = new PostMethod(DirectorRestHelper.createPropertURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName));
				postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod); //헤더 정보 셋팅
				postMethod.setRequestHeader("Content-Type", "application/json"); //header 정의
				JSONObject jsonobject = new JSONObject();
				jsonobject.put("name", name);
				jsonobject.put("value", value);
				String resultJson = jsonobject.toString();
				StringRequestEntity params = new StringRequestEntity(resultJson,"application/json","UTF-8");
				postMethod.setRequestEntity(params);
		}catch(Exception e){
			throw new CommonException("badRequest.proeprty.create.exception", "Property 생성 오류", HttpStatus.BAD_REQUEST);
		}if( postMethod != null ){
			postMethod.releaseConnection();
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 수정
	 * @title               : modifyProperty
	 * @return            : void
	***************************************************/
	public void modifyProperty(String deploymentName, String name, String value) {
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		//2. 설치가 없을 경우 exception
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		PutMethod putMethod = null;
		try{
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			putMethod = new PutMethod(DirectorRestHelper.updatePropertURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName, name));
			putMethod = (PutMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)putMethod); //헤더 정보 셋팅
			putMethod.setRequestHeader("Content-Type", "application/json"); //header 정의
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("value", value);
			String resultJson = jsonobject.toString();
			StringRequestEntity params = new StringRequestEntity(resultJson,"application/json","UTF-8");
			putMethod.setRequestEntity(params);
		}catch(Exception e){
			throw new CommonException("badRequest.property.modify.exception", "Property 수정 오류", HttpStatus.BAD_REQUEST);
		}if( putMethod != null ){
			putMethod.releaseConnection();
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 삭제
	 * @title               : deleteProperty
	 * @return            : void
	***************************************************/
	public void deleteProperty(String deploymentName, String name) {
		DirectorConfigVO defaultDirector = saveDefaultDirector();
		//2. 설치가 없을 경우 exception
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception", "기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		DeleteMethod deleteMethod = null;
		try{
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			deleteMethod = new DeleteMethod(DirectorRestHelper.deletePropertURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), deploymentName, name));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod); //헤더 정보 셋팅
			deleteMethod.setRequestHeader("Content-Type", "application/json"); 
		}catch(Exception e){
			throw new CommonException("badRequest.property.delete.exception", "Property 수정 오류", HttpStatus.BAD_REQUEST);
		}if( deleteMethod != null ){
			deleteMethod.releaseConnection();
		}
	}

}
