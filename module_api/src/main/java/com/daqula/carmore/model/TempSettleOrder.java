package com.daqula.carmore.model;

import javax.persistence.Entity;

/**
 * 销售临时结算单
 */
@Entity
public class TempSettleOrder extends BaseEntity {

    public long shopId;

    public long customerId;

    public long suiteId;

    public double price;
}
