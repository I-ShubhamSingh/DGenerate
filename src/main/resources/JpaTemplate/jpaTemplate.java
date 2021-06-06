/**	
 * This is an auto generated configuration file.
 * Please add <b>spring-boot-starter-data-jpa</b> dependency in pom.xml if using Maven or in build.gradle 
 * 
 * <b> Paste this file in your destination package <h5><i>[package must be added to build path] </i></h5>
 * 
 * @author Shubham Singh
*/
package DestinationPackage;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(basePackages = "RepoBasePackage", entityManagerFactoryRef = "EntityManagerBean", transactionManagerRef = "PlatformTransactionBean")
@EnableTransactionManagement()
public class NewConfig extends HikariConfig {

	@Autowired
	private Environment env;

	@Primary
	@Bean("DataSourceBean")
	@ConfigurationProperties(prefix = "spring.datasource")
	public HikariDataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
		return dataSource;
	}

	@Primary
	@Bean("EntityManagerBean")
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean(
			EntityManagerFactoryBuilder entityManagerFactoryBuilder,
			@Qualifier("DataSourceBean") HikariDataSource datasource) {
		return entityManagerFactoryBuilder.dataSource(datasource).packages("EntityPackage")
				.persistenceUnit("PersistentUnit").build();
	}

	@Primary
	@Bean("PlatformTransactionBean")
	public PlatformTransactionManager platformTransactionManager(
			@Qualifier("EntityManagerBean") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
