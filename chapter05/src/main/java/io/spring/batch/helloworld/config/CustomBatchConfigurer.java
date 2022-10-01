//package io.spring.batch.helloworld.config;
//
//import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
//import org.springframework.batch.support.DatabaseType;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
//public class CustomBatchConfigurer extends DefaultBatchConfigurer {
//
//    @Autowired
//    @Qualifier("repositoryDataSource")
//    private DataSource dataSource;
//
//    @Autowired
//    @Qualifier("batchTransactionManager")
//    private PlatformTransactionManager transactionManager;
//
//    @Override
//    public JobRepository createJobRepository() throws Exception {
//        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
//
//        factoryBean.setDatabaseType(DatabaseType.H2.getProductName());
//        factoryBean.setTablePrefix("FOO_");
//        factoryBean.setIsolationLevelForCreate("ISOLATION_REPEATABLE_READ");
//        factoryBean.setDataSource(this.dataSource);
//        factoryBean.afterPropertiesSet();
//        return factoryBean.getObject();
//    }
//
//    @Override
//    public PlatformTransactionManager getTransactionManager() {
//        return this.transactionManager;
//    }
//
//    @Override
//    protected JobExplorer createJobExplorer() throws Exception {
//        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
//
//        factoryBean.setDataSource(this.dataSource);
//        factoryBean.setTablePrefix("FOO_");
//        factoryBean.afterPropertiesSet();
//
//        return factoryBean.getObject();
//    }
//}
