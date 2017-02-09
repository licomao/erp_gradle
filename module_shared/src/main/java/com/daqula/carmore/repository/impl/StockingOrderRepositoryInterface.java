package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by mdc on 2015/9/26.
 */
public interface StockingOrderRepositoryInterface {

    Date findLastStockingDate(Shop shop);
}
