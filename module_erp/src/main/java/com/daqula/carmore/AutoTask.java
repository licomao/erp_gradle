package com.daqula.carmore;

import com.daqula.carmore.repository.SettleAccountsRepository;
import com.daqula.carmore.repository.ShopRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Created by mdc on 2016/2/22.
 */
@Component
public class AutoTask {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private SettleAccountsRepository settleAccountsRepository;

    @Scheduled(fixedDelay=10)
    public void notifiy(){
        DateTime dateTime = new DateTime();
//        if (dateTime.getHourOfDay() == 23){
            System.out.println("start settle today!");
//        }


    }

//    public static void main(String[] args) {
//        DateTime dateTime = new DateTime();
//        System.err.println();
//    }
}
