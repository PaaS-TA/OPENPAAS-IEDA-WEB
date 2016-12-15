package org.openpaas.ieda.web.common.dao;

import org.apache.ibatis.annotations.Param;

public interface CommonDAO {

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 템플릿 정보 조회
	 * @title               : getManifetTemplate
	 * @return            : ManifestTemplateVO
	***************************************************/
	ManifestTemplateVO getManifetTemplate(@Param("iaas") String iaas, @Param("releaseVersion") String ReleaseVersion, 
			@Param("deployType") String deployType, @Param("releaseType") String releaseType);
	
	
	
}
