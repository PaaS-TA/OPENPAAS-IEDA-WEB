package org.openpaas.ieda.web.management.auth.dao;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AuthManagementDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 조회
	 * @title               : selectRoleGroupList
	 * @return            : List<AuthManagementVO>
	***************************************************/
	List<AuthManagementVO> selectRoleGroupList();
	
	 /***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 상세 코드 조회
	 * @title               : selectRoleDetailListByRoleId
	 * @return            : List<HashMap<String,Object>>
	***************************************************/
	List<HashMap<String,Object>> selectRoleDetailListByRoleId(@Param("roleId")int roleId, @Param("parentCode")String parentCode);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 등록 되어 있는 권한 그룹 명 조회
	 * @title               : selectRoleInfoByRoleName
	 * @return            : AuthManagementVO
	***************************************************/
	AuthManagementVO selectRoleInfoByRoleName(@Param("roleName") String roleName);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 추가
	 * @title               : insertRoleGroupInfo
	 * @return            : int
	***************************************************/
	int insertRoleGroupInfo(@Param("authVO") AuthManagementVO authVO);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 삭제
	 * @title               : deleteRoleGroupInfoByRoleId
	 * @return            : int
	***************************************************/
	int deleteRoleGroupInfoByRoleId(@Param("id") Integer id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 하위 코드 삭제
	 * @title               : deleteRoleDetailInfoByRoleId
	 * @return            : void
	***************************************************/
	void deleteRoleDetailInfoByRoleId(@Param("id") Integer id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 수정 시 유효성 검사
	 * @title               : selectRoleInfoByRoleId
	 * @return            : AuthManagementVO
	***************************************************/
	AuthManagementVO selectRoleInfoByRoleId(@Param("id") int id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 수정
	 * @title               : updateRoleGroupInfoByRoleId
	 * @return            : int
	***************************************************/
	int updateRoleGroupInfoByRoleId(@Param("authVO") AuthManagementVO authVO);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 디테일 수정
	 * @title               : insertRoleDetailInfoByRoleId
	 * @return            : int
	***************************************************/
	int insertRoleDetailInfoByRoleId(@Param("authVO")AuthManagementVO authVO , @Param("activeYn") List<String> activeYn);
	
}
