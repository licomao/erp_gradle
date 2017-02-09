package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.PurchaseOrderDetail;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by mdc on 2015/9/22.
 */
public interface CustomStockItemRepositoryInterface {

    List<CustomStockItem> calCustomStockInfo(int page, int pageSize, Shop shop, Organization organization, CustomStockItem customStockItem, Date createdDate);

    int calCustomStockInfoCounts(Shop shop, Organization organization, CustomStockItem customStockItem, Date createdDate);

    List<CustomStockItem> calForStockingOrder(Shop shop, Organization organization, Date createdDate);

    int getStockNumber( Shop shop, Organization organization, CustomStockItem customStockItem, Date createdDate);

}
