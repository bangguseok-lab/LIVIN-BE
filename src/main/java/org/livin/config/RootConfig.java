package org.livin.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource({"classpath:/application.properties"})
@ComponentScan(basePackages = {"org.livin.property.service", "org.livin.user.service", "org.livin.auth.service",
	"org.livin.checklist.service", "org.livin.global.exception", "org.livin.risk.service",
	"org.livin.global.codef.service"})
@MapperScan(basePackages = {"org.livin.property.mapper", "org.livin.user.mapper", "org.livin.checklist.mapper"})
public class RootConfig {
	@Value("${jdbc.driver}")
	String driver;

	@Value("${jdbc.url}")
	String url;

	@Value("${jdbc.username}")
	String username;

	@Value("${jdbc.password}")
	String password;

	@Bean
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		HikariDataSource dataSource = new HikariDataSource(config); //데이터소스 커넥션 풀
		return dataSource;
	}

	@Autowired
	ApplicationContext applicationContext;

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));

		// Mapper XML 파일 위치 설정 (src/main/resources/mapper 하위 구조 포함)
		// sqlSessionFactory.setMapperLocations(
		//     new PathMatchingResourcePatternResolver().getResources("classpath:/org/livin/mapper/**/*.xml")
		// );

		// DB 연결 설정
		sqlSessionFactory.setDataSource(dataSource());

		// 최종 SqlSessionFactory 객체 반환
		return (SqlSessionFactory)sqlSessionFactory.getObject();
		// return sqlSessionFactory.getObject();
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource());
		return manager;
	}

}