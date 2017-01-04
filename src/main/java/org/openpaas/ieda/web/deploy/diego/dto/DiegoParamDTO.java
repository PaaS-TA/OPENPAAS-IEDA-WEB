package org.openpaas.ieda.web.deploy.diego.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class DiegoParamDTO {
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Default{
		private String id; //id
		private String iaas; //IaaS
		
		//1.1 기본정보	
		@NotNull
		private String deploymentName; //배포명
		@NotNull
		private String directorUuid; //설치관리자 UUID
		@NotNull
		private String diegoReleaseName; //DIEGO 릴리즈명
		@NotNull
		private String diegoReleaseVersion; //DIEGO 릴리즈 버전
		@NotNull
		private String gardenReleaseName; //Garden-Linux 릴리즈명
		@NotNull
		private String gardenReleaseVersion; //Garden-Linux 릴리즈 버전
		@NotNull
		private String etcdReleaseName; //ETCD 릴리즈명
		@NotNull
		private String etcdReleaseVersion; //ETCD 릴리즈 버전
		private int cfId; //cf 아아디
		private String cfDeploymentFile; //cf 파일명
		private String cfDeploymentName;
		
		private String cflinuxfs2rootfsreleaseName; //cflinuxfs2rootf 릴리즈 명
		private String cflinuxfs2rootfsreleaseVersion; //cflinuxfs2rootf 릴리즈 버전
		
		@NotNull
		private String keyFile;
		
		public String getId() {
			return id;
		}
		public String getIaas() {
			return iaas;
		}
		public String getDeploymentName() {
			return deploymentName;
		}
		public String getDirectorUuid() {
			return directorUuid;
		}
		public String getDiegoReleaseName() {
			return diegoReleaseName;
		}
		public String getDiegoReleaseVersion() {
			return diegoReleaseVersion;
		}
		public String getGardenReleaseName() {
			return gardenReleaseName;
		}
		public String getGardenReleaseVersion() {
			return gardenReleaseVersion;
		}
		public String getEtcdReleaseName() {
			return etcdReleaseName;
		}
		public String getEtcdReleaseVersion() {
			return etcdReleaseVersion;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public void setDeploymentName(String deploymentName) {
			this.deploymentName = deploymentName;
		}
		public void setDirectorUuid(String directorUuid) {
			this.directorUuid = directorUuid;
		}
		public void setDiegoReleaseName(String diegoReleaseName) {
			this.diegoReleaseName = diegoReleaseName;
		}
		public void setDiegoReleaseVersion(String diegoReleaseVersion) {
			this.diegoReleaseVersion = diegoReleaseVersion;
		}
		public void setGardenReleaseName(String gardenReleaseName) {
			this.gardenReleaseName = gardenReleaseName;
		}
		public void setGardenReleaseVersion(String gardenReleaseVersion) {
			this.gardenReleaseVersion = gardenReleaseVersion;
		}
		public void setEtcdReleaseName(String etcdReleaseName) {
			this.etcdReleaseName = etcdReleaseName;
		}
		public void setEtcdReleaseVersion(String etcdReleaseVersion) {
			this.etcdReleaseVersion = etcdReleaseVersion;
		}
		public int getCfId() {
			return cfId;
		}
		public void setCfId(int cfId) {
			this.cfId = cfId;
		}
		public String getCfDeploymentFile() {
			return cfDeploymentFile;
		}
		public void setCfDeploymentFile(String cfDeploymentFile) {
			this.cfDeploymentFile = cfDeploymentFile;
		}
		public String getCfDeploymentName() {
			return cfDeploymentName;
		}
		public void setCfDeploymentName(String cfDeploymentName) {
			this.cfDeploymentName = cfDeploymentName;
		}
		public String getCflinuxfs2rootfsreleaseName() {
			return cflinuxfs2rootfsreleaseName;
		}
		public void setCflinuxfs2rootfsreleaseName(String cflinuxfs2rootfsreleaseName) {
			this.cflinuxfs2rootfsreleaseName = cflinuxfs2rootfsreleaseName;
		}
		public String getCflinuxfs2rootfsreleaseVersion() {
			return cflinuxfs2rootfsreleaseVersion;
		}
		public void setCflinuxfs2rootfsreleaseVersion(String cflinuxfs2rootfsreleaseVersion) {
			this.cflinuxfs2rootfsreleaseVersion = cflinuxfs2rootfsreleaseVersion;
		}
		public String getKeyFile() {
			return keyFile;
		}
		public void setKeyFile(String keyFile) {
			this.keyFile = keyFile;
		}
		
	}
	
	public static class Network {
		@NotNull
		private String id; //id
		private String iaas; //IaaS
		//3.1 네트워크 정보	
		@NotNull
		private String subnetStaticFrom; //IP할당 대역 From
		@NotNull
		private String subnetStaticTo; //IP할당 대역 To
		@NotNull
		private String subnetReservedFrom; //IP할당 제외 대역 From
		@NotNull
		private String subnetReservedTo; //IP할당 제외 대역 To
		@NotNull
		private String subnetRange; //서브넷 범위
		@NotNull
		private String subnetGateway; //게이트웨이
		@NotNull
		private String subnetDns; //DNS
		@NotNull
		private String subnetId; //서브넷 ID
		@NotNull
		private String cloudSecurityGroups;	//시큐리티 그룹명
		
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
		public String getSubnetStaticFrom() {
			return subnetStaticFrom;
		}
		public void setSubnetStaticFrom(String subnetStaticFrom) {
			this.subnetStaticFrom = subnetStaticFrom;
		}
		public String getSubnetStaticTo() {
			return subnetStaticTo;
		}
		public void setSubnetStaticTo(String subnetStaticTo) {
			this.subnetStaticTo = subnetStaticTo;
		}
		public String getSubnetReservedFrom() {
			return subnetReservedFrom;
		}
		public void setSubnetReservedFrom(String subnetReservedFrom) {
			this.subnetReservedFrom = subnetReservedFrom;
		}
		public String getSubnetReservedTo() {
			return subnetReservedTo;
		}
		public void setSubnetReservedTo(String subnetReservedTo) {
			this.subnetReservedTo = subnetReservedTo;
		}
		public String getSubnetRange() {
			return subnetRange;
		}
		public void setSubnetRange(String subnetRange) {
			this.subnetRange = subnetRange;
		}
		public String getSubnetGateway() {
			return subnetGateway;
		}
		public void setSubnetGateway(String subnetGateway) {
			this.subnetGateway = subnetGateway;
		}
		public String getSubnetDns() {
			return subnetDns;
		}
		public void setSubnetDns(String subnetDns) {
			this.subnetDns = subnetDns;
		}
		public String getSubnetId() {
			return subnetId;
		}
		public void setSubnetId(String subnetId) {
			this.subnetId = subnetId;
		}
		public String getCloudSecurityGroups() {
			return cloudSecurityGroups;
		}
		public void setCloudSecurityGroups(String cloudSecurityGroups) {
			this.cloudSecurityGroups = cloudSecurityGroups;
		}
		
	}
	
	public static class Deployment{
		@NotNull
		private String id;//id
		@NotNull
		private String deploymentFile; //배포파일명

		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDeploymentFile() {
			return deploymentFile;
		}
		public void setDeploymentFile(String deploymentFile) {
			this.deploymentFile = deploymentFile;
		}
	}
	
	
	
	public static class Delete{
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String id; //id
		@NotNull
		private String platform;//플랫폼 유형
		
		private List<String> seq;
		
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public List<String> getSeq() {
			return seq;
		}
		public void setSeq(List<String> seq) {
			this.seq = seq;
		}
		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}
	}
	
	
	public static class Install{
		@NotNull
		private String iaas; //IaaS
		@NotNull
		private String id; //id
		@NotNull
		private String platform;//플랫폼 유형
		
		public String getIaas() {
			return iaas;
		}
		public void setIaas(String iaas) {
			this.iaas = iaas;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}
	}
}
