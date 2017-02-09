package com.daqula.carmore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.daqula.carmore.repository", "com.daqula.carmore.model"})
@EntityScan(basePackages = {"com.daqula.carmore.model","com.daqula.carmore.temp"})
public class CarmoreApi {

    public static void main(String[] args) {
        SpringApplication apiApplication = new SpringApplication(CarmoreApi.class);
        //apiApplication.addListeners(new ContextStartedEventListener());
        apiApplication.run(args);
    }

    @Bean
    public JsonBasicAuthenticationEntryPoint jsonBasicAuthenticationEntryPoint() {
        JsonBasicAuthenticationEntryPoint entryPoint = new JsonBasicAuthenticationEntryPoint();
        entryPoint.setRealmName("Spring Security Application");
        return entryPoint;
    }

}
