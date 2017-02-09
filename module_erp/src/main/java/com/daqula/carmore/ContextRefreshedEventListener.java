package com.daqula.carmore;

import com.daqula.carmore.util.JPAFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private CarmoreProperties properties;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (properties.isLoadJPAFixture()) {
            ApplicationContext applicationContext = event.getApplicationContext();
            JPAFixture fixture = new JPAFixture(applicationContext);

            boolean flag = false;
            if (flag) {
//                fixture.initOrganization();
//                fixture.initCustomerStock();
//                fixture.initUsers();
//                fixture.initSupplier();
////            fixture.initCustomSuiteAndSuiteItem();
//                fixture.initPreSaleOrder();
//                fixture.initStocking();
//                fixture.initCustomerErpProfile();
            }
//            fixture.initSettleOrderHistory();
        }
    }
}
