package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 采购单
 * Created by mdc on 2015/9/11.
 */
@Entity
public class PurchaseOrder extends BaseEntity {

    /** 采购单据
     */
    public long orderNumber;

    /** 采购单据
     */
    public String orderNumberView;

    /** 采购门店
     */
    @ManyToOne
    public Shop purchaseShop;

    /** 审批门店
     */
    @ManyToOne
    public Shop saleShop;

    /** 供应商
     */
    @ManyToOne
    public Supplier supplier;

    /** 采购类型
     */
    public int purchaseType;

    /** 备注
     */
    public String remark;

    /**
     * 申请人
     */
    public String applyPerson;

    /**
     * 审批人
     */
    public String reviewPerson;

    /**
     * 入库人
     */
    public String inStockPerson;

    /**
     * 销售单号
     */
    public String saleNo;
//    public SettleOrder

    /** 订单状态 待审批=0，审批通过=1，未通过=2，已入库=3 */
    public int orderStatus;

    /**领用单明细*/
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "purchase_order_id")
    public List<PurchaseOrderDetail> purchaseOrderDetailList;


    public Double getCostSum(){
        double sum = 0;
        for (PurchaseOrderDetail pd : purchaseOrderDetailList){
            sum += pd.price * pd.number;
        }
        return sum;
    }


    /**
     * 采购日期起    查询日期区间用
     */
    @Transient
    public Date purchaseDateStart;

    /**
     * 采购类型显示名称
     */
    @Transient
    public String purchaseTypeName;

    /**
     * 采购日期止    查询日期区间用
     */
    @Transient
    public Date purchaseDateEnd;

    @Transient
    public double unspentCost;

}
