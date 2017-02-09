package com.daqula.carmore.model.admin;

import com.daqula.carmore.annotation.ApiJsonIgnore;

import javax.persistence.*;

/**
 * 服务项目，无需进出库
 */
@Entity
@DiscriminatorValue(value=SkuItem.SKU_TYPE_SERVICE)
public class ServiceItem extends SkuItem {

    /** 工时，手工费=工时*工时费 */
    @ApiJsonIgnore
    public double laborHours;

}
