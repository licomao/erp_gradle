package com.daqula.carmore.model.shop;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.admin.ServiceItem;
import com.daqula.carmore.model.admin.SkuItem;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 门店自定义服务项目
 */
@Entity
@DiscriminatorValue(value=SkuItem.SKU_TYPE_CUSTOM_SERVICE)
public class CustomServiceItem extends ServiceItem {

    /** 是哪个门店自定义的 */
    @ManyToOne(optional = false)
    @ApiJsonIgnore
    public Organization organization;

}
