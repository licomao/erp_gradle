package com.daqula.carmore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;

@SpringBootApplication
public class CarmoreErp {

    @Autowired
    protected FreeMarkerProperties properties;

    public static void main(String[] args) {
        SpringApplication.run(CarmoreErp.class, args);
    }

}
