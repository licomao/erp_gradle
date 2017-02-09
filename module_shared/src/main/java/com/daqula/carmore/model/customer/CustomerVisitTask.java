package com.daqula.carmore.model.customer;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.shop.Shop;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 顾客回访记录
 */
@Entity
public class CustomerVisitTask extends BaseEntity {

    /** 解除绑定 */
    public static final int CUSTOMER_VISIT_TASK_TYPE_UNBIND = 1;
    /** 会员过期 */
    public static final int CUSTOMER_VISIT_TASK_TYPE_VIP_EXPIRED = 2;
    /** 预约迟到 */
    public static final int CUSTOMER_VISIT_TASK_TYPE_APPOINTMENT_LATE = 3;
    /** 维修回访 */
    public static final int CUSTOMER_VISIT_TASK_TYPE_REPAIR = 4;

    /** 顾客 */
    @ManyToOne(optional = false)
    public Customer customer;

    /** 所属门店 */
    @ManyToOne(optional = false)
    public Shop shop;

    /** 回访类型 */
    public int type;

    /** 回访原因 */
    public String reason;

    /** 是否已处理 */
    public boolean done;

    /** 回访记录 */
    public String note;
}
