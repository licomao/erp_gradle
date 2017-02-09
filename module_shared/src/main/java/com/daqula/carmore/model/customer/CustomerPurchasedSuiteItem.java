package com.daqula.carmore.model.customer;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.SuiteItem;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * 顾客已购买套餐项目
 */
@Entity
public class CustomerPurchasedSuiteItem extends BaseEntity {

    @ManyToOne
    @ApiJsonIgnore
    @JsonIgnore
    public CustomerPurchasedSuite purchasedSuite;

    /** 购买了哪个具体套餐项目 */
    @ManyToOne(optional = false)
    public SuiteItem suiteItem;

    /** 具体套餐项目的商品ID, 因为历史原因, 这个字段只能叫这个名字了 */
    @ManyToOne(optional = false)
    public SkuItem customStockItem;

    /** 上架的分类 */
    public int saleCategory;

    /** 商品成本 */
    public Double cost;

    /** 该项目可以使用的次数, <=0 表示无限次 */
    public int times;

    /** 已用掉的次数 */
    public Integer usedTimes;

    @Transient
    public Integer stockItemNumber;

    public int getTimesLeft() {
        return times > 0 ? times - usedTimes : 0;
    }

}
