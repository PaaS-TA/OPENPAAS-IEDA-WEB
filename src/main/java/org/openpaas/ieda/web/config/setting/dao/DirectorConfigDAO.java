package org.openpaas.ieda.web.config.setting.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface DirectorConfigDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 추가
	 * @title               : createDirector
	 * @return            : int
	***************************************************/
	int insertDirector(@Param("director")DirectorConfigVO director);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 수정
	 * @title               : updateDirector
	 * @return            : int
	***************************************************/
	int updateDirector(@Param("director")DirectorConfigVO director);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 삭제
	 * @title               : deleteDirecotr
	 * @return            : int
	***************************************************/
	int deleteDirecotr(@Param("seq")Integer seq);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 전체 목록 조회
	 * @title               : selectDirectorConfig
	 * @return            : List<DirectorConfigVO>
	***************************************************/
	List<DirectorConfigVO> selectDirectorConfig();
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 기본 설치관리자 조회
	 * @title               : selectDirectorConfigByDefaultYn
	 * @return            : DirectorConfigVO
	***************************************************/
	DirectorConfigVO selectDirectorConfigByDefaultYn(@Param("defaultYn")String defaultYn);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 설정 조회
	 * @title               : selectDirectorConfigBySeq
	 * @return            : DirectorConfigVO
	***************************************************/
	DirectorConfigVO selectDirectorConfigBySeq(@Param("seq")Integer seq);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 설치관리자 존재 여부 확인
	 * @title               : selectDirectorConfigByDirectorUrl
	 * @return            : List<DirectorConfigVO>
	***************************************************/
	List<DirectorConfigVO>selectDirectorConfigByDirectorUrl(@Param("directorUrl")String directorUrl);
}
