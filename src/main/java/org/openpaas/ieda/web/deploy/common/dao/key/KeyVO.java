package org.openpaas.ieda.web.deploy.common.dao.key;

import java.util.Date;

public class KeyVO {
	private Integer id; //id
	private String deployType; //배포 유형
	private Integer keyType;//key유형
	private String caCert;//ca 인증서
	private String serverCert;//서버 인증서
	private String serverKey;//서버키
	private String clientCert;//클라이언트 인증서
	private String clientKey;//클라이언트키
	private String agentCert;//에이전트 인증서
	private String agentKey;//에이전트키
	private String tlsCert;//tls 인증서
	private String privateKey;//개인키
	private String publicKey;//공개키
	private String hostKey;//호스트키
	private String createUserId; //생성자
	private String updateUserId; //수정자
	private Date createDate; // 생성일자
	private Date updateDate; // 수정일자
	
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
	public Integer getKeyType() {
		return keyType;
	}
	public void setKeyType(Integer keyType) {
		this.keyType = keyType;
	}
	public String getCaCert() {
		return caCert;
	}
	public void setCaCert(String caCert) {
		this.caCert = caCert;
	}
	public String getServerCert() {
		return serverCert;
	}
	public void setServerCert(String serverCert) {
		this.serverCert = serverCert;
	}
	public String getServerKey() {
		return serverKey;
	}
	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}
	public String getClientCert() {
		return clientCert;
	}
	public void setClientCert(String clientCert) {
		this.clientCert = clientCert;
	}
	public String getClientKey() {
		return clientKey;
	}
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}
	public String getAgentCert() {
		return agentCert;
	}
	public void setAgentCert(String agentCert) {
		this.agentCert = agentCert;
	}
	public String getAgentKey() {
		return agentKey;
	}
	public void setAgentKey(String agentKey) {
		this.agentKey = agentKey;
	}
	public String getTlsCert() {
		return tlsCert;
	}
	public void setTlsCert(String tlsCert) {
		this.tlsCert = tlsCert;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getHostKey() {
		return hostKey;
	}
	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
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
	public void setUpdateDate(Date updateDate) {
		if(updateDate == null) {
			this.updateDate = null;
		} else {
			this.updateDate = new Date(updateDate.getTime());
		}
	}

	public Date getUpdateDate() {
		if(updateDate == null) {
			return null;
		} else {
			return new Date(updateDate.getTime());
		}
	}

	public Date getCreateDate() {
		if(createDate == null) {
			return null;
		} else {
			return new Date(createDate.getTime());
		}
	}
	
	public void setCreateDate(Date createDate) {
		if(createDate == null) {
			this.createDate = null;
		} else {
			this.createDate = new Date(createDate.getTime());
		}
	}
	
}
