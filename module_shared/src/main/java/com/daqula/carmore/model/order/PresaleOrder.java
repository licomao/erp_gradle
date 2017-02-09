package com.daqula.carmore.model.order;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.CareSuite;
import com.daqula.carmore.model.admin.CareSuiteGroupItem;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.shop.SaleShelf;
import com.daqula.carmore.model.shop.Shop;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * App预约订单
 */
@Entity
public class PresaleOrder extends BaseEntity {

    /** 订单来源 - App */
    public static final int ORDER_SOURCE_APP = 0;
    /** 订单来源 - ERP */
    public static final int ORDER_SOURCE_ERP = 1;

    /** 订单分类 */
    public int saleCategory;

    @ManyToOne
    public VehicleInfo vehicleInfo;

    /** 顾客 */
    @ManyToOne(optional = false)
    public Customer customer;

    @Transient
    public String customerName;

    /** 要在哪个门店进行服务 */
    @ManyToOne(optional = false)
    public Shop shop;

    /** 订单详情 */
    @OneToMany(cascade = CascadeType.ALL,
               orphanRemoval = true)
    @JoinColumn(name = "presale_order_id")
    public List<OrderDetail> orderDetails;

    /** 预约时间 */
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime appointmentDate;

    /** 维修预约的图片URL */
    @ElementCollection
    public List<String> images;

    /** 维修预约，顾客提交的描述 */
    public String description;

    /** 是否已取消 */
    public boolean cancelled;

    /** 付款记录 */
    @OneToOne
    public Payment payment;

    /** 结算订单，服务完成以后设置 */
    @JsonIgnore
    @ApiJsonIgnore
    @OneToOne
    public SettleOrder settleOrder;

    /** 订单来源 */
    public int source;

    // 配件预约
    public void fillSkuItemPresale(DateTime appointmentDate, Customer customer, VehicleInfo vehicleInfo,
                                   Shop shop, SkuItem skuItem, int count, UUID uuid, int saleCategory, Payment payment) {
        fillCommonProperties(appointmentDate, customer, shop, vehicleInfo, saleCategory, payment);
        this.uid = uuid;
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.build(count, skuItem);
        this.orderDetails.add(orderDetail);
    }

    // 保养套餐预约
    public void fillCareSuitePresale(DateTime appointmentDate, Customer customer, VehicleInfo vehicleInfo,
                                     Shop shop, List<CareSuiteGroupItem> careSuiteGroupItems,
                                     CareSuite careSuite, UUID uuid, Payment payment) {
        fillCommonProperties(appointmentDate, customer, shop, vehicleInfo, SaleShelf.CATEGORY_CARE, payment);
        this.uid = uuid;
        // 保养套餐的单项是可以有不同的选择，所以要保存用户选了哪项
        for (CareSuiteGroupItem careSuiteGroupItem : careSuiteGroupItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.build(careSuite, careSuiteGroupItem.skuItem);
            this.orderDetails.add(orderDetail);
        }
    }

    public void fillSuite(DateTime appointmentDate, Customer customer, VehicleInfo vehicleInfo, int saleCategory,
                          CustomerPurchasedSuite customerPurchasedSuite, CustomerPurchasedSuiteItem purchasedSuiteItem) {
        fillCommonProperties(appointmentDate, customer, customerPurchasedSuite.shop,
                vehicleInfo, saleCategory, null);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.build(purchasedSuiteItem);
        this.orderDetails.add(orderDetail);
    }

    public void fillRepairPresale(DateTime appointmentDate, Customer customer, VehicleInfo vehicleInfo, Shop shop,
                           List<String> images, String description) {
        fillCommonProperties(appointmentDate, customer, shop, vehicleInfo, SaleShelf.CATEGORY_REPAIR, null);
        this.images = images;
        this.description = description;
    }

    private void fillCommonProperties(DateTime appointmentDate, Customer customer, Shop shop,
                                      VehicleInfo vehicleInfo, int saleCategory, Payment payment) {
        this.payment = payment;
        this.appointmentDate = appointmentDate;
        this.customer = customer;
        this.shop = shop;
        this.vehicleInfo = vehicleInfo;
        this.orderDetails = new ArrayList<>();
        this.source = ORDER_SOURCE_APP;
        this.saleCategory = saleCategory;
    }

    public String getTitle() {
        switch (saleCategory) {
            case SaleShelf.CATEGORY_WASH_CAR:
                return orderDetails.stream().findFirst().map(
                        detail -> detail.orderedItem != null ? detail.orderedItem.name : "洗车").orElse("洗车");
            case SaleShelf.CATEGORY_BEAUTIFY:
                return orderDetails.stream().findFirst().map(
                        detail -> detail.orderedItem != null ? detail.orderedItem.name : "美容").orElse("美容");
            case SaleShelf.CATEGORY_CARE:
                return orderDetails.stream().findFirst().map(
                        detail -> detail.referenceCareSuite != null ? detail.referenceCareSuite.name : "保养").orElse("保养");
            case SaleShelf.CATEGORY_ACCESSORY:
                return orderDetails.stream().findFirst().map(
                        detail -> detail.orderedItem != null ? detail.orderedItem.name : "配件").orElse("配件");
            case SaleShelf.CATEGORY_REPAIR:
                return orderDetails.stream().findFirst().map(
                        detail -> detail.orderedItem != null ? detail.orderedItem.name : "维修预约").orElse("维修预约");
            default:
                return "未知服务";
        }
    }

}
