package org.openpaas.ieda.web.management.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserManagementDAO {
		
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 목록 정보 조회
	 * @title               : selectUserInfoList
	 * @return            : List<UserManagementVO>
	***************************************************/
	List<UserManagementVO> selectUserInfoList();
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 저장
	 * @title               : insertUserInfo
	 * @return            : int
	***************************************************/
	int insertUserInfo(@Param("userVO") UserManagementVO userVO);
 
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 아이디 중복 검사
	 * @title               : selectUserIdInfoById
	 * @return            : UserManagementVO
	***************************************************/
	UserManagementVO selectUserIdInfoById(@Param("userId")String userId);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 수정
	 * @title               : updateUserInfoByUid
	 * @return            : void
	***************************************************/
	void updateUserInfoByUid(@Param("userVO")UserManagementVO userVO);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 정보 삭제
	 * @title               : deleteUserInfoByUid
	 * @return            : void
	***************************************************/
	void deleteUserInfoByUid(@Param("userId")String userId);
}
