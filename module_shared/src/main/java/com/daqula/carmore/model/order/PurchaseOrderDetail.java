package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.CustomStockItem;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * 采购单明细
 * Created by mdc on 2015/9/11.
 */
@Entity
public class PurchaseOrderDetail extends BaseEntity {

    @OneToOne
    public CustomStockItem customStockItem;

    public int number;

    public double price;

    public int bankNumber;

    public double lastPrice;

}
