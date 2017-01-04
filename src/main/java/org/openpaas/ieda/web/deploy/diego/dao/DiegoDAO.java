package org.openpaas.ieda.web.deploy.diego.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface DiegoDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 목록 정보를 조회
	 * @title               : selectDiegoListInfo
	 * @return            : List<DiegoVO>
	***************************************************/
	List<DiegoVO> selectDiegoListInfo(@Param("iaasType")String iaasType);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 상세 조회
	 * @title               : selectDiegoInfo
	 * @return            : DiegoVO
	***************************************************/
	DiegoVO selectDiegoInfo(@Param("id")int id);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 기본정보 저장
	 * @title               : insertDiegoDefaultInfo
	 * @return            : void
	***************************************************/
	void insertDiegoDefaultInfo(@Param("diego")DiegoVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 정보 저장
	 * @title               : updateDiegoDefaultInfo
	 * @return            : void
	***************************************************/
	void updateDiegoDefaultInfo(@Param("diego")DiegoVO vo);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego 단순 레코드 삭제
	 * @title               : deleteDiegoInfoRecord
	 * @return            : void
	***************************************************/
	void deleteDiegoInfoRecord(@Param("id")int id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Diego Key 정보 상세 조회
	 * @title               : selectDiegoKeyInfoById
	 * @return            : DiegoVO
	***************************************************/
	DiegoVO selectDiegoKeyInfoById(@Param("id")int id, @Param("deployType")String deployType, @Param("keyType")int keyType);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : DIEGO 리소스 정보 상세 조회
	 * @title               : selectResourceInfoById
	 * @return            : DiegoVO
	***************************************************/
	DiegoVO selectResourceInfoById(@Param("id")int id, @Param("deployType")String deployType);
}
