package org.openpaas.ieda.web.information.manifest.dto;

import javax.validation.constraints.NotNull;

public class ManifestParamDTO {

	@NotNull
	private String id;
	@NotNull
	private String content;
	@NotNull
	private String fileName;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
