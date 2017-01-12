package org.openpaas.ieda.web.deploy.diego.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openpaas.ieda.web.deploy.common.dao.network.NetworkVO;
import org.openpaas.ieda.web.deploy.common.dao.resource.ResourceVO;

public class DiegoVO {
	private Integer id; //id
	private String iaasType;//IAAS 유형
	
	private String createUserId;//IAAS 유형
	private Date createDate; //생성일자
	private String updateUserId;//IAAS 유형
	private Date updateDate; //수정일자
	
	//1.1 기본정보	
	private String deploymentName; //배포명
	private String directorUuid; //설치관리자 UUID
	private String diegoReleaseName; //DIEGO 릴리즈명
	private String diegoReleaseVersion; //DIEGO 릴리즈 버전
	private int cfId;
	private String cfName; 
	private String cfDeployment;
	private String cfReleaseName; //CF 릴리즈명
	private String cfReleaseVersion; //CF 릴리즈 버전
	private String gardenReleaseName; //Garden-Linux 릴리즈명
	private String gardenReleaseVersion; //Garden-Linux 릴리즈 버전
	private String etcdReleaseName; //ETCD 릴리즈명
	private String etcdReleaseVersion; //ETCD 릴리즈 버전
	private String cflinuxfs2rootfsreleaseName; //cflinuxfs2rootf 릴리즈 명
	private String cflinuxfs2rootfsreleaseVersion; //cflinuxfs2rootf 릴리즈 버전
	private String paastaMonitoringUse;//PaaS-TA 모니터링 사용 유무
	private String cadvisorDriverIp;//PaaS-TA 모니터링 DB 서버 IP
	private String cadvisorDriverPort;//PaaS-TA 모니터링 DB 서버 PORT
	private String keyFile;//key 파일명
	
	//2. 네트워크 정보
	private List<NetworkVO> networks;
	private NetworkVO network;
	
	//3. 리소스 정보	
	ResourceVO resource;
	//4. Deploy 정보
	private String deploymentFile; //배포파일명
	private String deployStatus; //배포상태
	private Integer taskId; //TASK ID
	
	public DiegoVO(){
		network = new NetworkVO();
		networks = new ArrayList<NetworkVO>();
		resource = new ResourceVO();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIaasType() {
		return iaasType;
	}
	public void setIaasType(String iaasType) {
		this.iaasType = iaasType;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getDeploymentName() {
		return deploymentName;
	}
	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}
	public String getDirectorUuid() {
		return directorUuid;
	}
	public void setDirectorUuid(String directorUuid) {
		this.directorUuid = directorUuid;
	}
	public String getDiegoReleaseName() {
		return diegoReleaseName;
	}
	public void setDiegoReleaseName(String diegoReleaseName) {
		this.diegoReleaseName = diegoReleaseName;
	}
	public String getDiegoReleaseVersion() {
		return diegoReleaseVersion;
	}
	public void setDiegoReleaseVersion(String diegoReleaseVersion) {
		this.diegoReleaseVersion = diegoReleaseVersion;
	}
	public int getCfId() {
		return cfId;
	}
	public void setCfId(int cfId) {
		this.cfId = cfId;
	}
	public String getCfName() {
		return cfName;
	}
	public void setCfName(String cfName) {
		this.cfName = cfName;
	}
	public String getCfDeployment() {
		return cfDeployment;
	}
	public void setCfDeployment(String cfDeployment) {
		this.cfDeployment = cfDeployment;
	}
	public String getCfReleaseName() {
		return cfReleaseName;
	}
	public void setCfReleaseName(String cfReleaseName) {
		this.cfReleaseName = cfReleaseName;
	}
	public String getCfReleaseVersion() {
		return cfReleaseVersion;
	}
	public void setCfReleaseVersion(String cfReleaseVersion) {
		this.cfReleaseVersion = cfReleaseVersion;
	}
	public String getGardenReleaseName() {
		return gardenReleaseName;
	}
	public void setGardenReleaseName(String gardenReleaseName) {
		this.gardenReleaseName = gardenReleaseName;
	}
	public String getGardenReleaseVersion() {
		return gardenReleaseVersion;
	}
	public void setGardenReleaseVersion(String gardenReleaseVersion) {
		this.gardenReleaseVersion = gardenReleaseVersion;
	}
	public String getEtcdReleaseName() {
		return etcdReleaseName;
	}
	public void setEtcdReleaseName(String etcdReleaseName) {
		this.etcdReleaseName = etcdReleaseName;
	}
	public String getEtcdReleaseVersion() {
		return etcdReleaseVersion;
	}
	public void setEtcdReleaseVersion(String etcdReleaseVersion) {
		this.etcdReleaseVersion = etcdReleaseVersion;
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
	public List<NetworkVO> getNetworks() {
		return networks;
	}
	public void setNetworks(List<NetworkVO> networks) {
		this.networks = networks;
	}
	public ResourceVO getResource() {
		return resource;
	}
	public void setResource(ResourceVO resource) {
		this.resource = resource;
	}
	public String getDeploymentFile() {
		return deploymentFile;
	}
	public void setDeploymentFile(String deploymentFile) {
		this.deploymentFile = deploymentFile;
	}
	public String getDeployStatus() {
		return deployStatus;
	}
	public void setDeployStatus(String deployStatus) {
		this.deployStatus = deployStatus;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}
	public NetworkVO getNetwork() {
		return network;
	}
	public void setNetwork(NetworkVO network) {
		this.network = network;
	}

	public String getCadvisorDriverIp() {
		return cadvisorDriverIp;
	}

	public void setCadvisorDriverIp(String cadvisorDriverIp) {
		this.cadvisorDriverIp = cadvisorDriverIp;
	}

	public String getCadvisorDriverPort() {
		return cadvisorDriverPort;
	}

	public void setCadvisorDriverPort(String cadvisorDriverPort) {
		this.cadvisorDriverPort = cadvisorDriverPort;
	}

	public String getPaastaMonitoringUse() {
		return paastaMonitoringUse;
	}

	public void setPaastaMonitoringUse(String paastaMonitoringUse) {
		this.paastaMonitoringUse = paastaMonitoringUse;
	}
	
}
