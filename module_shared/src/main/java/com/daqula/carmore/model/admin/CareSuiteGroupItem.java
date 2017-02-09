package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.template.VehicleModel;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

/**
 * 保养套餐具体项目，同个CareSuiteGroup可替换
 */
@Entity
public class CareSuiteGroupItem extends BaseEntity {

    /** 套餐项目商品或服务 */
    @ManyToOne
    public SkuItem skuItem;

    /** 对应车型的档位 */
    @Enumerated
    public VehicleModel.VehicleClass forClazz;

    /** 套餐价 */
    public double suitePrice;

    public CareSuiteGroupItem clone() {
        CareSuiteGroupItem careSuiteGroupItem = new CareSuiteGroupItem();
        careSuiteGroupItem.skuItem = this.skuItem;
        careSuiteGroupItem.suitePrice = this.suitePrice;
        careSuiteGroupItem.forClazz = this.forClazz;
        return careSuiteGroupItem;
    }

}
