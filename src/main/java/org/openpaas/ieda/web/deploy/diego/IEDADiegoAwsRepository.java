package org.openpaas.ieda.web.deploy.diego;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDADiegoAwsRepository extends JpaRepository<IEDADiegoAwsConfig, Integer> {

}
