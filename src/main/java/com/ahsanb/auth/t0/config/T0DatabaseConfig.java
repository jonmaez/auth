package com.ahsanb.auth.t0.config;

import com.ahsanb.auth.t0.dao.T0TenantRepository;
import com.ahsanb.auth.t0.entities.T0Tenant;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.ahsanb.auth.t0.entities", "com.ahsanb.auth.t0.dao"},
        entityManagerFactoryRef = "t0EntityManagerFactory",
        transactionManagerRef = "t0TransactionManager")
public class T0DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(T0DatabaseConfig.class);

    @Autowired
    private T0DatabaseConfigProperties t0DbProperties;

    //Create t0 Data Source using t0 properties and also configure HikariCP
    @Bean(name = "t0DataSource")
    public DataSource t0DataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setUsername(t0DbProperties.getUsername());
        hikariDataSource.setPassword(t0DbProperties.getPassword());
        hikariDataSource.setJdbcUrl(t0DbProperties.getUrl());
        hikariDataSource.setDriverClassName(t0DbProperties.getDriverClassName());
        hikariDataSource.setPoolName(t0DbProperties.getPoolName());
        // HikariCP settings
        hikariDataSource.setMaximumPoolSize(t0DbProperties.getMaxPoolSize());
        hikariDataSource.setMinimumIdle(t0DbProperties.getMinIdle());
        hikariDataSource.setConnectionTimeout(t0DbProperties.getConnectionTimeout());
        hikariDataSource.setIdleTimeout(t0DbProperties.getIdleTimeout());
        logger.info("Setup of t0DataSource succeeded.");
        return hikariDataSource;
    }

    @Primary
    @Bean(name = "t0EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean t0EntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        // Set the t0 data source
        em.setDataSource(t0DataSource());
        // The t0 tenant entity and repository need to be scanned
        em.setPackagesToScan(new String[]{T0Tenant.class.getPackage().getName(), T0TenantRepository.class.getPackage().getName()});
        // Setting a name for the persistence unit as Spring sets it as
        // 'default' if not defined
        em.setPersistenceUnitName("t0db-persistence-unit");
        // Setting Hibernate as the JPA provider
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        // Set the hibernate properties
        em.setJpaProperties(hibernateProperties());
        logger.info("Setup of t0EntityManagerFactory succeeded.");
        return em;
    }

    @Bean(name = "t0TransactionManager")
    public JpaTransactionManager t0TransactionManager(@Qualifier("t0EntityManagerFactory") EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    //Hibernate configuration properties
    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
        properties.put(org.hibernate.cfg.Environment.FORMAT_SQL, true);
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "none");
        return properties;
    }
}
