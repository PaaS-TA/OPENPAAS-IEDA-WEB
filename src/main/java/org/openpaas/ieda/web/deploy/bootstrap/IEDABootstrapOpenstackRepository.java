package org.openpaas.ieda.web.deploy.bootstrap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDABootstrapOpenstackRepository extends  JpaRepository<IEDABootstrapOpenstackConfig, Integer>  {
}
