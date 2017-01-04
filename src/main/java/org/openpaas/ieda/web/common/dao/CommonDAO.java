package org.openpaas.ieda.web.common.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CommonDAO {

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 템플릿 정보 조회
	 * @title               : getManifetTemplate
	 * @return            : ManifestTemplateVO
	***************************************************/
	ManifestTemplateVO selectManifetTemplate(@Param("iaas") String iaas, @Param("releaseVersion") String ReleaseVersion, 
			@Param("deployType") String deployType, @Param("releaseType") String releaseType);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포유형별 배포명 목록 조회
	 * @title               : getDeploymentNameByPlatform
	 * @return            : List<String>
	***************************************************/
	List<String> selectDeploymentNameByPlatform(@Param("platform") String platform);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 유형별 릴리즈 최적화 정보 목록 조회
	 * @title               : selectReleaseInfoByPlatform
	 * @return            : List<ManifestTemplateVO>
	***************************************************/
	List<ManifestTemplateVO> selectReleaseInfoByPlatform(@Param("deployType") String deployType, @Param("iaas") String iaas);
}
