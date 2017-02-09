package com.daqula.carmore.model.order;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.shop.Shop;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 评论
 */
@Entity
public class Comment extends BaseEntity {

    /** 门店 */
    @JsonSerialize(using = IDSerializer.class)
    @ApiJsonIgnore
    @ManyToOne(optional = false)
    public Shop shop;

    /** 留下评论的顾客 */
    @ManyToOne(optional = false)
    @ApiJsonIgnore
    public CustomerAppProfile customer;

    /** 结算单 */
    @ManyToOne(optional = false)
    @ApiJsonIgnore
    public SettleOrder settleOrder;

    /** 评分项1 */
    public int rating1;

    /** 评分项2 */
    public int rating2;

    /** 评分项3 */
    public int rating3;

    /** 评分项4 */
    public int rating4;

    /** 评分项5 */
    public int rating5;

    /** 评分项6 */
    public int rating6;

    /** 评分项7 */
    public int rating7;

    /** 文字评论 */
    public String comment;
}
