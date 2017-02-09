package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 供应退货单
 * Created by mdc on 2015/9/15.
 */
@Entity
public class RefundOrder extends BaseEntity {

    /** 退货单据编号
     */
    public long orderNumber;

    /** 退货单据编号(显示)
     */
    public String orderNumberView;

    /**
     * 退货门店
     */
    @ManyToOne
    public Shop refundShop;

    /**
     * 供应商
     */
    @ManyToOne
    public Supplier supplier;

    /** 订单状态 待审批=0，审批通过=1*/
    public int orderStatus;

    /** 备注
     */
    public String remark;

    /**
     * 退货申请人
     */
    public String applyPerson;

    /**退货单明细*/
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "refund_order_id")
    public List<RefundOrderDetail> refundOrderDetails;

    /**
     * 采购日期起    查询日期区间用
     */
    @Transient
    public Date refundDateStart;

    /**
     * 采购日期止    查询日期区间用
     */
    @Transient
    public Date refundDateEnd;
}
