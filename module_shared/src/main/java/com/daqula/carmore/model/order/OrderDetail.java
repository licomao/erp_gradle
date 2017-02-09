package com.daqula.carmore.model.order;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.CareSuite;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import com.daqula.carmore.model.shop.Staff;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.IOException;

/**
 * 订单项目
 */
@Entity
public class OrderDetail extends BaseEntity {

    /** 购买了哪个商品 */
    @ManyToOne
    public SkuItem orderedItem;

    /** 购买了哪个套餐 */
    @ManyToOne
    public Suite orderedSuite;

    /** 从哪个套餐项目里划扣 */
    @ManyToOne
    @JsonProperty("fromSuiteName")
    @JsonSerialize(using=PurchasedSuiteNameSerializer.class)
    public CustomerPurchasedSuiteItem fromPurchasedSuiteItem;

    /** 选择了哪个保养套餐 */
    @ManyToOne
    @ApiJsonIgnore
    public CareSuite referenceCareSuite;

    /** 数量 */
    public int count;

    /** 折扣率 */
    @ApiJsonIgnore
    public double discount;

    /** 实际应收金额 */
    public double receivable;

    /** 折扣售价 */
    public double discountPrice;

    /**
     *  库存成本为了和采购商品的明细对应.
     */
    @ApiJsonIgnore
    public double cost;

    /** 折扣授权人 */
    @ManyToOne
    @ApiJsonIgnore
    public ERPUser discountGranter;

    /** 施工人员 */
    @ManyToOne
    @ApiJsonIgnore
    public Staff merchandier;

    public void build(Integer skuCount, SkuItem skuItem) {
        this.orderedItem = skuItem;
        this.count = (skuCount == 0 ? 1 : skuCount);
    }

    public void build(CustomerPurchasedSuiteItem customerPurchasedSuiteItem) {
        this.orderedItem = customerPurchasedSuiteItem.customStockItem;
        this.fromPurchasedSuiteItem = customerPurchasedSuiteItem;
    }

    public void build(CareSuite careSuite, SkuItem skuItem) {
        this.orderedItem = skuItem;
        this.referenceCareSuite = careSuite;
    }

    //**************************************************************************
    // Serialization / Deserialization
    //**************************************************************************

    public static class PurchasedSuiteNameSerializer extends JsonSerializer<CustomerPurchasedSuiteItem> {
        @Override
        public void serialize(CustomerPurchasedSuiteItem value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            jgen.writeString(value.purchasedSuite.suite.name);
        }
    }

}
