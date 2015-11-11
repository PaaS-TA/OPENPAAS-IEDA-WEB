package org.openpaas.ieda.web.code;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity(name="IEDA_COMMON_CODE")
@Data
public class IEDACommonCode {
	
	@Id @GeneratedValue
	private Integer codeIdx;
	
	private String  codeName;
	
	private String  codeValue;
	
	private String  codeDescription;
	
	private Integer sortOrder;
	
	private Integer parentCodeIdx;
}
