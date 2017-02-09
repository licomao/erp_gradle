package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.shop.CustomStockItem;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * 耗材领用单明细
 * Created by mdc on 2015/9/11.
 */
@Entity
public class MaterialOrderDetail extends BaseEntity {

    /** 耗材商品 */
    @OneToOne
    public CustomStockItem customStockItem;

    /** 领用数量 */
    public int number;

    public double cost;


}
