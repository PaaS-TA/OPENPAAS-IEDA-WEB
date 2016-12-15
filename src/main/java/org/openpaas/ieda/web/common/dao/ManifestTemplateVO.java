package org.openpaas.ieda.web.common.dao;

import java.util.Date;

public class ManifestTemplateVO {
	private Integer id; //id
	private String deployType; //배포유형
	private String iaasType; //iaas 유형
	private String releaseType; // release유형
	private String templateVersion; // template 버전
	private String minReleaseVersion; //최소 릴리즈 버전
	private String commonBaseTemplate; //기초 템플릿
	private String commonJobTemplate; //JOB 템플릿
	private String commonOptionTemplate; //Option 템플릿
	private String iaasPropertyTemplate;//프로퍼티 템플릿(옵션)
	private String optionNetworkTemplate;//네트워크 템플릿(옵션)
	private String optionResourceTemplate;//리소스 템플릿(옵션)
	private String optionEtc;//기타 템플릿(옵션)
	private String cfTempleate; //DIEGO 연동 CF deploy 위치
	private String metaTemplate;//메타 템플릿
	private String inputTemplate; //입력값 템플릿
	private String shellScript;
	private String createUserId;//생성자
	private Date createDate;//생성일
	private String updateUserId;//수정자
	private Date updateDate;//수정일
	
	public ManifestTemplateVO(){
		
	}
	public ManifestTemplateVO( Date createDate, Date updateDate ){
		this.createDate = createDate;
		this.updateDate = updateDate;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDeployType() {
		return deployType;
	}
	public void setDeployType(String deployType) {
		this.deployType = deployType;
	}
	public String getIaasType() {
		return iaasType;
	}
	public void setIaasType(String iaasType) {
		this.iaasType = iaasType;
	}
	public String getMinReleaseVersion() {
		return minReleaseVersion;
	}
	public void setMinReleaseVersion(String minReleaseVersion) {
		this.minReleaseVersion = minReleaseVersion;
	}
	public String getCommonBaseTemplate() {
		return commonBaseTemplate;
	}
	public void setCommonBaseTemplate(String commonBaseTemplate) {
		this.commonBaseTemplate = commonBaseTemplate;
	}
	public String getCommonJobTemplate() {
		return commonJobTemplate;
	}
	public void setCommonJobTemplate(String commonJobTemplate) {
		this.commonJobTemplate = commonJobTemplate;
	}
	public String getCommonOptionTemplate() {
		return commonOptionTemplate;
	}
	public void setCommonOptionTemplate(String commonOptionTemplate) {
		this.commonOptionTemplate = commonOptionTemplate;
	}
	public String getReleaseType() {
		return releaseType;
	}
	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}
	public String getTemplateVersion() {
		return templateVersion;
	}
	public void setTemplateVersion(String templateVersion) {
		this.templateVersion = templateVersion;
	}
	public String getIaasPropertyTemplate() {
		return iaasPropertyTemplate;
	}
	public void setIaasPropertyTemplate(String iaasPropertyTemplate) {
		this.iaasPropertyTemplate = iaasPropertyTemplate;
	}
	public String getOptionNetworkTemplate() {
		return optionNetworkTemplate;
	}
	public void setOptionNetworkTemplate(String optionNetworkTemplate) {
		this.optionNetworkTemplate = optionNetworkTemplate;
	}
	public String getOptionResourceTemplate() {
		return optionResourceTemplate;
	}
	public void setOptionResourceTemplate(String optionResourceTemplate) {
		this.optionResourceTemplate = optionResourceTemplate;
	}
	public String getOptionEtc() {
		return optionEtc;
	}
	public void setOptionEtc(String optionEtc) {
		this.optionEtc = optionEtc;
	}
	public String getMetaTemplate() {
		return metaTemplate;
	}
	public void setMetaTemplate(String metaTemplate) {
		this.metaTemplate = metaTemplate;
	}
	public String getInputTemplate() {
		return inputTemplate;
	}
	public void setInputTemplate(String inputTemplate) {
		this.inputTemplate = inputTemplate;
	}
	public String getShellScript() {
		return shellScript;
	}
	public void setShellScript(String shellScript) {
		this.shellScript = shellScript;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Date getCreateDate() {
		return new Date(createDate.getTime());
	}
	public void setCreateDate(Date createDate) {
		this.createDate = new Date(createDate.getTime());
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Date getUpdateDate() {
		return new Date(updateDate.getTime());
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = new Date(updateDate.getTime());
	}
	public String getCfTempleate() {
		return cfTempleate;
	}
	public void setCfTempleate(String cfTempleate) {
		this.cfTempleate = cfTempleate;
	}
}
