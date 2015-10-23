package org.openpaas.ieda.web.config.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author "Cheolho, Moon <chmoon93@gmail.com / Cloud4U, Inc>"
 *
 */
public interface IEDADirectorConfigRepository extends JpaRepository<IEDADirectorConfig, Integer>{

//	public void delete(int iedaDirectorConfigSeq);
	public IEDADirectorConfig findByIedaDirectorConfigSeq(Integer seq);
	public IEDADirectorConfig findOneByDefaultYn(String defaultYn);
	public List<IEDADirectorConfig> findByDirectorUrl(String directorUrl);
}
