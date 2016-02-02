-- Parent Code
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (1, 'IaaS Type', 'IaaS Type', 'IaaS Type', 0, null);
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (2, 'OS Type', 'OS Type', 'OS Type', 0, null);

-- Child Code : IaaS
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (101, 'OpenStack', 'OpenStack', 'Open Source', 1, 1);
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (102,'vSphere', 'vSphere', 'VMWare vSphere', 2, 1);
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (103,'AWS', 'AWS', 'Amazon Web Service', 3, 1);

-- Child Code : OS Version
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (201, 'Ubuntu', 'Ubuntu', 'Ubuntu', 1, 2);
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (202,'CentOS', 'CentOS', 'CentOS', 2, 2);

-- Child Code : Ubuntu Version
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (211, 'Trusty', 'Ubuntu Trusty', 'Ubuntu Trusty', 1, 201);
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (212, 'Lucid', 'Ubuntu Lucid', 'Ubuntu Lucid', 2, 201);

-- Child Code : CentOS Version
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (221, '7.x', '7.x', 'CentOS 7.x', 1, 202);
insert into ieda_common_code (code_idx, code_name, code_value, code_description, sort_order, parent_code_Idx) values (222, '6.x', '6.x', 'CentOS 6.x', 2, 202);


