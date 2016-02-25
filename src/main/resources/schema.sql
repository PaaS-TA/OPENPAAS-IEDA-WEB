CREATE TABLE ieda_common_code (
	code_idx int(11) NOT NULL AUTO_INCREMENT,
	code_description varchar(255),
	code_name varchar(255),
	code_value varchar(255),
	parent_code_idx int(11),
	sort_order int(11),
	PRIMARY KEY (code_idx)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_director_config (
	ieda_director_config_seq int(11) NOT NULL AUTO_INCREMENT,
	
	current_deployment varchar(255),
	default_yn varchar(255),
	director_cpi varchar(255),
	director_name varchar(255),
	director_port int(11),
	director_url varchar(255),
	director_uuid varchar(255),
	director_version varchar(255),	
	user_id varchar(255),
	user_password varchar(255),
	created_date date,
	updated_date date,
	PRIMARY KEY (ieda_director_config_seq)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_public_stemcells (
	id int(11) NOT NULL AUTO_INCREMENT,
	download_status varchar(255),
	iaas varchar(255),
	sublink varchar(255),
	os varchar(255),
	os_version varchar(255),
	size varchar(255),
	stemcell_filename varchar(255),
	stemcell_version varchar(255),
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bootstrap_aws (
	id INT(11) NOT NULL AUTO_INCREMENT,
	created_date DATE NOT NULL,
	updated_date DATE NOT NULL,
	
	-- AWS
	access_key_id VARCHAR(100) NULL,
	secret_access_id VARCHAR(100) NULL,
	default_security_groups VARCHAR(100) NULL,
	region VARCHAR(100) NULL,
	availability_zone VARCHAR(100) NULL,
	private_key_name VARCHAR(100) NULL,
	private_key_path VARCHAR(100) NULL,
	
	-- BOSH
	deployment_name VARCHAR(100) NULL,
	director_name VARCHAR(100) NULL,
	bosh_release VARCHAR(100) NULL,
	bosh_cpi_release VARCHAR(100) NULL,
	
	-- NETWORK
	subnet_id VARCHAR(100) NULL,
	private_static_ip VARCHAR(100) NULL,
	public_static_ip VARCHAR(100) NULL,
	subnet_range VARCHAR(100) NULL,
	subnet_gateway VARCHAR(100) NULL,
	subnet_dns VARCHAR(100) NULL,
	ntp VARCHAR(100) NULL,
	
	-- RESOURCE
	stemcell VARCHAR(100) NULL,
	cloud_instance_type VARCHAR(100) NULL,
	bosh_password VARCHAR(200) NULL,
	
	deployment_file VARCHAR(100) NULL,
	deploy_status VARCHAR(100)  NULL,
	deploy_log LONGTEXT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_bootstrap_openstack (
	id INT(11) NOT NULL AUTO_INCREMENT,
	created_date DATE NOT NULL,
	updated_date DATE NOT NULL,
	
	-- OPENSTACK
	auth_url VARCHAR(100) NULL,
	tenant VARCHAR(100) NULL,
	user_name VARCHAR(100) NULL,
	api_key VARCHAR(100) NULL,
	default_security_groups VARCHAR(100) NULL,
	private_key_name VARCHAR(100) NULL,
	private_key_path VARCHAR(100) NULL,
	
	-- BOSH
	deployment_name VARCHAR(100) NULL,
	director_name VARCHAR(100) NULL,
	bosh_release VARCHAR(100) NULL,
	bosh_cpi_release VARCHAR(100) NULL,
	
	-- NETWORK
	subnet_id VARCHAR(100) NULL,
	private_static_ip VARCHAR(100) NULL,
	public_static_ip VARCHAR(100) NULL,
	subnet_range VARCHAR(100) NULL,
	subnet_gateway VARCHAR(100) NULL,
	subnet_dns VARCHAR(100) NULL,
	ntp VARCHAR(100) NULL,
	
	-- RESOURCE
	stemcell VARCHAR(100) NULL,
	cloud_instance_type VARCHAR(100) NULL,
	bosh_password VARCHAR(200) NULL,
	
	deployment_file VARCHAR(100) NULL,
	deploy_status VARCHAR(100)  NULL,
	deploy_log LONGTEXT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bosh_aws (
	id int(11) NOT NULL AUTO_INCREMENT,
	created_date date NOT NULL,
	updated_date date NOT NULL,
	-- AWS
	access_key_id VARCHAR(100)  NULL,
	secret_access_key VARCHAR(100)  NULL,
	default_security_groups VARCHAR(100)  NULL,
	region VARCHAR(100)  NULL,
	private_key_name VARCHAR(100)  NULL,
	private_key_path VARCHAR(100)  NULL,
	-- BOSH    
	deployment_name VARCHAR(100)  NULL,
	director_uuid VARCHAR(100)  NULL,
	release_version VARCHAR(100)  NULL,
	-- NETWORK
	subnet_id VARCHAR(100)  NULL,
	public_static_ip VARCHAR(100)  NULL,
	subnet_static_from VARCHAR(100)  NULL,
	subnet_static_to VARCHAR(100)  NULL,
	subnet_range VARCHAR(100)  NULL,
	subnet_gateway VARCHAR(100)  NULL,
	subnet_dns VARCHAR(100)  NULL,
	-- RESOURCE
	stemcell_name VARCHAR(100)  NULL,
	stemcell_version VARCHAR(100)  NULL,
	cloud_instance_type VARCHAR(100)  NULL,
	bosh_password varchar(200)  NULL,
	
	deployment_file VARCHAR(100)  NULL,
	deploy_status VARCHAR(100)  NULL,
	task_id INT(11)  NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bosh_openstack (
	id int(11) NOT NULL AUTO_INCREMENT,
	created_date date NOT NULL,
	updated_date date NOT NULL,
	-- OPENSTACK
	auth_url VARCHAR(100)  NULL,
	tenant VARCHAR(100)  NULL,
	user_name VARCHAR(100)  NULL,
	api_key VARCHAR(100)  NULL,
	default_security_groups VARCHAR(100)  NULL,
	private_key_name VARCHAR(100)  NULL,
	private_key_path VARCHAR(100)  NULL,
	-- BOSH
	deployment_name VARCHAR(100)  NULL,
	director_name VARCHAR(100)  NULL,
	director_uuid VARCHAR(100)  NULL,
	release_version VARCHAR(100)  NULL,
	-- NETWORK
	public_static_ip VARCHAR(100)  NULL,
	subnet_id VARCHAR(100)  NULL,
	subnet_static_from VARCHAR(100)  NULL,
	subnet_static_to varchar(45)  NULL,
	subnet_range VARCHAR(100)  NULL,
	subnet_gateway VARCHAR(100)  NULL,
	subnet_dns VARCHAR(100)  NULL,
	-- RESOURCE
	stemcell_name VARCHAR(100)  NULL,
	stemcell_version VARCHAR(100)  NULL,
	cloud_instance_type VARCHAR(100)  NULL,
	bosh_password varchar(200)  NULL,

	deployment_file VARCHAR(100)  NULL,
	deploy_status VARCHAR(100)  NULL,
	task_id INT(11)  NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_cf_aws (
	id int(11) NOT NULL AUTO_INCREMENT,
	created_date date NOT NULL,
	updated_date date NOT NULL,
		-- 1.1 Deployment 정보
	deployment_name VARCHAR(100) NULL,
	director_uuid VARCHAR(100) NULL,
	release_name VARCHAR(100) NULL,
	release_version VARCHAR(100) NULL,
	app_ssh_fingerprint VARCHAR(200) NULL,
	-- 1.2 기본정보	
	domain VARCHAR(100) NULL,
	description VARCHAR(100) NULL,
	domain_organization VARCHAR(100) NULL,
	-- 1.3 프록시 정보
	proxy_static_ips VARCHAR(100) NULL,
	ssl_pem_pub LONGTEXT NULL,
	ssl_pem_rsa LONGTEXT NULL,
	-- 2. UAA 정보	
	login_secret VARCHAR(100) NULL,
	signing_key LONGTEXT NULL,
	verification_key LONGTEXT NULL,
	-- 3. Consul 정보	
	agent_cert LONGTEXT NULL,
	agent_key LONGTEXT NULL,
	ca_cert LONGTEXT NULL,
	encrypt_keys VARCHAR(200) NULL,
	server_cert LONGTEXT NULL,
	server_key LONGTEXT NULL,
	-- 4. 네트워크 정보	
	subnet_static_from VARCHAR(100) NULL,
	subnet_static_to VARCHAR(100) NULL,
	subnet_reserved_from VARCHAR(100) NULL,
	subnet_reserved_to VARCHAR(100) NULL,
	subnet_range VARCHAR(100) NULL,
	subnet_gateway VARCHAR(100) NULL,
	subnet_dns VARCHAR(100) NULL,
	subnet_id VARCHAR(100) NULL,
	cloud_security_groups VARCHAR(100) NULL,
	-- 5. 리소스 정보	
	bosh_password VARCHAR(200) NULL,
	stemcell_name VARCHAR(100) NULL,
	stemcell_version VARCHAR(100) NULL,
	-- Deploy 정보	
	deployment_file VARCHAR(100) NULL,
	deploy_status VARCHAR(100) NULL,
	task_id INT(11)  NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_cf_openstack (
	id int(11) NOT NULL AUTO_INCREMENT,
	created_date date NOT NULL,
	updated_date date NOT NULL,
		-- 1.1 Deployment 정보
	deployment_name VARCHAR(100) NULL,
	director_uuid VARCHAR(100) NULL,
	release_name VARCHAR(100) NULL,
	release_version VARCHAR(100) NULL,
	app_ssh_fingerprint VARCHAR(200) NULL,
	-- 1.2 기본정보	
	domain VARCHAR(100) NULL,
	description VARCHAR(100) NULL,
	domain_organization VARCHAR(100) NULL,
	-- 1.3 프록시 정보
	proxy_static_ips VARCHAR(100) NULL,
	ssl_pem_pub LONGTEXT NULL,
	ssl_pem_rsa LONGTEXT NULL,
	-- 2. UAA 정보	
	login_secret VARCHAR(100) NULL,
	signing_key LONGTEXT NULL,
	verification_key LONGTEXT NULL,
	-- 3. Consul 정보	
	agent_cert LONGTEXT NULL,
	agent_key LONGTEXT NULL,
	ca_cert LONGTEXT NULL,
	encrypt_keys VARCHAR(200) NULL,
	server_cert LONGTEXT NULL,
	server_key LONGTEXT NULL,
	-- 4. 네트워크 정보	
	subnet_static_from VARCHAR(100) NULL,
	subnet_static_to VARCHAR(100) NULL,
	subnet_reserved_from VARCHAR(100) NULL,
	subnet_reserved_to VARCHAR(100) NULL,
	subnet_range VARCHAR(100) NULL,
	subnet_gateway VARCHAR(100) NULL,
	subnet_dns VARCHAR(100) NULL,
	cloud_net_id VARCHAR(100) NULL,
	cloud_security_groups VARCHAR(100) NULL,
	-- 5. 리소스 정보	
	bosh_password VARCHAR(200) NULL,
	stemcell_name VARCHAR(100) NULL,
	stemcell_version VARCHAR(100) NULL,
	-- Deploy 정보	
	deployment_file VARCHAR(100) NULL,
	deploy_status VARCHAR(100) NULL,
	task_id INT(11)  NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_diego_aws (
	id int(11) NOT NULL AUTO_INCREMENT,
	created_date date NOT NULL,
	updated_date date NOT NULL,
	-- 1.1 Deployment 정보
	deployment_name VARCHAR(100) NULL,
	director_uuid VARCHAR(100) NULL,
	diego_release_name VARCHAR(100) NULL,
	diego_release_version VARCHAR(100) NULL,
	cf_release_name VARCHAR(100) NULL,
	cf_release_version VARCHAR(100) NULL,
	garden_linux_release_name VARCHAR(100) NULL,
	garden_linux_release_version VARCHAR(100) NULL,
	etcd_release_name VARCHAR(100) NULL,
	etcd_release_version VARCHAR(100) NULL,

	-- 1.2	 CF 정보
	domain VARCHAR(100) NULL,
	deployment VARCHAR(100) NULL,
	secret VARCHAR(100) NULL,
	etcd_machines VARCHAR(100) NULL,
	nats_machines VARCHAR(100) NULL,
	consul_servers_lan VARCHAR(100) NULL,
	consul_agent_cert LONGTEXT NULL,
	consul_agent_key LONGTEXT NULL,
	consul_ca_cert LONGTEXT NULL,
	consul_encrypt_keys VARCHAR(200) NULL,
	consul_server_cert LONGTEXT NULL,
	consul_server_key LONGTEXT NULL,
	
	-- 2.1 Diego 정보
	diego_ca_cert LONGTEXT NULL,
	diego_client_cert LONGTEXT NULL,
	diego_client_key LONGTEXT NULL,
	diego_encryption_keys VARCHAR(200) NULL,
	diego_server_cert LONGTEXT NULL,
	diego_server_key LONGTEXT NULL,
	-- 2.2 ETCD 정보
	etcd_client_cert LONGTEXT NULL,
	etcd_client_key LONGTEXT NULL,
	etcd_peer_ca_cert LONGTEXT NULL,
	etcd_peer_cert LONGTEXT NULL,
	etcd_peer_key LONGTEXT NULL,
	etcd_server_cert LONGTEXT NULL,
	etcd_server_key LONGTEXT NULL,
	
	-- 3.1 네트워크 정보
	subnet_static_from VARCHAR(100) NULL,
	subnet_static_to VARCHAR(100) NULL,
	subnet_reserved_from VARCHAR(100) NULL,
	subnet_reserved_to VARCHAR(100) NULL,
	subnet_range VARCHAR(100) NULL,
	subnet_gateway VARCHAR(100) NULL,
	subnet_dns VARCHAR(100) NULL,
	subnet_id VARCHAR(100) NULL,
	cloud_security_groups VARCHAR(100) NULL,
	-- 3.2 프록시 정보
	diego_host_key LONGTEXT NULL,
	diego_servers VARCHAR(100) NULL,
	diego_uaa_secret VARCHAR(100) NULL,
	
	-- 4. 리소스 정보
	stemcell_name VARCHAR(100) NULL,
	stemcell_version VARCHAR(100) NULL,
	bosh_password VARCHAR(200) NULL,	
	
	-- Deploy 정보	
	deployment_file VARCHAR(100) NULL,
	deploy_status VARCHAR(100) NULL,
	task_id INT(11)  NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_diego_openstack (
	id int(11) NOT NULL AUTO_INCREMENT,
	created_date date NOT NULL,
	updated_date date NOT NULL,
	-- 1.1 Deployment 정보
	deployment_name VARCHAR(100) NULL,
	director_uuid VARCHAR(100) NULL,
	diego_release_name VARCHAR(100) NULL,
	diego_release_version VARCHAR(100) NULL,
	cf_release_name VARCHAR(100) NULL,
	cf_release_version VARCHAR(100) NULL,
	garden_linux_release_name VARCHAR(100) NULL,
	garden_linux_release_version VARCHAR(100) NULL,
	etcd_release_name VARCHAR(100) NULL,
	etcd_release_version VARCHAR(100) NULL,

	-- 1.2	 CF 정보
	domain VARCHAR(100) NULL,
	deployment VARCHAR(100) NULL,
	secret VARCHAR(100) NULL,
	etcd_machines VARCHAR(100) NULL,
	nats_machines VARCHAR(100) NULL,
	consul_servers_lan VARCHAR(100) NULL,
	consul_agent_cert LONGTEXT NULL,
	consul_agent_key LONGTEXT NULL,
	consul_ca_cert LONGTEXT NULL,
	consul_encrypt_keys VARCHAR(200) NULL,
	consul_server_cert LONGTEXT NULL,
	consul_server_key LONGTEXT NULL,
	
	-- 2.1 Diego 정보
	diego_ca_cert LONGTEXT NULL,
	diego_client_cert LONGTEXT NULL,
	diego_client_key LONGTEXT NULL,
	diego_encryption_keys VARCHAR(200) NULL,
	diego_server_cert LONGTEXT NULL,
	diego_server_key LONGTEXT NULL,
	-- 2.2 ETCD 정보
	etcd_client_cert LONGTEXT NULL,
	etcd_client_key LONGTEXT NULL,
	etcd_peer_ca_cert LONGTEXT NULL,
	etcd_peer_cert LONGTEXT NULL,
	etcd_peer_key LONGTEXT NULL,
	etcd_server_cert LONGTEXT NULL,
	etcd_server_key LONGTEXT NULL,
	
	-- 3.1 네트워크 정보
	subnet_static_from VARCHAR(100) NULL,
	subnet_static_to VARCHAR(100) NULL,
	subnet_reserved_from VARCHAR(100) NULL,
	subnet_reserved_to VARCHAR(100) NULL,
	subnet_range VARCHAR(100) NULL,
	subnet_gateway VARCHAR(100) NULL,
	subnet_dns VARCHAR(100) NULL,
	cloud_net_id VARCHAR(100) NULL,
	cloud_security_groups VARCHAR(100) NULL,
	-- 3.2 프록시 정보
	diego_host_key LONGTEXT NULL,
	diego_servers VARCHAR(100) NULL,
	diego_uaa_secret VARCHAR(100) NULL,
	
	-- 4. 리소스 정보
	stemcell_name VARCHAR(100) NULL,
	stemcell_version VARCHAR(100) NULL,
	bosh_password VARCHAR(200) NULL,	
	
	-- Deploy 정보	
	deployment_file VARCHAR(100) NULL,
	deploy_status VARCHAR(100) NULL,
	task_id INT(11)  NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

ALTER TABLE ieda_diego_aws
    ENGINE=InnoDB
    ROW_FORMAT=COMPRESSED 
    KEY_BLOCK_SIZE=8;

ALTER TABLE ieda_diego_openstack
    ENGINE=InnoDB
    ROW_FORMAT=COMPRESSED 
    KEY_BLOCK_SIZE=8;
	
