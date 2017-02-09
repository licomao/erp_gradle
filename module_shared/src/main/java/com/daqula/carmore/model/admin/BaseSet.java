package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * 基础数据设置
 * Created by mdc on 2015/9/11.
 */
@Entity
public class BaseSet extends BaseEntity{

    /**工时单价*/
    public double operationPrice;

    /** pos机费率 */
    public String posRate;

    /** pos机封顶费率 */
    public String posTopRate;

    /** 所属组织 */
    @OneToOne
    public Organization organization;

    /** 所属门店 */
    @OneToOne
    public Shop shop;

    /***
     * 是否验证盘点
     */
    public boolean isCheckPd;

    /***
     * 盘点日期
     */
    public int checkDay = 1;

}
