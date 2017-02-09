package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by mdc on 2015/10/22.
 */
public interface PaymentRepositoryInterface {

    Payment calSaleInfo(DateTime startTime, DateTime endTime, Shop shop);

    double getMaterialCal(DateTime startTime, DateTime endTime, Shop shop);
}