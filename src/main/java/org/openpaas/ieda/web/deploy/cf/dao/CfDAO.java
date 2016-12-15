package org.openpaas.ieda.web.deploy.cf.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface CfDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치 정보 목록 조회
	 * @title               : selectCfList
	 * @return            : List<CfVO>
	***************************************************/
	List<CfVO> selectCfList(@Param("iaas")String iaas, @Param("platform")String platform);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치 정보 상세 조회
	 * @title               : selectCfInfoById
	 * @return            : CfVO
	***************************************************/
	CfVO selectCfInfoById(@Param("id")int id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  CF 키 정보 상세 조회(Uaa, Consul, Blobstore, Hm9000)
	 * @title               : selectCfKeyInfoById
	 * @return            : CfVO
	***************************************************/
	CfVO selectCfKeyInfoById(@Param("id")Integer id, @Param("deployType") String deployType, @Param("keyType") Integer keyType);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 리소스 정보 상세 조회
	 * @title               : selectCfResourceInfoById
	 * @return            : CfVO
	***************************************************/
	CfVO selectCfResourceInfoById(@Param("id")Integer id, @Param("deployType")String deployType);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치 정보 저장
	 * @title               : insertCfInfo
	 * @return            : void
	***************************************************/
	void insertCfInfo(@Param("cf")CfVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 설치 정보 수정
	 * @title               : updateCfInfo
	 * @return            : void
	***************************************************/
	void updateCfInfo(@Param("cf")CfVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF 단순 레코드 삭제 
	 * @title               : deleteCfInfoRecord
	 * @return            : void
	***************************************************/
	void deleteCfInfoRecord(@Param("id")int id);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : CF Manifest Template 조회
	 * @title               : selectDeploymentFilebyDeploymentName
	 * @return            : CfVO
	***************************************************/
	CfVO selectDeploymentFilebyDeploymentName(@Param("cfDeploymentName") String cfDeployName);
	
}
