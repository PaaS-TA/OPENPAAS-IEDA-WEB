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
		
		if ( list.isEmpty() || list.size() == 0 ) {
			throw new IEDACommonException("notfound.code.exception",
					"코드 [" + parentCodeIdx + "]에 해당하는 하위 코드가 존재하지 않습니다.",
					HttpStatus.BAD_REQUEST);
		}
		
		return list;
	}
}
