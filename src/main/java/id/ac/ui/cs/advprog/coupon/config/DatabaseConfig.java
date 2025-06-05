package id.ac.ui.cs.advprog.coupon.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:}")
    private String propertyUrl;

    @Value("${spring.datasource.username:}")
    private String propertyUsername;

    @Value("${spring.datasource.password:}")
    private String propertyPassword;

    @Bean
    public DataSource dataSource() {
        // Ambil dari environment kalau ada
        String pgHost = System.getenv("PGHOST");
        String pgDatabase = System.getenv("PGDATABASE");
        String pgUser = System.getenv("PGUSER");
        String pgPassword = System.getenv("PGPASSWORD");

        // Bangun URL sendiri
        String url = (pgHost != null && pgDatabase != null)
                ? "jdbc:postgresql://" + pgHost + ":5432/" + pgDatabase + "?sslmode=require"
                : propertyUrl;

        String username = (pgUser != null) ? pgUser : propertyUsername;
        String password = (pgPassword != null) ? pgPassword : propertyPassword;

        logger.info("[DatabaseConfig] DB URL: {}", url);
        logger.info("[DatabaseConfig] DB User: {}", username);
        logger.info("[DatabaseConfig] Password Length: {}", (password != null) ? password.length() : 0);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url.trim());
        dataSource.setUsername(username.trim());
        dataSource.setPassword(password.trim());
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}
