package org.openpaas.ieda.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.ieda.web.code.IEDACommonCodeService;
//import org.openpaas.ieda.common.code.IEDACommonCode;
//import org.openpaas.ieda.common.code.IEDACommonCodeDto;
//import org.openpaas.ieda.common.code.IEDACommonCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=OpenpaasIedaWebApplication.class)
@WebAppConfiguration
@Transactional
@IntegrationTest
public class IEDACommonCodeControllerTest {
	
	@Autowired
	WebApplicationContext wac;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	IEDACommonCodeService service;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
/*	private IEDACommonCodeDto.Create commonCodeCreateDto() {
		IEDACommonCodeDto.Create createDto = new IEDACommonCodeDto.Create();
		createDto.setCodeName("aaaaaa");
		createDto.setCodeValue("bbbbbb");
		createDto.setCodeDescription("cccccc");
		createDto.setSortOrder(5);
		createDto.setParentCodeIdx(null);
		
		return createDto;
	}*/
	
	@Test
	public void getCodes() throws Exception {
		ResultActions result = mockMvc.perform(get("/codes"));
		
		result.andDo(print());
		result.andExpect(status().isOk());
	}
	
	@Test
	public void getCode() throws Exception {
		ResultActions result = mockMvc.perform(get("/codes/1"));
		
		result.andDo(print());
		result.andExpect(status().isOk());
	}
	
	@Test
	public void getChildCodeList() throws Exception {
		ResultActions result = mockMvc.perform(get("/codes/child/1"));
		
		result.andDo(print());
		result.andExpect(status().isOk());
	}	
	
	/*	@Test
	public void createCode() throws Exception {
		IEDACommonCodeDto.Create commonCodeDto = new IEDACommonCodeDto.Create();
		commonCodeDto.setCodeKey("llalla");
		commonCodeDto.setCodeName("hfhfhffh");
		commonCodeDto.setCodeValue("Code Value");
		commonCodeDto.setCodeDescription("Code Description");
		commonCodeDto.setParentCodeKey("");
		
		ResultActions result = mockMvc.perform(post("/codes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commonCodeDto)));
		result.andDo(print());
		result.andExpect(status().isCreated());
	}*/
	
//	@Test
//	public void createAccount_BadRequeset() throws Exception {
//		IEDACommonCodeDto.Create commonCodeDto = new IEDACommonCodeDto.Create();
//		commonCodeDto.setCodeKey("000000");
//		commonCodeDto.setCodeName("aaaa ");
//		commonCodeDto.setCodeValue("Code Value");
//		commonCodeDto.setCodeDescription("Code Description");
//		commonCodeDto.setParentCodeKey("");
//		
//		ResultActions result = mockMvc.perform(post("/codes")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsString(commonCodeDto)));
//		result.andDo(print());
//		result.andExpect(status().isBadRequest());
//	}
	
/*	@Test
	public void updateCode() throws JsonProcessingException, Exception {
		IEDACommonCodeDto.Create createDto = commonCodeCreateDto();
		IEDACommonCode commonCode = service.createCode(createDto);
		
		IEDACommonCodeDto.Update updateDto = new IEDACommonCodeDto.Update();
		updateDto.setCodeName("654321");
		updateDto.setCodeValue("zzzzzz");
		updateDto.setCodeDescription("ffffff");
		updateDto.setSortOrder((long)3);
		updateDto.setParentCodeKey("ABCDEF");
				
		ResultActions result = mockMvc.perform(put("/codes/" + commonCode.getCodeKey())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)));
		
		result.andDo(print());
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.codeName", is("654321")));
		result.andExpect(jsonPath("$.codeValue", is("zzzzzz")));
	}*/
	
/*	@Test
	public void deleteCode() throws Exception {
		ResultActions result = mockMvc.perform(delete("/codes/100"));
		result.andDo(print());
		result.andExpect(status().isBadRequest());
		
		IEDACommonCodeDto.Create createDto = commonCodeCreateDto();
		IEDACommonCode commonCode = service.createCode(createDto);
		
		result = mockMvc.perform(delete("/codes/" + commonCode.getCodeKey()));
		result.andDo(print());
		result.andExpect(status().isNoContent());
	}*/

}
