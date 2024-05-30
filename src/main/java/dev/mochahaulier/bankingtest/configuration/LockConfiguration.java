package dev.mochahaulier.bankingtest.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;

import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LockConfiguration {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DefaultLockRepository lockRepository(JdbcTemplate jdbcTemplate) {
        return new DefaultLockRepository(jdbcTemplate.getDataSource());
    }

    @Bean
    public JdbcLockRegistry lockRegistry(DefaultLockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }
}
