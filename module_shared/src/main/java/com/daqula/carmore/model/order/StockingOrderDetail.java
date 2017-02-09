package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.CustomStockItem;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 盘点明细单
 * Created by mdc on 2015/9/11.
 */
@Entity
public class StockingOrderDetail extends BaseEntity {

    /** 库存信息 */
    @ManyToOne
    public CustomStockItem customStockItem;

    /**盘前数量
     */
    public int oldNumber;

    /**盘后数量*/
    public int calculateNumber;

    /**
     *  库存成本为了和采购商品的明细对应.
     */
    public double stockCost;

}
