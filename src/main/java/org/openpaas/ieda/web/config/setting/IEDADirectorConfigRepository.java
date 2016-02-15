package org.openpaas.ieda.web.config.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IEDADirectorConfigRepository extends JpaRepository<IEDADirectorConfig, Integer>{

	public IEDADirectorConfig findByIedaDirectorConfigSeq(Integer seq);
	public IEDADirectorConfig findOneByDefaultYn(String defaultYn);
	public List<IEDADirectorConfig> findByDirectorUrl(String directorUrl);
}
