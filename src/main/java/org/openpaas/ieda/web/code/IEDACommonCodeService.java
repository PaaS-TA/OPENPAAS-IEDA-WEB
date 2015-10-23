package org.openpaas.ieda.web.code;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.openpaas.ieda.common.IEDACommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class IEDACommonCodeService {
	
	@Autowired
	private IEDACommonCodeRepository repository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public IEDACommonCode getCode(int codeIdx) {
		IEDACommonCode commonCode = repository.findOne(codeIdx);
		
		if ( commonCode == null ) {
			throw new IEDACommonException("notfound.code.exception",
					"코드 [" + codeIdx + "]에 해당하는 코드가 존재하지 않습니다.",
					HttpStatus.BAD_REQUEST);
		}
		
		return commonCode;
	}
	
	public List<IEDACommonCode> getChildCodeList(int parentCodeIdx) {
		List<IEDACommonCode> list = repository.findByParentCodeIdxOrderBySortOrderAsc(parentCodeIdx);
		
		log.info("getChildCodeList() list size : " + list.size());
		
		if ( list.isEmpty() || list.size() == 0 ) {
			throw new IEDACommonException("notfound.code.exception",
					"코드 [" + parentCodeIdx + "]에 해당하는 하위 코드가 존재하지 않습니다.",
					HttpStatus.BAD_REQUEST);
		}
		
		return list;
	}
	
	
	/*	public IEDACommonCode createCode(IEDACommonCodeDto.Create dto) {
	
	// 유일한 코드키가 아닌 경우 
	String codename = dto.getCodeName();
	if (repository.findByCodeKey(dto.getCodeKey()) != null ) {
		log.debug("code duplicated exception {}", codename);
		throw new IEDACommonException("duplicated.code.exception",
				"코드키 [" + dto.getCodeKey() + "]가 이미 등록되어 있습니다.");
	}
	
	// TODO: 코드 추가 시 정렬 순서 로직 추가
	IEDACommonCode commonCode = new IEDACommonCode();
	commonCode.setCodeKey(dto.getCodeKey());
	commonCode.setCodeName(dto.getCodeName());
	commonCode.setCodeValue(dto.getCodeValue());
	commonCode.setCodeDescription(dto.getCodeDescription());
	commonCode.setParentCodeKey(dto.getParentCodeKey());
	
	IEDACommonCode commonCode1 = modelMapper.map(dto, IEDACommonCode.class);
			
	return repository.save(commonCode1);
}*/

/*	public IEDACommonCode updateCode(String codeKey, IEDACommonCodeDto.Update updateDto ) {
	
	IEDACommonCode commonCode = repository.findByCodeKey(codeKey);
	
	commonCode.setCodeName(updateDto.getCodeName());
	commonCode.setCodeValue(updateDto.getCodeValue());
	commonCode.setCodeDescription(updateDto.getCodeDescription());
	commonCode.setSortOrder(updateDto.getSortOrder());
	commonCode.setParentCodeKey(updateDto.getParentCodeKey());
	
	return repository.save(commonCode); 
}*/
	
/*	public void deleteCode(String codeKey) {
		repository.delete(getCode(codeKey));
	}*/

}
