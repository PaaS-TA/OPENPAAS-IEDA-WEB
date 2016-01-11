package org.openpaas.ieda.web.deploy.bootstrap;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author jspark81@cloud4u.co.kr
 *
 */
public interface IEDABootstrapAwsRepository extends  JpaRepository<IEDABootstrapAwsConfig, Integer>  {
	public IEDABootstrapAwsConfig findById(Integer id);
}
