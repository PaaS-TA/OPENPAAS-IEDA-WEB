package org.openpaas.ieda.web.management.user.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserDAO {
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 사용자 존재 여부 확인
	 * @title               : selectUser
	 * @return            : UserVO
	***************************************************/
	UserVO selectUser(@Param("userId")String userId, @Param("password")String password);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 아이디를 기준으로 사용자 존재 여부 확인
	 * @title               : selectUserByUserId
	 * @return            : UserVO
	***************************************************/
	UserVO selectUserByUserId(@Param("userId")String userId);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 사용자가 가지고 있는 권한을 출력
	 * @title               : getRoleDetailsByRoleId
	 * @return            : List<HashMap<String,String>>
	***************************************************/
	List<HashMap<String,String>> getRoleDetailsByRoleId(@Param("roleId")Integer roleId);	

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 비밀번호 저장
	 * @title               : savePassword
	 * @return            : int
	***************************************************/
	int savePassword(@Param("user")UserVO user);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Map을 통하여 권한 목록을 가져온다.
	 * @title               : getSecurityPathMapList
	 * @return            : List<HashMap<String,String>>
	***************************************************/
	List<HashMap<String,String>> getSecurityPathMapList(@Param("parent_code")Integer parentCode);

}
