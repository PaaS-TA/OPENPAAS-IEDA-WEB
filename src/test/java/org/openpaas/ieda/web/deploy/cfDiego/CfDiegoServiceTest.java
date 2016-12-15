package org.openpaas.ieda.web.deploy.cfDiego;

import org.openpaas.ieda.web.deploy.cf.CfServiceTest;
import org.openpaas.ieda.web.deploy.cfDiego.dao.CfDiegoDAO;
import org.openpaas.ieda.web.deploy.cfDiego.dao.CfDiegoVO;
import org.openpaas.ieda.web.deploy.diego.DiegoServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@TransactionConfiguration(defaultRollback=true)
@TestPropertySource(locations="classpath:application_test.properties")
@Service
public class CfDiegoServiceTest {
	
	@Autowired private CfServiceTest cfService;
	@Autowired private DiegoServiceTest diegoService;
	@Autowired private CfDiegoDAO cfDiegoDao;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 통합 설치 정보 저장
	 * @title               : saveCfDiegoInfo
	 * @return            : void
	***************************************************/
	@Rollback(true)
	public void saveCfDiegoInfo(){
		//save the cf info
		cfService.saveCfInfo();
		//save the diego Info
		diegoService.insertDiegoInfo();
		//save the cf&diego info
		CfDiegoVO vo = setCfDiegoInfo();
		cfDiegoDao.insertCfDiegoInfo(vo);
	}
	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF&Diego 정보 설정
	 * @title               : setCfDiegoInfo
	 * @return            : CfDiegoVO
	***************************************************/
	public CfDiegoVO setCfDiegoInfo() {
		CfDiegoVO vo = new CfDiegoVO();
		vo.setId(1);
		vo.getCfVo().setId(1);
		vo.getDiegoVo().setId(1);
		vo.setIaasType("openstack");
		vo.setCreateUserId("tester");
		vo.setUpdateUserId("tester");
		
		return vo;
	}
}
