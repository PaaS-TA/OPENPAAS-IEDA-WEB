package org.openpaas.ieda.web.information.property;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.OpenpaasIedaWebApplication;
import org.openpaas.ieda.TestBeansConfiguration;
import org.openpaas.ieda.api.deployment.DeploymentInfoDTO;
import org.openpaas.ieda.web.common.BaseTestController;
import org.openpaas.ieda.web.information.property.dto.PropertyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {OpenpaasIedaWebApplication.class, TestBeansConfiguration.class})
@WebAppConfiguration
@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
public class PropertyControllerTest extends BaseTestController{
	
	@Autowired WebApplicationContext wac;
	@Autowired PropertyServiceTest service;
	
	private MockMvc mockMvc;
	protected MockRestServiceServer mockServer;
	private final static Logger LOGGER = LoggerFactory.getLogger(PropertyControllerTest.class);
	
	/*************************************** URL *******************************************/
	final static String VIEW_URL = "/info/property"; //Property 화면 이동
	final static String DEPLOYMENT_LIST_URL = "/common/use/deployments";//배포명 조회
	final static String PROPERTY_LIST_URL="/info/property/list/{deployment}";//Property 목록 조회
	final static String PROPERTY_DETAIL_URL="/info/property/list/detailInfo";//Property 상세 조회
	final static String CREATE_PROPERTY_URL = "/info/property/modify/createProperty";//Property 생성
	final static String MODIFY_PROPERTY_URL = "/info/property/modify/updateProperty";//Property 수정
	final static String DELETE_PROPERTY_URL ="/info/property/modify/deleteProperty";//Property 삭제
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 하나의 메소드가 실행되기 전 호출
	 * @title               : setUp
	 * @return            : void
	***************************************************/
	@Before
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		
		getLoggined();
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 화면 이동
	 * @title               : testGoPropertyTest
	 * @return            : void
	***************************************************/
	@Test
	public void testGoPropertyTest() throws Exception{
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  Property 화면 요청 테스트 TEST START  ================="); 
		}
		ResultActions result = mockMvc.perform(get(VIEW_URL).contentType(MediaType.APPLICATION_JSON));
		result.andDo(MockMvcResultHandlers.print())
		.andReturn();
		if(LOGGER.isInfoEnabled()){ 
			LOGGER.info("=================  Property 화면 요청 테스트 TEST END  ================="); 
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Deployment 목록 정보 조회
	 * @title               : testGetDeploymentList
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetDeploymentList(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포명 조회 요청 테스트 테스트");  }
		List<DeploymentInfoDTO> deploymentInfo = service.listDeployment();
		if( LOGGER.isDebugEnabled()) {
			LOGGER.debug(deploymentInfo.size() + "");
			for(int i=0; i<deploymentInfo.size(); i++){
				LOGGER.debug("deployment name : " +  deploymentInfo.get(i).getName() );
			}
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> 배포명 조회 요청 테스트 테스트 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 목록 정보 조회
	 * @title               : testGetPropertyList
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetPropertyList(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 조회 요청 테스트");  }
		String deploymentName = "cf-openstack";
		List<PropertyDTO> propertyList = service.getPropertyList(deploymentName);
		if( LOGGER.isDebugEnabled()) {
			LOGGER.debug(propertyList.size() + "");
			for(int i=0; i<propertyList.size(); i++){
				LOGGER.debug("deployment name : " +  propertyList.get(i).getName());
				LOGGER.debug("deployment value : " +  propertyList.get(i).getValue());
			}
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 조회 요청 테스트 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 상세 정보 조회
	 * @title               : testGetPropertyDetailInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testGetPropertyDetailInfo(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 상세 조회 요청 테스트");  }
		String deploymentName = "cf-openstack";
		String name ="PropertyName_JunitTest";
		PropertyDTO propertyInfo = service.getPropertyInfo(deploymentName,name);
		if( LOGGER.isDebugEnabled()) {
				LOGGER.debug("deployment name : " +  propertyInfo.getName());
				LOGGER.debug("deployment value : " +  propertyInfo.getValue());
		}
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 상세 조회 요청 테스트 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 정보 저장
	 * @title               : testCreatePropertyInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testCreatePropertyInfo(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 생성 요청 테스트");  }
		String deploymentName = "cf-openstack";
		String name ="PropertyName_JunitTest";
		String value ="PropertyValue_JunitTest";
		service.createProperty(deploymentName,name,value);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 생성 요청 테스트 성공");  }
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 정보 수정
	 * @title               : testModifyPropertyInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testModifyPropertyInfo(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 수정 요청 테스트");  }
		String deploymentName = "cf-openstack";
		String name ="PropertyName_JunitTest";
		String value ="PropertyModifyValue_JunitTest";
		service.modifyProperty(deploymentName,name,value);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 수정 요청 테스트 성공");  }
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Property 정보 삭제
	 * @title               : testDeletePropertyInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	@Test
	public void testDeletePropertyInfo(){
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 삭제 요청 테스트");  }
		String deploymentName = "cf-openstack";
		String name ="PropertyName_JunitTest";
		service.deleteProperty(deploymentName,name);
		if(LOGGER.isInfoEnabled()){ LOGGER.info("================================> Property 삭제 요청 테스트 성공");  }
	}
	
}
