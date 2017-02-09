package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.*;
import java.util.List;

/**
 * 施工单
 */
@Entity
public class ProcessingOrder extends BaseEntity {

    /** 顾客 */
    @ManyToOne(optional = false)
    public Customer customer;

    /** 要在哪个门店进行服务 */
    @ManyToOne(optional = false)
    public Shop shop;

    /** 是否已取消 */
    public boolean cancelled;

    /** 订单详情 */
    @OneToMany(cascade = CascadeType.ALL,
                orphanRemoval = true)
    @JoinColumn(name = "processing_order_id")
    public List<OrderDetail> orderDetails;

    /** 预约订单，如果顾客不是通过App下单则为空 */
    @OneToOne
    public PresaleOrder presaleOrder;

}
