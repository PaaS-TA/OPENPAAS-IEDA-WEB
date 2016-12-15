package org.openpaas.ieda.web.deploy.cfDiego.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CfDiegoDAO {

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : iaas별 CfDiego 통합 설치 정보 목록 조회
	 * @title               : selectCfDiegoList
	 * @return            : List<CfDiegoVO>
	***************************************************/
	List<CfDiegoVO> selectCfDiegoList(@Param("iaas") String iaas);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : id와 일치하는 CF & Diego 정보 상세 조회
	 * @title               : selectCfDiegoInfoById
	 * @return            : CfDiegoVO
	***************************************************/
	CfDiegoVO selectCfDiegoInfoById(@Param("id") int id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  CF 및 Diego id와 일치하는 CF & Diego 통합 정보 상세 조회
	 * @title               : selectCfDiegoInfoByPlaform
	 * @return            : CfDiegoVO
	***************************************************/
	CfDiegoVO selectCfDiegoInfoByPlaform(@Param("platform") String platform,@Param("id") Integer id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 통합 설치 정보 저장
	 * @title               : insertCfDiegoInfo
	 * @return            : void
	***************************************************/
	void insertCfDiegoInfo(@Param("cfDiego") CfDiegoVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 통합 설치 정보 수정
	 * @title               : updateCfDiegoInfo
	 * @return            : void
	***************************************************/
	void updateCfDiegoInfo(@Param("cfDiego") CfDiegoVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF & Diego 정보 삭제
	 * @title               : deleteCfDiegoInfo
	 * @return            : void
	***************************************************/
	void deleteCfDiegoInfo(@Param("id") int id);
	
}
