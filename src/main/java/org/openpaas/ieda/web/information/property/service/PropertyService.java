package org.openpaas.ieda.web.information.property.service;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONObject;
import org.openpaas.ieda.api.director.utility.DirectorRestHelper;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;
import org.openpaas.ieda.web.config.setting.service.DirectorConfigService;
import org.openpaas.ieda.web.information.property.dto.PropertyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PropertyService {
	@Autowired private DirectorConfigService directorConfigService;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 정보 목록 조회
	 * @title               : getPropertyList
	 * @return            : List<PropertyDTO>
	***************************************************/
	public List<PropertyDTO> getPropertyList(String deployment) {
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		//2. 설치가 없을 경우 exception
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		List<PropertyDTO> dtoInfoList= null;
		PropertyDTO[] propertyList= null;
		GetMethod get = null;
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			get = new GetMethod(DirectorRestHelper.getPropertyListURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(),deployment));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			httpClient.executeMethod(get);
			
			if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
				ObjectMapper mapper = new ObjectMapper();
				propertyList = mapper.readValue(get.getResponseBodyAsString(), PropertyDTO[].class);
				dtoInfoList = new ArrayList<PropertyDTO>();
				for(int i=0;i<propertyList.length;i++){
					PropertyDTO dto = new PropertyDTO();
					dto.setRecid(i);
					dto.setName(propertyList[i].getName());
					dto.setValue(propertyList[i].getValue());
					dtoInfoList.add(i, dto);
				}
			}
		} catch(RuntimeException e){
			throw new CommonException("runtime.property.excepion",
					"Property 목록 정보 조회  중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonParseException e) {
			throw new CommonException("jsonParse.property.excepion",
					"Property 목록 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonMappingException e) {
			throw new CommonException("jsonMapping.property.excepion",
					"Property 목록 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("ioFileRead.property.excepion",
					"Property 목록 정보 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}  finally{
			if( get != null ){
				get.releaseConnection();
			}
		}
		return dtoInfoList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 프로퍼티 상세 조회
	 * @title               : getPropertyDetailInfo
	 * @return            : PropertyDTO
	***************************************************/
	public PropertyDTO getPropertyDetailInfo(PropertyDTO paramDto) {
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		//2. 설치가 없을 경우 exception
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		PropertyDTO dtoInfo= null;
		GetMethod get = null;
		try {
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			get = new GetMethod(DirectorRestHelper.getPropertyDetailInfoURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(),paramDto.getDeploymentName(), paramDto.getName()));
			get = (GetMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)get);
			httpClient.executeMethod(get);
			
			if ( !StringUtils.isEmpty(get.getResponseBodyAsString()) ) {
				ObjectMapper mapper = new ObjectMapper();
				dtoInfo = mapper.readValue(get.getResponseBodyAsString(), PropertyDTO.class);
			}
		} catch(RuntimeException e){
			throw new CommonException("runtime.property.excepion",
					"Property 상세 조회 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonParseException e) {
			throw new CommonException("jsonParse.property.excepion",
					"Property 상세 조회 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (JsonMappingException e) {
			throw new CommonException("jsonMapping.property.excepion",
					"Property 상세 조회 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("ioFilRead.property.excepion",
					"Property 상세 조회 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally{
			if( get != null ){
				get.releaseConnection();
			}
		}
		return dtoInfo;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 생성 요청
	 * @title               : createProperyInfo
	 * @return            : void
	***************************************************/
	public void createProperyInfo(PropertyDTO dto, Principal principal) {
		
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		int statusCode;
		PostMethod postMethod = null;
		try {
				HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
				postMethod = new PostMethod(DirectorRestHelper.createPropertURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName()));
				postMethod = (PostMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)postMethod); //헤더 정보 셋팅
				postMethod.setRequestHeader("Content-Type", "application/json"); //header 정의
				
				JSONObject jsonobject = new JSONObject();
				jsonobject.put("name", dto.getName());
				jsonobject.put("value", dto.getValue());
				String resultJson = jsonobject.toString();
				
				StringRequestEntity params = new StringRequestEntity(resultJson,"application/json","UTF-8");
				postMethod.setRequestEntity(params);
				statusCode = httpClient.executeMethod(postMethod);
				
				if(statusCode == 400){
					throw new CommonException("existPropertyName.property.exception", "Property 명이 중복됐습니다.", HttpStatus.CONFLICT);
				}
		} catch(RuntimeException e){
			throw new CommonException("runtime.property.exception", "Property 생성 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (HttpException e) {
					throw new CommonException("http.property.exception", "Property 생성 요청 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("ioFileRead.property.exception", "Property 생성 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally{
			if( postMethod != null ){
				postMethod.releaseConnection();
			}
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 수정 요청
	 * @title               : updateProperyInfo
	 * @return            : void
	***************************************************/
	public void updateProperyInfo(PropertyDTO dto, Principal principal) {
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		PutMethod putMethod = null;
		try{
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			putMethod = new PutMethod(DirectorRestHelper.updatePropertURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName(),dto.getName()));
			putMethod = (PutMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)putMethod); //헤더 정보 셋팅
			putMethod.setRequestHeader("Content-Type", "application/json"); //header 정의
			
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("value", dto.getValue());
			String resultJson = jsonobject.toString();
			StringRequestEntity params = new StringRequestEntity(resultJson,"application/json","UTF-8");
			putMethod.setRequestEntity(params);
			
			httpClient.executeMethod(putMethod);
			
		} catch(RuntimeException e){
			throw new CommonException("runtime.propertyUpdate.excepiton",
					"Property 수정 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (HttpException e) {
			throw new CommonException("http.propertyUpdate.excepiton",
					"Property 수정 요청 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("ioFileRead.propertyUpdate.excepiton",
					"Property 수정 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} finally{
			if( putMethod != null ){
				putMethod.releaseConnection();
			}
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 삭제 요청
	 * @title               : deleteProperyInfo
	 * @return            : void
	***************************************************/
	public void deleteProperyInfo(PropertyDTO dto, Principal principal) {
		DirectorConfigVO defaultDirector = directorConfigService.getDefaultDirector();
		int statusCode;
		if ( defaultDirector == null ) {
			throw new CommonException("notfound.director.exception",
					"기본 설치관리자 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		DeleteMethod deleteMethod = null;
		try{
			HttpClient httpClient = DirectorRestHelper.getHttpClient(defaultDirector.getDirectorPort());
			deleteMethod = new DeleteMethod(DirectorRestHelper.deletePropertURI(defaultDirector.getDirectorUrl(), defaultDirector.getDirectorPort(), dto.getDeploymentName(),dto.getName()));
			deleteMethod = (DeleteMethod)DirectorRestHelper.setAuthorization(defaultDirector.getUserId(), defaultDirector.getUserPassword(), (HttpMethodBase)deleteMethod); //헤더 정보 셋팅
			statusCode = httpClient.executeMethod(deleteMethod);
			
			if(statusCode==404){
				throw new CommonException("notFound.PropertyDelete.exception", "해당 Property를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
			}
		} catch(RuntimeException e){
			throw new CommonException("runtime.PropertyDelete.exception", "Property 삭제 요청 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (HttpException e) {
			throw new CommonException("http.PropertyDelete.exception", "Property 삭제 요청 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			throw new CommonException("ioFileRead.PropertyDelete.exception", "Property 삭제 요청 중 문제가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}  finally {
			if( deleteMethod != null ){
				deleteMethod.releaseConnection();
			}
		}
	}
	
}
