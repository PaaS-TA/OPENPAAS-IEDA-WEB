package org.openpaas.ieda.web.information.manifest.dto;

public class ManifestListDTO {
	
	private String id; //id
	private String iaas;//iaas
	private String fileName; //manifest 파일명
	private String description; //manifest 설명
	private String path; //manifest 업로드 경로
	private String deploymentName; //서비스팩 배포명
	private String deployStatus;//Manifest 사요 유무
	private String createUserId;//생성자
	private String updateUserId;//수정자
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIaas() {
		return iaas;
	}
	public void setIaas(String iaas) {
		this.iaas = iaas;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDeploymentName() {
		return deploymentName;
	}
	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	public String getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
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
	
}
