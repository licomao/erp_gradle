package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.Organization;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * 采购单付款记录
 * Created by mdc on 2016/1/20.
 */
@Entity
public class PurchasePayment extends BaseEntity{

    /**付款编号*/
    public long payNo;

    /** 编号view*/
    public String payNoView;

    /**供应商*/
    @ManyToOne
    public Supplier supplier;

    /*付款日期*/
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime payDate;

    /** 付款方式 1：现金；2：银行汇款;3：支票 */
    public int payType;

    /** 付款去向 */
    public String payWay;

    /** 结余款 */
//    public double unspentBalance;

    /** 应付款 */
    public double accountPayable;

    /** 发票欠款 */
    public double invoiceArrears;

    /** 本次付款 */
    public double payment;

    /** 抵扣金额 */
    public double deductionPayment;

    /** 对应采购单**/
    @ManyToOne
    public PurchaseOrder purchaseOrder;

    /** 付款账号*/
    public String payAccount;

    /** 所属组织*/
    @OneToOne
    public Organization organization;

}
