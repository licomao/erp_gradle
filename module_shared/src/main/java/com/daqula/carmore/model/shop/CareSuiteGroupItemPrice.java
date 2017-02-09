package com.daqula.carmore.model.shop;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.CareSuiteGroupItem;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * 门店自定义保养套餐项目的价格
 */
@Entity
public class CareSuiteGroupItemPrice extends BaseEntity {

    /** 是哪个组织自定义的 */
    @ApiJsonIgnore
    @ManyToOne
    public Organization organization;

    @OneToOne
    public CareSuiteGroupItem careSuiteGroupItem;

    /** 组织自定义的价格 */
    public Double overriddenPrice;

}
