package com.daqula.carmore.model.customer;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.Entity;

/**
 * 1.0会员卡 这个模型只做临时使用
 * Created by mdc on 2016/2/2.
 */
@Entity
public class HistoryCustomerCardSuite extends BaseEntity {

    public String name;

    public String palteName;

    public String cardNo;

    public String cardName;

    public String mobile;

    public String shop;


}
