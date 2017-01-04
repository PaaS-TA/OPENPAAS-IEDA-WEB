package org.openpaas.ieda.web.management.user.service;

import java.sql.SQLException;
import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.management.user.dao.UserManagementDAO;
import org.openpaas.ieda.web.management.user.dao.UserManagementVO;
import org.openpaas.ieda.web.management.user.dto.UserManagementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
@Service
public class UserManagementService {
	
	@Autowired UserManagementDAO dao;
	@Autowired SessionRegistry sessionRegistry;
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 목록 정보 조회
	 * @title               : getUserInfoList
	 * @return            : List<UserManagementVO>
	***************************************************/
	public List<UserManagementVO> getUserInfoList() {
		List<UserManagementVO> userList = dao.selectUserInfoList();
		for(int i=0;i<userList.size();i++){
			userList.get(i).setRecid(i);
		}
		return userList;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 저장
	 * @title               : savaUserInfo
	 * @return            : void
	***************************************************/
	public void savaUserInfo(UserManagementDTO.Regist dto) {
		UserManagementVO findUserId = dao.selectUserIdInfoById(dto.getUserId());	
		if(findUserId!=null ){
			throw new CommonException("CONFLICT.user.exception",
					" 사용자 아이디 중복 입니다.", HttpStatus.CONFLICT);
		}
		UserManagementVO userVO = new UserManagementVO();
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		userVO.setUserId(dto.getUserId());
		userVO.setUserName(dto.getUserName());
		userVO.setUserPassword(dto.getUserPassword());
		userVO.setEmail(dto.getEmail());
		userVO.setCreateUserId(sessionInfo.getUserId());
		userVO.setUpdateUserId(sessionInfo.getUserId());
		userVO.setRoleId(dto.getRoleId());
		userVO.setInitPassYn(dto.getInitPassYn());
		try{
		dao.insertUserInfo(userVO);
		}catch(Exception e){
			throw new CommonException("sql.user.exception",
					"사용자 정보 등록 중 에러가 발생 했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 수정
	 * @title               : updateUserInfo
	 * @return            : void
	***************************************************/
	public void updateUserInfo(UserManagementDTO.Regist dto, String userId) throws SQLException {
		UserManagementVO findUserId = dao.selectUserIdInfoById(userId);
		if(findUserId==null ){
			throw new CommonException("NOT_FOUND.user.exception",
					"해당 사용자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		UserManagementVO userVO = new UserManagementVO();
		userVO.setUserId(userId);
		userVO.setUserName(dto.getUserName());
		userVO.setUserPassword(dto.getUserPassword());
		userVO.setUpdateUserId(findUserId.getCreateUserId());
		userVO.setUpdateDate(findUserId.getCreateDate());
		userVO.setRoleId(dto.getRoleId());
		userVO.setInitPassYn(dto.getInitPassYn());
		userVO.setEmail(dto.getEmail());
		dao.updateUserInfoByUid(userVO);
		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 삭제
	 * @title               : deleteUserInfo
	 * @return            : void
	***************************************************/
	@Transactional
	public void deleteUserInfo(String userId) {
		UserManagementVO findUserInfo = dao.selectUserIdInfoById(userId);
		if(findUserInfo==null){
			throw new CommonException("notfound.user.exception",
					"해당 사용자 정보를 찾을 수 없습니다 ", HttpStatus.NOT_FOUND);
		}
		try{
			dao.deleteUserInfoByUid(userId);
			expireUserSessions(userId);
		}catch(Exception e){
			e.printStackTrace();
			throw new CommonException("sql.user.exception",
					"사용자 정보 삭제 중 에러가 발생 했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 삭제된 유저의 세션 강제 종료
	 * @title               : expireUserSessions
	 * @return            : void
	***************************************************/
	public void expireUserSessions(String username) {
		List<SessionInformation> usersessions = sessionRegistry.getAllSessions(username, false);
		if(usersessions !=null && usersessions.size() > 0){
			for (int i = 0; i < usersessions.size(); i++){
				usersessions.get(i).expireNow();
			}
		} 
    }
}
