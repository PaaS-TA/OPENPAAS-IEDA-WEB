package org.openpaas.ieda.web.config.systemRelease.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.web.config.systemRelease.dto.ReleaseManagementDTO;

public interface ReleaseManagementDAO {
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 유형을 기준으로 정렬하여 시스템 릴리즈 정보 목록 조회
	 * @title         : selectSystemReleaseList
	 * @return        : List<ReleaseManagementVO>
	***************************************************/
	List<ReleaseManagementVO> selectSystemReleaseList(@Param("option")String option);

	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 파일명에 따른 시스템 릴리즈 정보 상세 조회
	 * @title         : selectSystemRelease
	 * @return        : ReleaseManagementVO
	***************************************************/
	ReleaseManagementVO selectSystemRelease(@Param("fileName")String fileName);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : Id에 따른 시스템 릴리즈 정보 상세 조회
	 * @title         : selectSystemReleaseById
	 * @return        : ReleaseManagementVO
	***************************************************/
	ReleaseManagementVO selectSystemReleaseById(@Param("id")Integer id);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 정보 저장
	 * @title         : insertSystemRelease
	 * @return        : void
	***************************************************/
	void insertSystemRelease(@Param("release")ReleaseManagementDTO.Regist dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : Id에 따른 시스템 릴리즈 파일명에 따른 정보 수정
	 * @title         : updateSystemRelease
	 * @return        : void
	***************************************************/
	void updateSystemRelease(@Param("release")ReleaseManagementDTO.Regist dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 id에 따른 정보 수정
	 * @title         : updateSystemReleaseById
	 * @return        : int
	***************************************************/
	int updateSystemReleaseById(@Param("release")ReleaseManagementDTO.Regist dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 시스템 릴리즈 정보 삭제
	 * @title         : deleteSystemRelase
	 * @return        : void
	***************************************************/
	void deleteSystemRelase(@Param("release")ReleaseManagementDTO.Delete dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 공통 릴리즈 콤보
	 * @title         : selectLocalReleaseList
	 * @return        : List<String>
	***************************************************/
	List<String> selectLocalReleaseList(@Param("type")String type, @Param("iaas")String iaas);
	
}
