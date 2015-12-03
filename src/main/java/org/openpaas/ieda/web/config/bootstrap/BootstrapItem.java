package org.openpaas.ieda.web.config.bootstrap;

import lombok.Data;

@Data
public class BootstrapItem {
	String sourceItem;
	String targetItem;
	
	public BootstrapItem(String targetItem, String sourceItem) {
		this.targetItem = targetItem;
		this.sourceItem = sourceItem;
	}
}
