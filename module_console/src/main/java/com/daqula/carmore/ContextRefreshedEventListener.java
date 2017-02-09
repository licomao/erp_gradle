package com.daqula.carmore;

import com.daqula.carmore.util.FreeMarkerUtil;
import com.daqula.carmore.util.JPAFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Component
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    public FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private CarmoreProperties properties;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        freeMarkerConfigurer.getConfiguration().setSharedVariable("AUTHORITY", FreeMarkerUtil.getAuthorityModel());

        if (properties.isLoadJPAFixture()) {
            ApplicationContext applicationContext = event.getApplicationContext();
//            JPAFixture fixture = new JPAFixture(applicationContext);
//            fixture.initOrg();
        }
    }
}
