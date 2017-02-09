package com.daqula.carmore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class TestDataConfig {

    private final static Log log = LogFactory.getLog(TestDataConfig.class);

    @Configuration
    @ConditionalOnClass(Flyway.class)
    @ConditionalOnProperty(prefix = "flyway", name = "enabled", matchIfMissing = true)
    @AutoConfigureAfter(FlywayAutoConfiguration.class)
    public static class extFlywayConfig {

        @Autowired
        private CarmoreProperties properties;

        @Autowired
        private DataSource dataSource;

        @Autowired
        private FlywayProperties flywayProperties;

        @PostConstruct
        public void migrateTestData() {
            if (properties.isDebug()) {
                Flyway flyway = new Flyway();
                flyway.setDataSource(dataSource);
                flyway.setLocations("classpath:/db_testdata");
                flyway.setTable("schema_version_debug");
                try {
                    flyway.baseline();
                } catch (FlywayException ignored) {
                    log.info("Test data is baselined.");
                }
                flyway.migrate();
            }
        }
    }
}
