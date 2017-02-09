package com.daqula.carmore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Properties;

@SpringBootApplication
public class CarmoreConsole {

    @Autowired
    protected FreeMarkerProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(CarmoreConsole.class, args);
    }

}
