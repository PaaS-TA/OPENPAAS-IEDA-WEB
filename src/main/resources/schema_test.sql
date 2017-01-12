#사용자 아이디    USER_ID                 VARCHAR(255) NOT NULL
#사용자 비밀번호   USER_PASSWORD           VARCHAR(255) NOT NULL
#사용자 명      USER_NAME               VARCHAR(255) NOT NULL
#사용자 메일 주소  EMAIL                   VARCHAR(255) NOT NULL
#롤 아이디      ROLE_ID                 INT NOT NULL
#생성자        CREATE_USER_ID          VARCHAR(255) NOT NULL
#생성일자       CREATE_DATE             DATE NOT NULL
#수정자        UPDATE_USER_ID          VARCHAR(255) NOT NULL
#수정일자       UPDATE_DATE             DATE NOT NULL


create database ieda_test default character set utf8 collate utf8_general_ci;

#ieda 데이터 베이스 생성
use ieda_test;

SET character_set_client = utf8;
SET character_set_results = utf8;
SET character_set_connection = utf8;
SET character_set_server = utf8;
SET NAMES 'UTF8';
SET CHARACTER SET 'UTF8';

CREATE TABLE ieda_user
(
  user_id                           VARCHAR(255)  NOT NULL,
  user_password                     VARCHAR(255)  NOT NULL,
  user_name                         VARCHAR(255)  NOT NULL,
  email                             VARCHAR(255)  NOT NULL,
  role_id                           INT(11)       NOT NULL,
  init_pass_yn                      CHAR(1)       NOT NULL,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#롤 아이디    ROLE_ID                 INT(11) NOT NULL
#일련 번호    SEQ                     INT(11) NOT NULL
#권한 코드    AUTH_CODE               VARCHAR(255) NOT NULL
#생성자      CREATE_USER_ID          VARCHAR(255) NOT NULL
#생성일자     CREATE_DATE             DATE NOT NULL  SYSDATE
#수정자      UPDATE_USER_ID          VARCHAR(255) NOT NULL
#수정일자     UPDATE_DATE             DATE NOT NULL  SYSDATE

CREATE TABLE ieda_role
(
  role_id                           INT(11)       NOT NULL AUTO_INCREMENT,
  role_name                         VARCHAR(255)  NOT NULL,
  role_description                  VARCHAR(255)  NULL,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (role_id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_role_detail
(
  role_id                           INT(11)       NOT NULL,
  seq                               INT(11)       NOT NULL,
  auth_code                         VARCHAR(255)  NOT NULL,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (role_id, seq)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_common_code
(
  code_idx                          INT(11)       NOT NULL AUTO_INCREMENT,
  code_name                         VARCHAR(255)  NOT NULL,
  code_value                        VARCHAR(255)  NOT NULL,
  code_name_kr                      VARCHAR(255),
  code_description                  VARCHAR(255),
  sort_order                        INT(11)       NOT NULL,
  sub_group_code                    VARCHAR(255),
  parent_code                       VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (code_idx) ,
  INDEX COMMON_CODE_INDEX (code_value)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_director_config
(
  ieda_director_config_seq          INT(11)       NOT NULL AUTO_INCREMENT,
  current_deployment                VARCHAR(255),
  default_yn                        VARCHAR(255),
  director_cpi                      VARCHAR(255),
  director_name                     VARCHAR(255),
  director_port                     INT(11),
  director_url                      VARCHAR(255),
  director_uuid                     VARCHAR(255),
  director_version                  VARCHAR(255),
  user_id                           VARCHAR(255),
  user_password                     VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (ieda_director_config_seq)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

# public stemcell
CREATE TABLE ieda_public_stemcells
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  download_status                   VARCHAR(255),
  iaas                              VARCHAR(255),
  stemcell_name                     VARCHAR(255),
  os                                VARCHAR(255),
  os_version                        VARCHAR(255),
  size                              VARCHAR(255),
  stemcell_filename                 VARCHAR(255),
  stemcell_version                  VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#system_release(new)
CREATE TABLE ieda_system_releases
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  release_type                      VARCHAR(255),
  release_name                      VARCHAR(255),
  size                              VARCHAR(255),
  release_filename                  VARCHAR(255),
  download_status                   VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  primary key(id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#Manifast
SET FOREIGN_KEY_CHECKS=0 ;
/* Drop Tables */
DROP TABLE IF EXISTS ieda_manifest_template CASCADE;
CREATE TABLE ieda_manifest_template
(
  id                                INT           NOT NULL AUTO_INCREMENT,
  deploy_type                       VARCHAR(255)  NOT NULL,
  iaas_type                         VARCHAR(255)  NOT NULL,
  release_type                      VARCHAR(255)  NOT NULL,
  template_version                  VARCHAR(255)  NOT NULL,
  min_release_version               VARCHAR(255)  NOT NULL,
  common_base_template              VARCHAR(255)  NOT NULL,
  common_job_template               VARCHAR(255)  NOT NULL,
  common_option_template            VARCHAR(255),
  iaas_property_template            VARCHAR(255),
  option_network_template           VARCHAR(255),
  option_resource_template          VARCHAR(255),
  option_etc                        VARCHAR(255),
  meta_template                     VARCHAR(255),
  input_template                    VARCHAR(255)  NOT NULL,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id, deploy_type, iaas_type, min_release_version)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1 ;

#Manifest
SET FOREIGN_KEY_CHECKS=0 ;

DROP TABLE IF EXISTS ieda_manifest CASCADE;
CREATE TABLE ieda_manifest
(
  id                                INT           NOT NULL AUTO_INCREMENT,
  manifest_idx                      INT,
  manifest_file                     VARCHAR(255),
  iaas                              VARCHAR(255),
  deployment_name                   VARCHAR(255),
  description                       VARCHAR(255),
  path                              VARCHAR(255),
  deploy_status                     VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

# BOOTSTRAP
CREATE TABLE ieda_bootstrap
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(255)  NOT NULL,
  aws_access_key_id                 VARCHAR(100),
  aws_secret_access_id              VARCHAR(100),
  aws_region                        VARCHAR(100),
  aws_availability_zone             VARCHAR(100),
  openstack_auth_url                VARCHAR(100),
  openstack_tenant                  VARCHAR(100),
  openstack_user_name               VARCHAR(100),
  openstack_api_key                 VARCHAR(100),
  default_security_groups           VARCHAR(100),
  private_key_name                  VARCHAR(100),
  private_key_path                  VARCHAR(100),
  vcenter_address                   VARCHAR(100),
  vcenter_user                      VARCHAR(100),
  vcenter_password                  VARCHAR(100),
  vcenter_datacenter_name           VARCHAR(100),
  vcenter_vm_folder                 VARCHAR(100),
  vcenter_template_folder           VARCHAR(100),
  vcenter_datastore                 VARCHAR(100),
  vcenter_persistent_datastore      VARCHAR(100),
  vcenter_disk_path                 VARCHAR(100),
  vcenter_clusters                  VARCHAR(100),
  deployment_name                   VARCHAR(100),
  director_name                     VARCHAR(100),
  ntp                               VARCHAR(100),
  bosh_release                      VARCHAR(100),
  bosh_cpi_release                  VARCHAR(100),
  enable_snapshots                  VARCHAR(100),
  snapshot_schedule                 VARCHAR(100),
  subnet_id                         VARCHAR(100),
  private_static_ip                 VARCHAR(100),
  public_static_ip                  VARCHAR(100),
  subnet_range                      VARCHAR(100),
  subnet_gateway                    VARCHAR(100),
  subnet_dns                        VARCHAR(100),
  public_subnet_id                  VARCHAR(100),
  public_subnet_range               VARCHAR(100),
  public_subnet_gateway             VARCHAR(100),
  public_subnet_dns                 VARCHAR(100),
  stemcell                          VARCHAR(100),
  cloud_instance_type               VARCHAR(100),
  bosh_password                     VARCHAR(255),
  resource_pool_cpu                 VARCHAR(100),
  resource_pool_ram                 VARCHAR(100),
  resource_pool_disk                VARCHAR(100),
  deployment_file                   VARCHAR(255),
  deploy_status                     VARCHAR(100),
  deploy_log                        LONGTEXT,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

# BOSH
CREATE TABLE ieda_bosh
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(255)  NOT NULL,
  aws_access_key_id                 VARCHAR(255),
  aws_secret_access_id              VARCHAR(255),
  aws_region                        VARCHAR(255),
  aws_availability_zone             VARCHAR(255),
  openstack_auth_url                VARCHAR(255),
  openstack_tenant                  VARCHAR(255),
  openstack_user_name               VARCHAR(255),
  openstack_api_key                 VARCHAR(255),
  vcenter_address                   VARCHAR(255),
  vcenter_user                      VARCHAR(255),
  vcenter_password                  VARCHAR(255),
  vcenter_datacenter_name           VARCHAR(255),
  vcenter_vm_folder                 VARCHAR(255),
  vcenter_template_folder           VARCHAR(255),
  vcenter_datastore                 VARCHAR(255),
  vcenter_persistent_datastore      VARCHAR(255),
  vcenter_disk_path                 VARCHAR(255),
  vcenter_clusters                  VARCHAR(255),
  default_security_groups           VARCHAR(100),
  private_key_name                  VARCHAR(100),
  deployment_name                   VARCHAR(100),
  director_uuid                     VARCHAR(100),
  release_version                   VARCHAR(100),
  ntp                               VARCHAR(100),
  director_name                     VARCHAR(100),
  snapshot_schedule                 VARCHAR(100),
  enable_snapshots                  VARCHAR(100),
  deployment_file                   VARCHAR(100),
  deploy_status                     VARCHAR(100),
  task_id                           INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_network
(
  id                                INT(11)       NOT NULL,
  deploy_type                       VARCHAR(100)  NOT NULL,
  seq                               INT(11)       NOT NULL DEFAULT 0,
  net                               VARCHAR(255)  NOT NULL,
  subnet_range                      VARCHAR(255),
  subnet_gateway                    VARCHAR(255),
  subnet_reserved_from              VARCHAR(255),
  subnet_reserved_to                VARCHAR(255),
  subnet_dns                        VARCHAR(255),
  subnet_static_from                VARCHAR(255),
  subnet_static_to                  VARCHAR(255),
  subnet_id                         VARCHAR(255),
  cloud_security_groups             VARCHAR(255),
  availability_zone                 VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id, deploy_type, seq)
 ) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#Resource
CREATE TABLE ieda_resource
(
  id                                INT(11)       NOT NULL,
  deploy_type                       VARCHAR(100)  NOT NULL,
  bosh_password                     VARCHAR(255),
  stemcell_name                     VARCHAR(255),
  stemcell_version                  VARCHAR(255),
  small_type_flavor                 VARCHAR(255),
  small_type_cpu                    INT(11),
  small_type_ram                    INT(11),
  small_type_disk                   INT(11),
  medium_type_flavor                VARCHAR(255),
  medium_type_cpu                   INT(11),
  medium_type_ram                   INT(11),
  medium_type_disk                  INT(11),
  large_type_flavor                 VARCHAR(255),
  large_type_cpu                    INT(11),
  large_type_ram                    INT(11),
  large_type_disk                   INT(11),
  runner_type_flavor                VARCHAR(255),
  runner_type_cpu                   INT(11),
  runner_type_ram                   INT(11),
  runner_type_disk                  INT(11),
  runner_instance_number            INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id, deploy_type)
)ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#cf
CREATE TABLE ieda_cf
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(255)  NOT NULL,
  diego_yn                          VARCHAR(255)  NOT NULL,
  deployment_name                   VARCHAR(100),
  director_uuid                     VARCHAR(100),
  release_name                      VARCHAR(100),
  release_version                   VARCHAR(100),
  app_ssh_fingerprint               VARCHAR(200),
  dea_memory_mb                     INT(11),
  dea_disk_mb                       INT(11),
  domain                            VARCHAR(100),
  description                       VARCHAR(100),
  domain_organization               VARCHAR(100),
  proxy_static_ips                  VARCHAR(100),
  login_secret                      VARCHAR(100),
  country_code                      VARCHAR(255),
  state_name                        VARCHAR(255),
  locality_name                     VARCHAR(255),
  organization_name                 VARCHAR(255),
  unit_name                         VARCHAR(255),
  email                             VARCHAR(255),
  key_file                          VARCHAR(255),
  deployment_file                   VARCHAR(255),
  deploy_status                     VARCHAR(100),
  task_id                           INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_diego
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(255)  NOT NULL,
  deployment_name                   VARCHAR(100),
  director_uuid                     VARCHAR(100),
  diego_release_name                VARCHAR(100),
  diego_release_version             VARCHAR(100),
  cflinuxfs2_rootfs_release_name    VARCHAR(100),
  cflinuxfs2_rootfs_release_version VARCHAR(100),
  cf_deployment                     VARCHAR(255),
  garden_release_name               VARCHAR(100),
  garden_release_version            VARCHAR(100),
  etcd_release_name                 VARCHAR(100),
  etcd_release_version              VARCHAR(100),
  paasta_monitoring_use             VARCHAR(100),
  cadvisor_driver_ip                VARCHAR(100),
  cadvisor_driver_port              VARCHAR(100),
  key_file                          VARCHAR(100),
  deployment_file                   VARCHAR(255),
  deploy_status                     VARCHAR(100),
  task_id                           INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_cf_diego
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  cf_id                             INT(11),
  diego_id                          INT(11),
  iaas_type                         VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

## SET GLOBAL innodb_file_format=Barracuda;
## SET GLOBAL innodb_file_per_table=ON;

CREATE TABLE ieda_service_pack
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas                              VARCHAR(255)  NOT NULL,
  deployment_name                   VARCHAR(100),
  deployment_file                   VARCHAR(100),
  deploy_status                     VARCHAR(100),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


#Setting AUTO_INCREMENT
ALTER TABLE ieda_role AUTO_INCREMENT=1000;
ALTER TABLE ieda_common_code AUTO_INCREMENT=1000;
ALTER TABLE ieda_director_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_public_stemcells AUTO_INCREMENT=1000;
ALTER TABLE ieda_system_releases AUTO_INCREMENT=1000;
ALTER TABLE ieda_manifest_template AUTO_INCREMENT=1000;
ALTER TABLE ieda_manifest AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap AUTO_INCREMENT=1000;
ALTER TABLE ieda_bosh AUTO_INCREMENT=1000;
ALTER TABLE ieda_cf AUTO_INCREMENT=1000;
ALTER TABLE ieda_diego AUTO_INCREMENT=1000;
ALTER TABLE ieda_cf_diego AUTO_INCREMENT=1000;
ALTER TABLE ieda_service_pack AUTO_INCREMENT=1000;
