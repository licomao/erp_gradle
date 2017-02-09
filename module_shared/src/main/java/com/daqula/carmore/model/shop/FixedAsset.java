package com.daqula.carmore.model.shop;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 固定资产管理
 * Created by mdc on 2015/9/11.
 */
@Entity
public class FixedAsset extends BaseEntity {


    /** 固定资产名称 */
    public String name;

    /** 型号 */
    public String model;

    /** 单价 */
    public double price;

    /** 数量 */
    public int number;

    public double getSum(){
        return price * number;
    }

    /** 设备状态 在用=0，报废=-1 */
    public int assetStatus;

    /** 所属门店 */
    @ManyToOne
    public Shop shop;

    /**在用*/
    public final static int ASSET_STATUS_USE = 0;
    /**报废*/
    public final static int ASSET_STATUS_USELESS = -1;

}
