package org.openpaas.ieda.web.config.bootstrap;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author jspark81@cloud4u.co.kr
 *
 */
public interface IEDABootstrapOpenstackRepository extends  JpaRepository<IEDABootstrapOpenstackConfig, Integer>  {
	//public IEDABootstrapOpenstackConfig findById(Integer id);
}
