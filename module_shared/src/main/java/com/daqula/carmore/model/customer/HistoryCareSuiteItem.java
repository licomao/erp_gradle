package com.daqula.carmore.model.customer;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.Entity;

/**
 * 1.0会员卡消费明细
 * Created by mdc on 2016/2/4.
 */
@Entity
public class HistoryCareSuiteItem extends BaseEntity {

    public String plateNumber;

    public String name;

    public String cardNo;

    public String cardName;

    public String cardDetailName;

    public String shop;

    public int number;

}
