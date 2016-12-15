package org.openpaas.ieda;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(value = { "org.openpaas.ieda.web"})
public class PersistenceConfig {
	
	/***************************************************
	 * @project          : Paas 플랫폼 설치 자동화
	 * @description   : xml mybatis 셋팅
	 * @title               : sqlSEssionFactory
	 * @return            : SqlSessionFactory
	***************************************************/
	@Bean
    public SqlSessionFactory sqlSEssionFactory(DataSource dataSource) throws Exception {
    	SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    	sessionFactory.setDataSource(dataSource);
    	
    	Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:ieda/**/**/*.xml");
    	sessionFactory.setMapperLocations(res);
    	
    	return sessionFactory.getObject();
    }
	
    /***************************************************
     * @project          : Paas 플랫폼 설치 자동화
     * @description   : DataSourceTransactionManager 객체 생성
     * @title               : transactionManager
     * @return            : PlatformTransactionManager
    ***************************************************/
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
