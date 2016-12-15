package org.openpaas.ieda.web.deploy.bootstrap.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;

public interface BootstrapDAO {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 전체 목록 조회
	 * @title               : selectBootstrapList
	 * @return            : List<BootstrapVO>
	***************************************************/
	List<BootstrapVO> selectBootstrapList();
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 수정 시 상세조회
	 * @title               : selectBootstrapInfo
	 * @return            : BootstrapVO
	***************************************************/
	BootstrapVO selectBootstrapInfo(@Param("id")int id);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BootStrap 정보 저장
	 * @title               : insertBootStrapInfo
	 * @return            : int
	***************************************************/
	int insertBootStrapInfo(@Param("bootstrap")BootstrapVO vo);
	
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BootStrap 정보 수정
	 * @title               : updateBootStrapInfo
	 * @return            : int
	***************************************************/
	int updateBootStrapInfo(@Param("bootstrap")BootstrapVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : BootStrap 정보 삭제
	 * @title               : deleteBootstrapInfo
	 * @return            : void
	***************************************************/
	void deleteBootstrapInfo(@Param("id")int id);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 사용 여부
	 * @title               : selectSnapshotInfo
	 * @return            : int
	***************************************************/
	int selectSnapshotInfo( @Param("director")DirectorConfigVO  director);
}
