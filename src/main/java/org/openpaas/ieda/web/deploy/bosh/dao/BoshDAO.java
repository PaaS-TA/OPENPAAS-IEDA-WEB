package org.openpaas.ieda.web.deploy.bosh.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.openpaas.ieda.web.config.setting.dao.DirectorConfigVO;

public interface BoshDAO {

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 정보 목록을 조회
	 * @title               : selectBoshListByIaasType
	 * @return            : List<BoshVO>
	***************************************************/
	List<BoshVO> selectBoshListByIaasType(@Param("iaas") String iaas);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 상세 조회
	 * @title               : selectBoshDetailInfo
	 * @return            : BoshVO
	***************************************************/
	BoshVO selectBoshDetailInfo(@Param("id")int boshId);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh OPENSTACK 정보 저장
	 * @title               : saveBoshInfo
	 * @return            : void
	***************************************************/
	void saveBoshInfo(@Param("bosh")BoshVO config);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 설치 정보 저장
	 * @title               : updateBoshInfo
	 * @return            : void
	***************************************************/
	void updateBoshInfo(@Param("bosh")BoshVO vo);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : Bosh 단순 레코드 삭제
	 * @title               : deleteBoshInfoRecord
	 * @return            : void
	***************************************************/
	void deleteBoshInfoRecord(@Param("id")int boshId);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   :  Bosh 리소스 정보 상세 조회
	 * @title               : selectResourceInfoById
	 * @return            : BoshVO
	***************************************************/
	BoshVO selectResourceInfoById(@Param("id")int id, @Param("deployType")String deployType);

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 스냅샷 사용 여부
	 * @title               : selectSnapshotInfo
	 * @return            : int
	***************************************************/
	int selectSnapshotInfo( @Param("director")DirectorConfigVO  director);
	
	
}
