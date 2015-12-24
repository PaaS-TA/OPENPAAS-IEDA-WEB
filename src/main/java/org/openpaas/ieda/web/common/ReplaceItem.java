package org.openpaas.ieda.web.common;

import lombok.Data;

@Data
public class ReplaceItem {
	String sourceItem;
	String targetItem;
	
	public ReplaceItem(String targetItem, String sourceItem) {
		this.targetItem = targetItem;
		this.sourceItem = sourceItem;
	}
}
