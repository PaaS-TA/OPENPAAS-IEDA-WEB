package org.openpaas.ieda.web.management.code.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.ieda.common.CommonException;
import org.openpaas.ieda.web.common.dto.SessionInfoDTO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeDAO;
import org.openpaas.ieda.web.management.code.dao.CommonCodeVO;
import org.openpaas.ieda.web.management.code.dto.CommonCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CommonCodeService {
	
	@Autowired private CommonCodeDAO dao;

	private static final String parentCode = "10000";
	private final static String SUB_CODE_TYPE_3 = "3";
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 서브그룹 정보 목록을 조회 
	 * @title               : getSubGroupCodeList
	 * @return            : List<CommonCodeVO>
	***************************************************/
	public List<CommonCodeVO> getSubGroupCodeList(String parentCode, String subGroupCode, String type) {
		List<CommonCodeVO> list = dao.selectParentCodeAndSubGroupCode(parentCode, subGroupCode, type);
		
		if (SUB_CODE_TYPE_3.equals(type) && (list == null || list.isEmpty() || list.size() == 0 )) {
				throw new CommonException("notfound.code.exception",
						" 해당하는 하위 코드가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		
		return list;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 그룹 등록
	 * @title               : createCode
	 * @return            : int
	***************************************************/
	public int createCode(CommonCodeDTO.Regist createCodeDto) {
		// 해당 코드가 존재하는지 확인한다
		int codeValCheck = dao.selectCodeValueCheck(createCodeDto);
		if( codeValCheck > 0 ){  
			throw new CommonException("existCode.code.exception",
					"이미 등록되어 있는 코드 그룹값입니다.", HttpStatus.CONFLICT);
		}

		CommonCodeVO commonCode = new CommonCodeVO();
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		if(createCodeDto.getCodeIdx() != null){
			commonCode.setCodeIdx(createCodeDto.getCodeIdx());
		}
		commonCode.setCodeName(createCodeDto.getCodeName());
		commonCode.setCodeValue(createCodeDto.getCodeValue());
		commonCode.setCodeDescription(createCodeDto.getCodeDescription());
		commonCode.setSortOrder(0);
		commonCode.setCreateUserId(sessionInfo.getUserId());
		commonCode.setUpdateUserId(sessionInfo.getUserId());

		// 입력된 코드 그룹 정보를 데이터베이스에 저장한다.
		return dao.insertCode(commonCode);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 등록
	 * @title               : createSubCode
	 * @return            : void
	***************************************************/
	public void createSubCode(CommonCodeDTO.Regist createCodeDto){
		// 해당 코드가 존재하는지 확인한다
		List<CommonCodeVO> codeList = dao.selectCodeName(createCodeDto.getCodeName());
		int codeValCheck = dao.selectCodeValueCheck(createCodeDto);
		if(codeList.size()!=0){
			if ( codeList.get(0).getParentCode() == null || codeList.get(0).getParentCode().isEmpty() ) {
				throw new CommonException("notfound.code.exception",
						"코드 그룹을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
			}else if( codeValCheck > 0 ){  
				throw new CommonException("existCode.code.exception",
						"이미 등록되어 있는 코드값 입니다.", HttpStatus.CONFLICT);
			}
		}
		
		Integer maxSorderOrder = 0;
		if(!StringUtils.isEmpty(createCodeDto.getSubGroupCode()) || createCodeDto.getSubGroupCode() != null ){
			maxSorderOrder = dao.selectMaxSortOrder(createCodeDto.getParentCode(), createCodeDto.getSubGroupCode());
		}
		
		CommonCodeVO commonCode = new CommonCodeVO();
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
		
		commonCode.setParentCode(createCodeDto.getParentCode());
		commonCode.setSubGroupCode(createCodeDto.getSubGroupCode());
		commonCode.setCodeName(createCodeDto.getCodeName());
		commonCode.setCodeValue(createCodeDto.getCodeValue());
		commonCode.setCodeNameKR(createCodeDto.getCodeNameKR());
		commonCode.setSortOrder(maxSorderOrder);
		commonCode.setCodeDescription(createCodeDto.getCodeDescription());
		commonCode.setCreateUserId(sessionInfo.getUserId());
		commonCode.setUpdateUserId(sessionInfo.getUserId());
		
		dao.insertCode(commonCode);
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 수정
	 * @title               : updateCode
	 * @return            : int
	***************************************************/
	public int updateCode(CommonCodeDTO.Regist updateCodeDto) {
		
		// 해당 코드가 존재하는지 확인한다
		CommonCodeVO commonCode = dao.selectCodeIdx(updateCodeDto.getCodeIdx());
		SessionInfoDTO sessionInfo = new SessionInfoDTO();
				
		if ( commonCode == null )
			throw new CommonException("notfound.code_update.exception",
					"코드가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		
		// 입력된 코드 정보를 데이터베이스에 저장한다.
		commonCode.setCodeName(updateCodeDto.getCodeName());
		commonCode.setCodeValue(updateCodeDto.getCodeValue());
		commonCode.setCodeNameKR(updateCodeDto.getCodeNameKR());
		commonCode.setCodeDescription(updateCodeDto.getCodeDescription());
		commonCode.setParentCode(updateCodeDto.getParentCode());
		commonCode.setSubGroupCode(updateCodeDto.getSubGroupCode());
		commonCode.setUpdateUserId(sessionInfo.getUserId());
		
		return dao.updateCode(commonCode);
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 코드 삭제
	 * @title               : deleteCode
	 * @return            : void
	***************************************************/
	@Transactional
	public void deleteCode(int codeIdx) {
		// 해당 코드가 존재하는지 확인한다
		CommonCodeVO commonCode = dao.selectCodeIdx(codeIdx);
		if ( commonCode == null ) {
			throw new CommonException("notfound.code.exception", "해당 코드가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
		}
		try{
			dao.deleteCode(codeIdx);// 코드를 삭제를 수행한다.		
		} catch (Exception e){
			throw new CommonException("sql.code.exception",
				"삭제 중 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 상세 권한 목록 조회
	 * @title               : getCommonCodeList
	 * @return            : List<CommonCodeVO>
	***************************************************/
	public List<CommonCodeVO> getCommonCodeList() {
		List<CommonCodeVO> list = dao.selectCommonCodeList(parentCode);
		if (list == null || list.isEmpty() || list.size() == 0 ) {
			throw new CommonException("notfound.code.exception",
					"상세 권한 코드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		return list;
	}
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : 국가 코드 조회(KR 기준 정렬)
	 * @title               : getCountryCodeList
	 * @return            : List<CommonCodeVO>
	***************************************************/
	public List<CommonCodeVO> getCountryCodeList(String parentCode){
		List<CommonCodeVO> list = dao.selectCountryCodeList(parentCode);
		if ( list.size() == 0 ) {
			throw new CommonException("notfound.code.exception",
					"국가 코드를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		return list;
	}
	
		
}
