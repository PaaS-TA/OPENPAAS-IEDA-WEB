package org.openpaas.ieda.web.deploy.bosh;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDABoshOpenstackRepository extends JpaRepository<IEDABoshOpenstackConfig, Integer>  {
	public IEDABoshOpenstackConfig findById(Integer id);
}
