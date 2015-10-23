package org.openpaas.ieda.web.code;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDACommonCodeRepository extends JpaRepository<IEDACommonCode, Integer>{
	IEDACommonCode findByCodeName(String codename);
	List<IEDACommonCode> findByParentCodeIdxOrderBySortOrderAsc(Integer parentCodeIdx);
}
