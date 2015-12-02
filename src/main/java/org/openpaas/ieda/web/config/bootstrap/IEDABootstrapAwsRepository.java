package org.openpaas.ieda.web.config.bootstrap;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author jspark81@cloud4u.co.kr
 *
 */
public interface IEDABootstrapAwsRepository extends  JpaRepository<IEDABootstrapAwsConfig, Integer>  {
	public IEDABootstrapAwsConfig findById(Integer id);
	public IEDABootstrapAwsConfig findOneBybootstrapId(Integer bootstrapId);
}
