package org.openpaas.ieda.web.management.user.service;

import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.management.user.dao.UserDAO;
import org.openpaas.ieda.web.management.user.dao.UserVO;
import org.openpaas.ieda.web.management.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
		
	@Autowired private UserDAO dao;

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 패스워드를 재설정 한다
	 * @title               : savePassword
	 * @return            : int
	***************************************************/
	public int savePassword(UserDTO.SavePassword savePasswordDto) {

		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		UserVO user = new UserVO();
		
		user.setUserId(sessionInfo.getUserId());
		user.setUpdateUserId(sessionInfo.getUserId());
		user.setPassword(savePasswordDto.getPassword());

		// 입력된 패스워드 변경 정보를 데이터베이스에 저장한다.
		return dao.savePassword(user);
	}
}
