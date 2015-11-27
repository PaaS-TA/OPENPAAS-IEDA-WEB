package org.openpaas.ieda.web.config.bootstrap;

import lombok.Data;

@Data
public class BootstrapItem {
	String sourceItem;
	String targetItem;
	
	public BootstrapItem(String sourceItem, String targetItem) {
		this.sourceItem = sourceItem;
		this.targetItem = targetItem;
	}
}
