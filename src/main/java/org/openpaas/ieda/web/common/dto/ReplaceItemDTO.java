package org.openpaas.ieda.web.common.dto;

public class ReplaceItemDTO {
	private String sourceItem; //sourceItem
	private String targetItem; //targetItem
	
	public ReplaceItemDTO(String targetItem, String sourceItem) {
		this.targetItem = targetItem;
		this.sourceItem = sourceItem;
	}
	
	
	public String getSourceItem() {
		return sourceItem;
	}
	public void setSourceItem(String sourceItem) {
		this.sourceItem = sourceItem;
	}
	public String getTargetItem() {
		return targetItem;
	}
	public void setTargetItem(String targetItem) {
		this.targetItem = targetItem;
	}

	
}
