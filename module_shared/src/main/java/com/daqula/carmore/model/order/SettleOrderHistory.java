package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.*;

/**
 * 本地会员异地消费 记录表
 */
@Entity
public class SettleOrderHistory extends BaseEntity {

    /** 销售单据 */
    @ManyToOne(optional = false)
    public SettleOrder settleOrder;

    /** 在哪个门店进行服务 */
    @OneToOne(cascade = CascadeType.PERSIST)
    public Shop shop;

    /** 所属门店 */
    @OneToOne(cascade = CascadeType.PERSIST)
    public Shop belongShop;


//    public Organization organization;

    /**
     * 是否签收 true已签收 false未签收
     */
    public boolean isSignFor;

}
