package org.openpaas.ieda.web.information.manifest.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.web.information.manifest.dto.ManifestListDTO;

public interface ManifestDAO {

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 목록 조회
	 * @title               : selectManifestList
	 * @return            : List<ManifestVO>
	***************************************************/
	List<ManifestVO> selectManifestList();
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 상세 조회
	 * @title               : selectManifestInfo
	 * @return            : ManifestVO
	***************************************************/
	ManifestVO selectManifestInfo(@Param("id") int id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 저장
	 * @title               : insertManifestInfo
	 * @return            : void
	***************************************************/
	void insertManifestInfo(@Param("manifest") ManifestListDTO dto);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 수정
	 * @title               : updateManifestInfo
	 * @return            : void
	***************************************************/
	void updateManifestInfo(@Param("manifest") ManifestVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Manifest 정보 삭제
	 * @title               : deleteManifestInfo
	 * @return            : void
	***************************************************/
	void deleteManifestInfo(@Param("id") int id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포명 조건에 따른 Manifest 정보 조회
	 * @title               : selectManifestInfoByDeployName
	 * @return            : ManifestVO
	***************************************************/
	ManifestVO selectManifestInfoByDeployName(@Param("deploymentName") String deploymentName);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스 팩의 Manifest 파일 검색
	 * @title               : selectManifestSearchList
	 * @return            : List<ManifestVO>
	***************************************************/
	List<ManifestVO> selectManifestSearchList(@Param("searchVal") String searchVal);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포명과 Id 조건에 따른 Manifest 정보 조회
	 * @title               : selectManifestInfoByDeployNameANDId
	 * @return            : ManifestVO
	***************************************************/
	ManifestVO selectManifestInfoByDeployNameANDId(@Param("deploymentName") String deploymentName, @Param("id") int id);
	
}
