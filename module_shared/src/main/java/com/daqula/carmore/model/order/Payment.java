package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.customer.Customer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 顾客付款记录
 */
@Entity
public class Payment extends BaseEntity {

    /** APP在线支付 */
    public static final int PAYMENT_TYPE_APP    = 1;
    /** POS机消费 */
    public static final int PAYMENT_TYPE_POS    = 2;
    /** 现金支付 */
    public static final int PAYMENT_TYPE_CASH   = 3;

    /** APP在线支付chargeId */
    public String chargeId;

    /** 付款的顾客 */
    @ManyToOne(optional = false)
    public Customer customer;

    /** 初始付费金额 */
    @Column(nullable = false)
    public double amount;

    /** 通过App支付SDK支付了多少钱 */
    public double appAmount;

    /** 通过刷POS机支付了多少钱 */
    public double posAmount;

    /** 通过现金支付了多少钱 */
    public double cashAmount;

    /** 第三方支付金额*/
    public double otherAmount;

    /** 最终结算时调整金额，可为负。
     * 如果订单是预付的，即amount不为空，可通过movement调整最终金额。
     * amount+movement即为每张订单的最终金额 */
    @Column(nullable = false)
    public double movement;

    /** 通过刷POS机支付了多少钱 */
    public double posMovement;

    /** 通过现金支付了多少钱 */
    public double cashMovement;

}
