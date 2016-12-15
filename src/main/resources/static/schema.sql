#사용자 아이디	USER_ID	VARCHAR(255)	NOT NULL	
#사용자 비밀번호	USER_PASSWORD	VARCHAR(255)	NOT NULL	
#사용자 명		USER_NAME	VARCHAR(255)	NOT NULL	
#사용자 메일 주소	EMAIL	VARCHAR(255)	NOT NULL	
#롤 아이디		ROLE_ID	INT	NOT NULL	
#생성자			CREATE_USER_ID	VARCHAR(255)	NOT NULL	
#생성일자		CREATE_DATE	DATE	NOT NULL	
#수정자			UPDATE_USER_ID	VARCHAR(255)	NOT NULL	
#수정일자		UPDATE_DATE	DATE	NOT NULL


create database ieda default character set utf8 collate utf8_general_ci;

#ieda 데이터 베이스 생성
use ieda;

SET character_set_client = utf8;
SET character_set_results = utf8;
SET character_set_connection = utf8;
SET character_set_server = utf8;
SET NAMES 'UTF8';
SET CHARACTER SET 'UTF8';

CREATE TABLE ieda_user (    
	 user_id varchar(255) NOT NULL,	       
	 user_password varchar(255) NOT NULL,	
	 user_name varchar(255) NOT NULL,	   
	 email varchar(255) NOT NULL,	       
	 role_id int(11) NOT NULL,	   
	 init_pass_yn char(1) NOT NULL,	               
     create_user_id varchar(255) NOT NULL,
     create_date date NOT NULL,	           
     update_user_id varchar(255) NOT NULL,
     update_date date NOT NULL,
     PRIMARY KEY (`user_id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;	
	
#롤 아이디	ROLE_ID				INT(11)			NOT NULL	
#일련 번호	SEQ					INT(11)			NOT NULL	
#권한 코드	AUTH_CODE			VARCHAR(255)	NOT NULL	
#생성자		CREATE_USER_ID		VARCHAR(255)	NOT NULL	-
#생성일자	CREATE_DATE	DATE	NOT NULL		SYSDATE
#수정자		UPDATE_USER_ID		VARCHAR(255)	NOT NULL	-
#수정일자	UPDATE_DATE	DATE	NOT NULL		SYSDATE

CREATE TABLE ieda_role
(
	role_id INT(11) NOT NULL AUTO_INCREMENT,
	role_name VARCHAR(255) NOT NULL,
	role_description VARCHAR(255) 	 NULL,
	create_user_id VARCHAR(255) NOT NULL,
	create_date DATE NOT NULL,
	update_user_id VARCHAR(255) NOT NULL,
	update_date DATE NOT NULL,
	PRIMARY KEY (role_id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_role AUTO_INCREMENT=1000;

CREATE TABLE ieda_role_detail (    
	 role_id int(11) NOT NULL,	       
	 seq int(11) NOT NULL,	
	 auth_code varchar(255) NOT NULL,                 
     create_user_id varchar(255) NOT NULL,
     create_date date NOT NULL,	           
     update_user_id varchar(255) NOT NULL,
     update_date date NOT NULL
	) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_common_code (
	code_idx int(11) NOT NULL AUTO_INCREMENT,
	code_name varchar(255),
	code_value varchar(255),
	code_name_kr varchar(255),
	code_description varchar(255),
	sort_order int(11),
	sub_group_code varchar(255),
	parent_code varchar(255),
    create_user_id varchar(255) NOT NULL ,
    create_date date NOT NULL ,
    update_user_id varchar(255) NOT NULL ,
    update_date date NOT NULL ,
	PRIMARY KEY (code_idx) ,
    index COMMON_CODE_INDEX (CODE_VALUE)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_common_code AUTO_INCREMENT=1000;

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
	create_user_id varchar(255) not null,
	create_date date not null,
	update_user_id varchar(255) not null,
	update_date date not null,
	PRIMARY KEY (ieda_director_config_seq)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_director_config AUTO_INCREMENT=1000;

# public stemcell
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
	create_user_id varchar(255) NOT NULL,
	create_date date NOT NULL,
	update_user_id varchar(255) NOT NULL,
	update_date date NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_public_stemcells AUTO_INCREMENT=1000;

#system_release(new)
CREATE TABLE ieda_system_releases(
 id int(11) not null auto_increment,
 release_type varchar(255) null,
 release_name varchar(255) null,
 size varchar(255) null,
 release_filename varchar(255) null,
 download_status varchar(255) null,
 create_user_id varchar(255) not null,
 create_date date not null,
 update_user_id varchar(255) not null,
 update_date date not null,
 primary key(id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_system_releases AUTO_INCREMENT=1000;

#Manifast
SET FOREIGN_KEY_CHECKS=0 ;
/* Drop Tables */
DROP TABLE IF EXISTS ieda_manifest_template CASCADE;
CREATE TABLE ieda_manifest_template
(
	id INT NOT NULL AUTO_INCREMENT, 
	deploy_type VARCHAR(255) NOT NULL,
	iaas_type VARCHAR(255) NOT NULL,
	release_type VARCHAR(255) NOT NULL,
	template_version VARCHAR(255) NOT NULL,
	min_release_version VARCHAR(255) NOT NULL,
	common_base_template VARCHAR(255) NOT NULL,
	common_job_template VARCHAR(255) NOT NULL,
	common_option_template VARCHAR(255)  NULL,
	iaas_property_template VARCHAR(255) 	 NULL,
	option_network_template VARCHAR(255) 	 NULL,
	option_resource_template VARCHAR(255) 	 NULL,
	option_etc VARCHAR(255) 	 NULL,
	meta_template VARCHAR(255) 	 NULL,
	input_template VARCHAR(255) NOT NULL,
	create_user_id VARCHAR(255) NOT NULL,
	create_date DATE NOT NULL,
	update_user_id VARCHAR(255) NOT NULL,
	update_date DATE NOT NULL,
	PRIMARY KEY (id, deploy_type, iaas_type, min_release_version)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_manifest_template AUTO_INCREMENT=1000;

SET FOREIGN_KEY_CHECKS=1 ;

#Manifest
SET FOREIGN_KEY_CHECKS=0 ;

DROP TABLE IF EXISTS ieda_manifest CASCADE;
CREATE TABLE ieda_manifest
(
	id INT NOT NULL AUTO_INCREMENT,
	manifest_idx INT NULL,
	manifest_file VARCHAR(255) NULL,
	iaas VARCHAR(255) NULL,
	deployment_name VARCHAR(255) NULL,
	description VARCHAR(255) NULL,
	path VARCHAR(255) NULL,
	deploy_status VARCHAR(255) NULL,
	create_user_id VARCHAR(255) NOT NULL,
	create_date DATE NOT NULL,
	update_user_id VARCHAR(255) NOT NULL,
	update_date DATE NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_manifest AUTO_INCREMENT=1000;


# BOOTSTRAP
CREATE TABLE ieda_bootstrap (
	 id int(11) NOT NULL auto_increment,
	 iaas_type varchar(255)	NOT NULL,
	 aws_access_key_id varchar(100) NULL,	
	 aws_secret_access_id varchar(100) NULL,	
	 aws_region varchar(100) NULL,
	 aws_availability_zone varchar(100) NULL,
	 openstack_auth_url varchar(100) NULL,	
	 openstack_tenant varchar(100) NULL,	
	 openstack_user_name varchar(100) NULL,	
	 openstack_api_key varchar(100) NULL,	
	 default_security_groups varchar(100) NULL,
	 private_key_name varchar(100) NULL,
	 private_key_path varchar(100) NULL,
	 vcenter_address varchar(100) NULL,	
	 vcenter_user varchar(100) NULL,	
	 vcenter_password varchar(100) NULL,
	 vcenter_datacenter_name varchar(100) NULL,
	 vcenter_vm_folder varchar(100) NULL,
	 vcenter_template_folder varchar(100) NULL,
	 vcenter_datastore varchar(100) NULL,
	 vcenter_persistent_datastore varchar(100) NULL,
	 vcenter_disk_path varchar(100) NULL,
	 vcenter_clusters varchar(100) NULL,
	 deployment_name varchar(100) NULL,	
	 director_name varchar(100) NULL,	
	 ntp varchar(100) NULL,
	 bosh_release varchar(100) NULL,	
	 bosh_cpi_release varchar(100) NULL,
	 enable_snapshots varchar(100) NULL,
	 snapshot_schedule varchar(100) NULL,
	 subnet_id varchar(100) NULL,
	 private_static_ip varchar(100) NULL,
	 public_static_ip varchar(100) NULL,	
	 subnet_range varchar(100) NULL,
	 subnet_gateway varchar(100) NULL,
	 subnet_dns varchar(100) NULL,	
	 public_subnet_id varchar(100) NULL,
	 public_subnet_range varchar(100) NULL,
	 public_subnet_gateway varchar(100) NULL,
	 public_subnet_dns varchar(100) NULL,
	 stemcell varchar(100) NULL,
	 cloud_instance_type varchar(100) NULL,
	 bosh_password varchar(255) NULL,
	 resource_pool_cpu varchar(100) NULL,
	 resource_pool_ram varchar(100) NULL,
	 resource_pool_disk varchar(100) NULL,
	 deployment_file varchar(255) NULL,
	 deploy_status varchar(100) NULL,
	 deploy_log longtext NULL,
	 create_date date NOT NULL,
	 create_user_id varchar(255) NOT NULL,
	 update_date date NOT NULL,
	 update_user_id varchar(255) NOT NULL,
 	 PRIMARY KEY (id)
 ) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_bootstrap AUTO_INCREMENT=1000;

# BOSH
 CREATE TABLE ieda_bosh(
	 id int(11) not null auto_increment,
	 iaas_type VARCHAR(255) not NULL,
	 aws_access_key_id VARCHAR(255) NULL,
	 aws_secret_access_id VARCHAR(255) NULL,
	 aws_region VARCHAR(255) NULL,
	 aws_availability_zone varchar(255) NULL,
	 openstack_auth_url VARCHAR(255) NULL,
	 openstack_tenant VARCHAR(255) NULL,
	 openstack_user_name VARCHAR(255) NULL,
	 openstack_api_key VARCHAR(255) NULL,
	 vcenter_address VARCHAR(255) NULL,
	 vcenter_user VARCHAR(255) NULL,
	 vcenter_password VARCHAR(255) NULL,
	 vcenter_datacenter_name VARCHAR(255) NULL,
	 vcenter_vm_folder VARCHAR(255) NULL,
	 vcenter_template_folder VARCHAR(255) NULL,
	 vcenter_datastore VARCHAR(255) NULL,
	 vcenter_persistent_datastore VARCHAR(255) NULL,
	 vcenter_disk_path VARCHAR(255) NULL,
	 vcenter_clusters VARCHAR(255) NULL,
	 default_security_groups varchar(100) NULL,
	 private_key_name varchar(100) NULL,
	 deployment_name varchar(100) NULL,	
	 director_uuid varchar(100) NULL,
	 release_version varchar(100) NULL,
	 ntp varchar(100) NULL,
	 director_name varchar(100) NULL,	
	 snapshot_schedule varchar(100) NULL,
	 enable_snapshots varchar(100) NULL,
	 deployment_file varchar(100) NULL,
	 deploy_status varchar(100) NULL,
	 task_id int(11) NULL,
	 create_user_id varchar(255) NOT NULL,
	 create_date date NOT NULL,
	 update_user_id varchar(255) NOT NULL,
	 update_date date NOT NULL,
 	 PRIMARY KEY (id)
 ) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
 ALTER TABLE ieda_bosh AUTO_INCREMENT=1000;
 
 CREATE TABLE ieda_network(
 	id int(11) NOT NULL,
 	deploy_type varchar(100)  NOT NULL,
 	seq int(11) NOT NULL DEFAULT 0,
 	net varchar(255) NOT NULL,
 	subnet_range varchar(255) NULL,
 	subnet_gateway varchar(255) NULL,
 	subnet_reserved_from varchar(255) NULL,
	subnet_reserved_to varchar(255) NULL,
	subnet_dns varchar(255) NULL,
	subnet_static_from varchar(255) NULL,
	subnet_static_to varchar(255) NULL,
	subnet_id varchar(255) NULL,
	cloud_security_groups varchar(255) NULL,
	create_user_id varchar(255) NOT NULL,
	create_date date NOT NULL,
	update_user_id varchar(255) NOT NULL,
	update_date date NOT NULL,
	PRIMARY KEY (id, deploy_type, seq)
 ) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
 ALTER TABLE ieda_network AUTO_INCREMENT=1000;
 #Key
CREATE TABLE ieda_key(
	id int(11) NOT NULL,
	deploy_type varchar(100)  NOT NULL,
	key_type int(11) NOT NULL,
	ca_cert longtext NULL,
	server_cert longtext NULL,
	server_key longtext NULL,
	client_cert longtext NULL,
	client_key longtext NULL,
	agent_cert longtext NULL,
	agent_key longtext NULL,
	tls_cert longtext NULL,
	private_key longtext NULL,
	public_key longtext NULL,
	host_key longtext NULL,
	create_user_id longtext NOT NULL,
	create_date date NOT NULL,
	update_user_id varchar(255) NOT NULL,
	update_date date NOT NULL ,
	PRIMARY KEY (id, deploy_type, key_type)
)ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_key AUTO_INCREMENT=1000;
#Resource
CREATE TABLE ieda_resource(
	id int(11) NOT NULL,
	deploy_type varchar(100) NOT NULL,
	bosh_password varchar(255) NULL,
	stemcell_name varchar(255) NULL,
	stemcell_version varchar(255) NULL,
	small_type_flavor varchar(255) NULL,
	small_type_cpu int(11) NULL,
	small_type_ram int(11) NULL,
	small_type_disk int(11) NULL,
	medium_type_flavor varchar(255) NULL,
	medium_type_cpu int(11) NULL,
	medium_type_ram int(11) NULL,
	medium_type_disk int(11) NULL,
	large_type_flavor varchar(255) NULL,
	large_type_cpu int(11) NULL,
	large_type_ram int(11) NULL,
	large_type_disk int(11) NULL,
	runner_type_flavor varchar(255) NULL,
	runner_type_cpu int(11) NULL,
	runner_type_ram int(11) NULL,
	runner_type_disk int(11) NULL,
	runner_instance_number int(11) NULL,
	create_user_id varchar(255) NOT NULL,
	create_date date NOT NULL,
	update_user_id varchar(255) NOT NULL,
	update_date date NOT NULL,
	PRIMARY KEY (id, deploy_type)
)ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_resource AUTO_INCREMENT=1000;

ALTER TABLE ieda_key
    ENGINE=InnoDB
    ROW_FORMAT=COMPRESSED 
    KEY_BLOCK_SIZE=8;
#cf
CREATE TABLE ieda_cf(
	 id int(11)	NOT NULL auto_increment,
	 iaas_type varchar(255) NOT NULL,
	 diego_yn varchar(255) NOT NULL,
	 deployment_name varchar(100) NULL,
	 director_uuid varchar(100) NULL,
	 release_name varchar(100) NULL,
	 release_version varchar(100) NULL,
	 app_ssh_fingerprint varchar(200) NULL,
	 dea_memory_mb int(11) NULL,
	 dea_disk_mb int(11) NULL,
	 domain varchar(100) NULL,
	 description varchar(100) NULL,
	 domain_organization varchar(100) NULL,
	 proxy_static_ips varchar(100) NULL,
	 ssl_pem_pub longtext NULL,
	 ssl_pem_rsa longtext NULL,
	 login_secret varchar(100) NULL,
	 encrypt_keys varchar(200) NULL,
	 deployment_file varchar(255) NULL,
	 deploy_status varchar(100) NULL,
	 task_id int(11) NULL,
	 create_user_id varchar(255) NOT NULL,
	 create_date date NOT NULL,
	 update_user_id varchar(255) NOT NULL,
	 update_date date NOT NULL,
	 PRIMARY KEY (id)
 ) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_cf AUTO_INCREMENT=1000;

CREATE TABLE ieda_diego (
	id int(11) NOT NULL auto_increment,
	iaas_type varchar(255) NOT NULL,
	create_user_id varchar(255) NOT NULL,
	create_date date NOT NULL,
	update_user_id varchar(255) NOT NULL,
	update_date date NOT NULL,
	deployment_name VARCHAR(100) NULL,
	director_uuid VARCHAR(100) NULL,
	diego_release_name VARCHAR(100) NULL,
	diego_release_version VARCHAR(100) NULL,
	cflinuxfs2_rootfs_release_name VARCHAR(100) NULL,
	cflinuxfs2_rootfs_release_version VARCHAR(100) NULL,
	cf_deployment VARCHAR(255) NULL,
	garden_release_name VARCHAR(100) NULL,
	garden_release_version VARCHAR(100) NULL,
	etcd_release_name VARCHAR(100) NULL,
	etcd_release_version VARCHAR(100) NULL,
	diego_encryption_keys VARCHAR(200) NULL,
	deployment_file VARCHAR(255) NULL,
	deploy_status VARCHAR(100) NULL,
	task_id INT(11)  NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_diego AUTO_INCREMENT=1000;

CREATE TABLE ieda_cf_diego (
	id INT(11) NOT NULL auto_increment,
	cf_id INT(11) NULL,
	diego_id INT(11) NULL,
	iaas_type VARCHAR(255) NULL,
	create_user_id varchar(255) NOT NULL,
	create_date date NOT NULL,
	update_user_id varchar(255) NOT NULL,
	update_date date NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_cf_diego AUTO_INCREMENT=1000; 	

SET GLOBAL innodb_file_format=Barracuda;
SET GLOBAL innodb_file_per_table=ON;
    
ALTER TABLE ieda_key
    ENGINE=InnoDB
    ROW_FORMAT=COMPRESSED 
    KEY_BLOCK_SIZE=8;
    
CREATE TABLE ieda_service_pack(
	id int(11) NOT NULL auto_increment,
	iaas VARCHAR(255) NOT NULL,
	deployment_name VARCHAR(100) NULL,
	deployment_file VARCHAR(100) NULL,
	deploy_status VARCHAR(100) NULL,
	create_user_id VARCHAR(255) NOT NULL,
	create_date date NOT NULL,
	update_user_id varchar(255) NOT NULL,
	update_date date NOT NULL,
	 PRIMARY KEY (id)
)  ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;
ALTER TABLE ieda_service_pack AUTO_INCREMENT=1000;
