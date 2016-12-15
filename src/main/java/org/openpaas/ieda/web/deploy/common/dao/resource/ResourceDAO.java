package org.openpaas.ieda.web.deploy.common.dao.resource;

import org.apache.ibatis.annotations.Param;

public interface ResourceDAO {

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 플랫폼 리소스 정보 조회
	 * @title               : selectResourceInfo
	 * @return            : ResourceVO
	***************************************************/
	ResourceVO selectResourceInfo(@Param("id")Integer id, @Param("deployType")String deployType);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 플랫폼 리소스 정보 저장
	 * @title               : insertResourceInfo
	 * @return            : void
	***************************************************/
	void insertResourceInfo(@Param("resource") ResourceVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 
	 * @title               : updateResourceInfo
	 * @return            : void
	***************************************************/
	void updateResourceInfo(@Param("resource") ResourceVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 해당 플랫폼 리소스 정보 수정
	 * @title               : deleteResourceInfo
	 * @return            : void
	***************************************************/
	void deleteResourceInfo(@Param("id")Integer id, @Param("deployType")String deployType);
}
