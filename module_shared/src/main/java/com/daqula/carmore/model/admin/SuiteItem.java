package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;
import org.springframework.data.annotation.Transient;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 套餐项目
 */
@Entity
public class SuiteItem extends BaseEntity {

    /** 套餐项目对应的商品SKU */
    @ManyToOne
    public SkuItem skuItem;

    /** 该项目可以使用的次数, <=0 表示无限次 */
    public int times;

    /** 成本 */
    public double cost;

    /** 上架的分类 */
    public int saleCategory;

    /** 剩余次数,客户端显示需要 */
    @Transient
    public int timesLeft;

    /** 已用掉的次数，客户端显示需要 */
    @Transient
    public int usedTimes;

    public SuiteItem clone() {
        SuiteItem clone = new SuiteItem();
        clone.skuItem = this.skuItem;
        clone.times = this.times;
        return clone;
    }

}
