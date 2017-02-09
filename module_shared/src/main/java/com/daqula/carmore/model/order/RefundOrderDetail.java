package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.CustomStockItem;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * 退货单明细
 * Created by mdc on 2015/9/15.
 */
@Entity
public class RefundOrderDetail extends BaseEntity {
    @OneToOne
    public CustomStockItem customStockItem;

    public int number;

    public double cost;

    /**
     * 用于存储库存数量
     */
    public double bankNumber;
}
