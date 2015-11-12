package org.openpaas.ieda.web.config.stemcell;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDAStemcellContentRepository extends JpaRepository<StemcellContent, Integer> {

	List<StemcellContent> findByOsAndOsVersionAndIaasAllIgnoreCaseOrderByOsVersionDesc(String os, String osVersion, String iaas);
	List<StemcellContent> findByStemcellFileNameInOrderByOsVersionDesc(List<String> stemcellFileName);
}
