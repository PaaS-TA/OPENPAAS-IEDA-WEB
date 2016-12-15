package org.openpaas.ieda.web.deploy.common.dao.network;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface NetworkDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 유형에 따른 네트워크 정보 조회
	 * @title               : selectNetworkList
	 * @return            : List<NetworkVO>
	***************************************************/
	List<NetworkVO> selectNetworkList(@Param("id")int id, @Param("deployType")String deployType);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 유형에 따른 네트워크 목록 저장
	 * @title               : insertNetworkList
	 * @return            : void
	***************************************************/
	void insertNetworkList(@Param("networks") List<NetworkVO> networks);
		
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 배포 유형에 따른 네트워크 레코드 삭제
	 * @title               : deleteNetworkInfoRecord
	 * @return            : void
	***************************************************/
	void deleteNetworkInfoRecord(@Param("id")Integer id, @Param("deployType")String deployType);
}
