package org.openpaas.ieda.web.deploy.cf;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDACfOpenstackRepository  extends JpaRepository<IEDACfOpenstackConfig, Integer > {

}
