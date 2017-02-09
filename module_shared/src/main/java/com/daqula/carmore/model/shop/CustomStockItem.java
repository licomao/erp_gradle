package com.daqula.carmore.model.shop;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.StockItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * 门店自定义商品。同个组织下的商品可以在门店之间进行调拔
 */
@Entity
@DiscriminatorValue(value=SkuItem.SKU_TYPE_CUSTOM_STOCK)
public class CustomStockItem extends StockItem {

    /** 是哪个组织自定义的 */
    @ManyToOne(fetch = FetchType.LAZY)
    @ApiJsonIgnore
    @JsonIgnore
    public Organization organization;

    /** 用于库存计算读取字段 */
    @Column(updatable = false)
    public int number;

    /** 用于库存查询显示其真正ID */
    public String viewId;

    /**
     * 服务名称
     */
    @Transient
    public String serviceName;


}
