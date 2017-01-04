package org.openpaas.ieda.web.common.dto;

import javax.validation.constraints.NotNull;

public class KeyInfoDTO {
		@NotNull
		private String id; //id
		@NotNull
		private String iaas;//iaas
		@NotNull
		private String platform;//platform
		private String domain;//도메인
		private String countryCode;//국가코드
		private String stateName;//시/도
		private String localityName;//시/구/군
		private String organizationName;//회사명
		private String unitName;//부서명
		private String email;//이메일
		
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
		public String getDomain() {
			return domain;
		}
		public void setDomain(String domain) {
			this.domain = domain;
		}
		public String getCountryCode() {
			return countryCode;
		}
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		public String getStateName() {
			return stateName;
		}
		public void setStateName(String stateName) {
			this.stateName = stateName;
		}
		public String getLocalityName() {
			return localityName;
		}
		public void setLocalityName(String localityName) {
			this.localityName = localityName;
		}
		public String getOrganizationName() {
			return organizationName;
		}
		public void setOrganizationName(String organizationName) {
			this.organizationName = organizationName;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}
}
