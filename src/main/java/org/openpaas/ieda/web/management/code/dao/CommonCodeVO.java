package org.openpaas.ieda.web.management.code.dao;

import java.util.Date;

public class CommonCodeVO {
	
	private Integer recid;
	private Integer codeIdx; //code 순번
	private String  codeName; //code명
	private String  codeValue; //code값
	private String codeNameKR; //코드명(한글)
	private String  codeDescription; //code설명
	private String subGroupCode; //서브 그룹
	private Integer sortOrder; //정렬
	private String parentCode; // 상위 codeIdx
	private String createUserId;//생성자
    private String updateUserId;//수정자
	private Date createDate; // 생성일자
	private Date updateDate; // 수정일자

	public Integer getRecid() {
		return recid;
	}

	public void setRecid(Integer recid) {
		this.recid = recid;
	}
	
	public Integer getCodeIdx() {
		return codeIdx;
	}
	
	public void setCodeIdx(Integer codeIdx) {
		this.codeIdx = codeIdx;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public String getCodeDescription() {
		return codeDescription;
	}

	public void setCodeDescription(String codeDescription) {
		this.codeDescription = codeDescription;
	}

	public String getSubGroupCode() {
		return subGroupCode;
	}

	public void setSubGroupCode(String subGroupCode) {
		this.subGroupCode = subGroupCode;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}


	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	
	public String getCodeNameKR() {
		return codeNameKR;
	}

	public void setCodeNameKR(String codeNameKR) {
		this.codeNameKR = codeNameKR;
	}
	public void setUpdateDate(Date updateDate) {
		if(updateDate == null) {
			this.updateDate = null;
		} else {
			this.updateDate = new Date(updateDate.getTime());
		}
	}

	public Date getUpdateDate() {
		if(updateDate == null) {
			return null;
		} else {
			return new Date(updateDate.getTime());
		}
	}

	public Date getCreateDate() {
		if(createDate == null) {
			return null;
		} else {
			return new Date(createDate.getTime());
		}
	}
	
	public void setCreateDate(Date createDate) {
		if(createDate == null) {
			this.createDate = null;
		} else {
			this.createDate = new Date(createDate.getTime());
		}
	}
	
	
}
