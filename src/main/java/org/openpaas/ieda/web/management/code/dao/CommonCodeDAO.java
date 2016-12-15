package org.openpaas.ieda.web.management.code.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.web.management.code.dto.CommonCodeDTO;

public interface CommonCodeDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 추가
	 * @title               : createCode
	 * @return            : int
	***************************************************/
	int insertCode(@Param("code")CommonCodeVO code); 
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 수정
	 * @title               : updateCode
	 * @return            : int
	***************************************************/
	int updateCode(@Param("code")CommonCodeVO code); 
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 삭제
	 * @title               : deleteCode
	 * @return            : int
	***************************************************/
	int deleteCode(@Param("codeIdx")Integer codeIdx);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 추가시 동일 이름 존재 여부 확인
	 * @title               : selectCodeName
	 * @return            : List<CommonCodeVO>
	***************************************************/
	List<CommonCodeVO> selectCodeName(@Param("codeName")String codeName);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 및 코드 값 중복 체크
	 * @title               : selectCodeValueCheck
	 * @return            : int
	***************************************************/
	int selectCodeValueCheck(@Param("code") CommonCodeDTO.Regist code);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 상세 조회
	 * @title               : selectCodeIdx
	 * @return            : CommonCodeVO
	***************************************************/
	CommonCodeVO selectCodeIdx(@Param("CodeIdx")Integer codeIdx);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 조회
	 * @title               : selectParentCodeIsNull
	 * @return            : List<CommonCodeVO>
	***************************************************/
	List<CommonCodeVO> selectParentCodeIsNull();
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Max sortOrder 조회
	 * @title               : selectMaxSortOrder
	 * @return            : Integer
	***************************************************/
	Integer selectMaxSortOrder(@Param("parentCode")String parentCode, @Param("subGroupCode")String subGroupCode);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스템셀 정보 목록을 조회
	 * @title               : selectParentCodeAndSubGroupCode
	 * @return            : List<CommonCodeVO>
	***************************************************/
	List<CommonCodeVO> selectParentCodeAndSubGroupCode(@Param("parentCode")String parentCode, @Param("subGroupCode")String subGroupCode, @Param("type")String type);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 설정 정보를 조회
	 * @title               : selectCommonCodeList
	 * @return            : List<CommonCodeVO>
	***************************************************/
	List<CommonCodeVO> selectCommonCodeList(@Param("parentCode")String parentcode);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 공통 배포 상태 정보 조회
	 * @title               : selectCommonCodeByCodeName
	 * @return            : CommonCodeVO
	***************************************************/
	CommonCodeVO selectCommonCodeByCodeName(@Param("parentCode")String parentCode, @Param("subGroupCode")String subGroupCode, @Param("codeName") String codeName);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 릴리즈 유형 코드 조회
	 * @title               : selectReleaseTypeList
	 * @return            : List<String>
	***************************************************/
	List<String> selectReleaseTypeList(@Param("parentCodeName") String parentCodeName);
}
