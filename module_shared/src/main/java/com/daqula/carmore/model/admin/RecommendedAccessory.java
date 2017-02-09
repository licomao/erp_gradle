package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 配件推广
 */
@Entity
public class RecommendedAccessory extends BaseEntity {

    /** 优惠价格 */
    public double price;

    /** 配件 */
    @ManyToOne
    public SkuItem skuItem;

}
