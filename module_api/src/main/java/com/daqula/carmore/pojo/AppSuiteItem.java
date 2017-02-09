package com.daqula.carmore.pojo;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AppSuiteItem {

    public long suiteItemId;

    @JsonSerialize(using = BaseEntity.IDSerializer.class)
    public CustomerPurchasedSuiteItem purchasedSuiteItemId;

    /** 套餐项目对应的商品SKU */
    public SkuItem skuItem;

    /** 该项目可以使用的次数，理论上应该等于timesLeft+usedTimes，但有可能套餐调整过。*/
    public int times;

    /** 剩余次数 */
    public int timesLeft;

    /** 已用掉的次数 */
    public int usedTimes;

    /** 已预约 */
    public boolean appointed;
}
