package org.openpaas.ieda.web.management.auth.service;



import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.management.auth.dao.AuthManagementDAO;
import org.openpaas.ieda.web.management.auth.dao.AuthManagementVO;
import org.openpaas.ieda.web.management.auth.dto.AuthManagementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
@Service
public class AuthManagementService {
	
	@Autowired private AuthManagementDAO dao;
	
	static final String parentCode = "10000";
	private final static Logger LOG = LoggerFactory.getLogger(AuthManagementService.class);
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 리스트 조회
	 * @title               : getRoleGroupList
	 * @return            : List<AuthManagementVO>
	***************************************************/
	public List<AuthManagementVO> getRoleGroupList() {				
		List<AuthManagementVO> result = dao.selectRoleGroupList();		
		return result;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹의 하부 권한 코드 조회
	 * @title               : getRoleDetailList
	 * @return            : List<HashMap<String,Object>>
	***************************************************/
	public List<HashMap<String,Object>> getRoleDetailList(int roleId) {
			List<HashMap<String, Object>> list = dao.selectRoleDetailListByRoleId(roleId,parentCode);
		return list;		
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 저장 데이터(권한 그룹 이름, 설명, 권한 코드)를 VO에 저장 한 뒤 DB 테이블에 저장
	 * @title               : saveRoleInfo
	 * @return            : boolean
	***************************************************/
	public boolean saveRoleInfo(AuthManagementDTO.Regist dto)  {
		
			AuthManagementVO auth = dao.selectRoleInfoByRoleName(dto.getRoleName());
			if ( auth != null) {
				throw new CommonException("conflict.auth.exception", "이미 등록되어 있는 권한 그룹 명입니다.", HttpStatus.CONFLICT);
			}			
			AuthManagementVO authVO = new AuthManagementVO();
			SessionInfoDTO sessionInfo = new SessionInfoDTO();
			if(dto.getRoleId()!=null){
				authVO.setRoleId(Integer.parseInt(dto.getRoleId()));
			}
			authVO.setRoleName(dto.getRoleName());
			authVO.setRoleDescription(dto.getRoleDescription());
			authVO.setCreateUserId(sessionInfo.getUserId());
			authVO.setUpdateUserId(sessionInfo.getUserId());
			try{
				if(dao.insertRoleGroupInfo(authVO)==1) return true;
			}catch (Exception e) {
				throw new CommonException("sqlExcepion.auth.exception",
						"권한 그룹 추가 중 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		if(LOG.isInfoEnabled()){ LOG.info("====================================> 권한 추가 삽입 완료 이동"); }
		return true;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 하위 권한 코드 삭제 -> 권한 그룹 삭제
	 * @title               : deleteRole
	 * @return            : boolean
	***************************************************/
	public boolean deleteRole(Integer roleId) {
		AuthManagementVO authVO = dao.selectRoleInfoByRoleId(roleId);
		if ( authVO == null )
			throw new CommonException("notfound_rold_id.auth_delete.exception",
					"해당 권한 코드가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		try{
			dao.deleteRoleDetailInfoByRoleId(roleId);
			dao.deleteRoleGroupInfoByRoleId(roleId);					
		}catch(Exception e){
			throw new CommonException("sqlException.auth.exception",
					"권한 그룹 삭제 중 에러가 발생 했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return true;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 권한 그룹 하위 권한 코드 삭제 -> 권한 그룹 수정 -> 권한 그룹 하위 권한 코드 재 등록
	 * @title               : updateRole
	 * @return            : boolean
	***************************************************/
	public boolean updateRole(int roleId, AuthManagementDTO.Regist updateAuthDto) {
		AuthManagementVO authVO = dao.selectRoleInfoByRoleId(roleId);
		if ( authVO == null )
			throw new CommonException("notfound_rold_id.auth_update.exception",
					"해당 권한 코드가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		AuthManagementVO auth = new AuthManagementVO();
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		auth.setRoleId(roleId);
		auth.setRoleName(updateAuthDto.getRoleName());
		auth.setRoleDescription(updateAuthDto.getRoleDescription());
		auth.setCreateUserId(authVO.getCreateUserId());
		auth.setCreateDate(authVO.getCreateDate());
		auth.setUpdateUserId(sessionInfo.getUserId());		
		try{		
			if(dao.updateRoleGroupInfoByRoleId(auth)==1){
				return true;
			}
		}catch(Exception e){
			throw new CommonException("sqlException.auth.exception",
					" 권한 그룹 수정 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return true;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 상세 권한 추가
	 * @title               : saveRoleDetail
	 * @return            : void
	***************************************************/
	public void saveRoleDetail(int roleId, AuthManagementDTO.Regist dto) throws SQLException {
		AuthManagementVO authVO = dao.selectRoleInfoByRoleId(roleId);
		AuthManagementVO auth = new AuthManagementVO();
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		if(dto.getRoleId()!=null){
			auth.setRoleId(roleId);
		}
		auth.setRoleId(roleId);
		auth.setActiveYn(dto.getActiveYn());	
		auth.setCreateUserId(sessionInfo.getUserId());
		auth.setUpdateUserId(sessionInfo.getUserId());
		if(authVO==null){
			//여기서 롤 아이디로 검사를 한번 더 해준다..
			if(auth.getActiveYn().size()!=0){
			dao.insertRoleDetailInfoByRoleId(auth, auth.getActiveYn());
			}
		}else{
			dao.deleteRoleDetailInfoByRoleId(auth.getRoleId());
			if(auth.getActiveYn().size()!=0)
			dao.insertRoleDetailInfoByRoleId(auth, auth.getActiveYn());
		}
	}
	
}
