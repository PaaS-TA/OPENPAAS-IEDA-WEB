package org.openpaas.ieda.web.config.stemcell;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDAStemcellManagementRepository extends JpaRepository<StemcellManagementConfig, Integer> {

	List<StemcellManagementConfig> findByOsAndOsVersionAndIaasAllIgnoreCaseOrderByOsVersionDesc(String os, String osVersion, String iaas);
	List<StemcellManagementConfig> findByStemcellFileNameInOrderByOsVersionDesc(List<String> stemcellFileName);
	List<StemcellManagementConfig> findByStemcellFileNameInOrderByStemcellVersionDesc(List<String> stemcellFileName);
}
