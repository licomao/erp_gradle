package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.CustomStockItem;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 库存调拨明细单
 * Created by mdc on 2015/9/21.
 */
@Entity
public class StockTransferOrderDetail extends BaseEntity{

    /** 库存信息 */
    @ManyToOne
    public CustomStockItem customStockItem;

    /**调拨数量*/
    public int number;

    /**
     * 调拨前库存数量
     */
    public int beforeNumber;

    /**
     *  库存成本为了和采购商品的明细对应.
     */
    public double cost;
}
