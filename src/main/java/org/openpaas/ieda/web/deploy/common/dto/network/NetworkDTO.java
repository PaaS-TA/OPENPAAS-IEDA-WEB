package org.openpaas.ieda.web.deploy.common.dto.network;

import javax.validation.constraints.NotNull;

public class NetworkDTO {
	
	private String cfId; //id
	private String diegoId;//id
	private String boshId;
	private String id;
	@NotNull
	private String iaas;//iaas
	@NotNull
	private String deployType;//배포 유형
	@NotNull
	private String net;
	private String seq; //시퀀스
	private String publicStaticIP;
	@NotNull
	private String subnetRange; //서브넷 범위
	@NotNull
	private String subnetGateway; //게이트웨이
	@NotNull
	private String subnetDns; //DNS
	@NotNull
	private String subnetReservedFrom; //할당된 IP대역 From
	@NotNull
	private String subnetReservedTo; //할당된 IP대역 To
	@NotNull
	private String subnetStaticFrom; //VM 할당 IP대역 From
	@NotNull
	private String subnetStaticTo; //VM 할당 IP대역 To
	@NotNull
	private String subnetId; //네트워크 ID
	private String cloudSecurityGroups; //시큐리티 그룹
	private String availabilityZone;
	
	public String getDiegoId() {
		return diegoId;
	}
	public void setDiegoId(String diegoId) {
		this.diegoId = diegoId;
	}
	public String getCfId() {
		return cfId;
	}
	public void setCfId(String cfId) {
		this.cfId = cfId;
	}
	public String getBoshId() {
		return boshId;
	}
	public void setBoshId(String boshId) {
		this.boshId = boshId;
	}
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
	public String getDeployType() {
		return deployType;
	}
	public void setDeployType(String deployType) {
		this.deployType = deployType;
	}
	public String getNet() {
		return net;
	}
	public void setNet(String net) {
		this.net = net;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public String getPublicStaticIP() {
		return publicStaticIP;
	}
	public void setPublicStaticIP(String publicStaticIP) {
		this.publicStaticIP = publicStaticIP;
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
	public String getAvailabilityZone() {
		return availabilityZone;
	}
	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}
}
