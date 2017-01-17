package org.openpaas.ieda.web.config.stemcell.dao;
 
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO;
import org.openpaas.ieda.web.config.stemcell.dto.StemcellManagementDTO.Delete;
 
public interface StemcellManagementDAO {
	
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : 전체 Public Stemcell 목록 조회
     * @title               : selectPublicStemcellList
     * @return            : List<StemcellManagementVO>
    ***************************************************/
    List<StemcellManagementVO> selectPublicStemcellList();
    
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 파일 이름 기준 Public Stemcell 목록 조회
	 * @title         : selectPublicStemcell
	 * @return        : StemcellManagementVO
	***************************************************/
	StemcellManagementVO selectPublicStemcell(@Param("stemcellFileName") String stemcellFileName);

	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 정보 저장
	 * @title         : insertPublicStemcell
	 * @return        : void
	***************************************************/
	void insertPublicStemcell(@Param("dto") StemcellManagementDTO.Regist dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 정보 수정
	 * @title         : updatePublicStemcell
	 * @return        : void
	***************************************************/
	void updatePublicStemcell(@Param("dto") StemcellManagementDTO.Regist dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 아이디 기준 스템셀 목록 조회
	 * @title         : selectPublicStemcellById
	 * @return        : StemcellManagementVO
	***************************************************/
	StemcellManagementVO selectPublicStemcellById(@Param("id") Integer id);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 아이디 기준 스템셀 정보 수정
	 * @title         : updatePublicStemcellById
	 * @return        : void
	***************************************************/
	void updatePublicStemcellById(@Param("dto") StemcellManagementDTO.Regist dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 정보 삭제
	 * @title         : deletePublicStemcell
	 * @return        : void
	***************************************************/
	void deletePublicStemcell(@Param("dto") Delete dto);
	
	/***************************************************
	 * @project       : Paas 플랫폼 설치 자동화
	 * @description   : IaaS 기준 다운로드된 스템셀 정보 조회
	 * @title         : selectLocalStemcellList
	 * @return        : List<StemcellManagementVO>
	***************************************************/
	List<StemcellManagementVO> selectLocalStemcellList(@Param("iaas") String iaas);
}