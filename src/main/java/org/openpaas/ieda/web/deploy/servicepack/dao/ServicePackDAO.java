package org.openpaas.ieda.web.deploy.servicepack.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ServicePackDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 전체 목록 조회
	 * @title               : selectServicePackInfo
	 * @return            : List<ServicePackVO>
	***************************************************/
	List<ServicePackVO> selectServicePackInfo(@Param("iaasAll") String iaas);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 상세 목록 정보 조회
	 * @title               : selectServicePackDetailInfo
	 * @return            : ServicePackVO
	***************************************************/
	ServicePackVO selectServicePackDetailInfo(@Param("id") int id);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 정보 저장
	 * @title               : insertServicePackInfo
	 * @return            : void
	***************************************************/
	void insertServicePackInfo(@Param("vo") ServicePackVO vo);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 상세 목록 수정
	 * @title               : updateServicePackInfo
	 * @return            : void
	***************************************************/
	void updateServicePackInfo(@Param("vo") ServicePackVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서비스팩 단순 레코드 삭제
	 * @title               : deleteServicePackInfoRecord
	 * @return            : void
	***************************************************/
	void deleteServicePackInfoRecord(@Param("id") int id);

	
}
