package org.openpaas.ieda.web.deploy.bosh;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDABoshAwsRepository extends JpaRepository<IEDABoshAwsConfig, Integer>  {
	public IEDABoshAwsConfig findById(Integer id);
}
