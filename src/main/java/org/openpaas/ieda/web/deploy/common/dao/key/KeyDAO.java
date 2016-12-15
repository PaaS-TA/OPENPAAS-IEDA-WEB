package org.openpaas.ieda.web.deploy.common.dao.key;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface KeyDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼에 따른 각 key 목록 조회
	 * @title               : selectKeyInfoLIst
	 * @return            : List<KeyVO>
	***************************************************/
	List<KeyVO> selectKeyInfoLIst(@Param("id")int id, @Param("deployType")String deployType);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼에 따른 각 key 정보 저장
	 * @title               : insertKeyInfo
	 * @return            : void
	***************************************************/
	void insertKeyInfo(@Param("key") KeyVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼에 따른 각  key 정보 수정
	 * @title               : updateKeyInfo
	 * @return            : void
	***************************************************/
	void updateKeyInfo(@Param("key") KeyVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 플랫폼에 따른 key 정보 삭제
	 * @title               : deleteKeyInfo
	 * @return            : void
	***************************************************/
	void deleteKeyInfo(@Param("id")int id, @Param("deployType") String deployType);
}
