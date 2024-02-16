package com.coralpay.config;

import com.digicore.encryptionlib.EncryptionUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * Created by Felix.Ike on 10/7/15.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {"com.coralpay"},
        entityManagerFactoryRef = "coralpayEntityManagerFactory",
        transactionManagerRef = "coralpayTransactionManager")
public class DataSourceConfig {



    @Value("${coralpay.datasource.url}")
    private String databaseUrl;

    @Value("${coralpay.datasource.username}")
    private String username;

    @Value("${coralpay.datasource.password}")
    private String password;

    @Value("${coralpay.datasource.max-active:180000}")
    private int maxActive;

    @Value("${coralpay.datasource.driver-class-name}")
    private String driverClass;

    @Value("${coralpay.datasource.validation-query}")
    private String validationQuery;

    @Value("${coralpay.datasource.maxIdle:20000}")
    private int maxIdle;


    @Value("${coralpay.datasource.maxWait:10}")
    private int maxWait;


    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.naming-strategy:com.coralpay.config.PhysicalNamingStrategyImpl}")
    private String hibernateNamingStrategy;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Value("${datasource.properties.encrypted:false}")
    private boolean datasourceIsEncrypted;


    @Bean
    @Primary
    public DataSource primaryDataSource() {
        String databaseUser = username;
        String databasePass = password;
        if(datasourceIsEncrypted) {
            databaseUser = encryptionUtil.decrypt(username);
            databasePass = encryptionUtil.decrypt(password);
        }
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setJdbcUrl(databaseUrl);
        dataSource.setUsername(databaseUser);
        dataSource.setPassword(databasePass);
        dataSource.setMaxLifetime(maxActive);
        dataSource.setIdleTimeout(maxIdle);
        dataSource.setMaximumPoolSize(maxWait);
        dataSource.setConnectionTestQuery(validationQuery);

        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean coralpayEntityManagerFactory() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(primaryDataSource());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setJpaProperties(hibernateProperties());
        factoryBean.setPackagesToScan("com.coralpay.model");

        return factoryBean;
    }

    private Properties hibernateProperties() {
        Properties prop = new Properties();
        prop.put("hibernate.ejb.naming_strategy", hibernateNamingStrategy);
        prop.put("hibernate.physical_naming_strategy", hibernateNamingStrategy);
        prop.put("hibernate.id.new_generator_mappings", false);
        prop.put("hibernate.dialect", hibernateDialect);
        prop.put("hibernate.use_sql_comments", false);

        return prop;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sf = new LocalSessionFactoryBean();
        sf.setDataSource(primaryDataSource());
        sf.setPackagesToScan("com.coralpay");
        sf.setHibernateProperties(hibernateProperties());
        return sf;
    }

    @Bean
    @Primary
    @DependsOn(value = "coralpayEntityManagerFactory")
    public PlatformTransactionManager coralpayTransactionManager() {
        return new JpaTransactionManager(coralpayEntityManagerFactory().getObject());
    }
}
