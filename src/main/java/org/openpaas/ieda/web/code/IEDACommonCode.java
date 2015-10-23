package org.openpaas.ieda.web.code;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity(name="IEDA_COMMON_CODE")
@Getter
@Setter
public class IEDACommonCode {
	
	@Id @GeneratedValue
	private Integer codeIdx;
	
	private String  codeName;
	
	private String  codeValue;
	
	private String  codeDescription;
	
	private Integer sortOrder;
	
	private Integer parentCodeIdx;
}
