package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.shop.SaleShelf;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 销售结算单
 */
@Entity
public class SettleOrder extends BaseEntity {

    /** 车 */
    @ManyToOne
    public VehicleInfo vehicleInfo;

    /** 顾客 */
    @ManyToOne
    public Customer customer;

    /** 订单分类 */
    public int saleCategory;

    /** 要在哪个门店进行服务 */
    @ManyToOne(optional = false)
    public Shop shop;

    /** 接车人员 */
    @ManyToOne
    public Staff receiver;

    /** 销售人员 */
    @ManyToOne
    public ERPUser saler;

    /** 是否已评论 */
    public boolean commented;

    /** 是否挂帐 */
    public boolean credit;

    /** 是否结算 */
    public boolean isFinish;

    /** 如果是App支付，是否与达丘拉完成结算 */
    public boolean close;

    /** 订单详情 */
    @OneToMany(cascade = CascadeType.ALL,
                orphanRemoval = true)
    @JoinColumn(name = "settle_order_id")
    public List<OrderDetail> orderDetails;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "settle_order_id")
    public List<OperationItemDetail> operationItemDetails;

    /** 预约订单，如果顾客不是通过App下单则为空 */
    @OneToOne
    public PresaleOrder presaleOrder;

    /** 结算日期 */
    @DateTimeFormat(pattern="yyyy/MM/dd")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime finishDate;

    /** 施工单 */
    @OneToOne
    public ProcessingOrder processingOrder;

    /** 付款记录 */
    @OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    public Payment payment;

    /** 销售单号 */
    public Long saleNo;

    /** 显示用销售单号 */
    public String saleNoView;

    /**备注**/
    public String remark;

    /**
     * 会员卡购买记录
     */
    @OneToOne
    public CustomerPurchasedSuite customerPurchasedSuite;

    public void fill(Shop shop, int saleCategory, Customer customer,
                     Suite suite, UUID uuid, Payment payment) {
        this.payment = payment;
        this.uid = uuid;
        this.customer = customer;
        this.saleCategory = saleCategory;
        this.shop = shop;
        this.orderDetails = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.orderedSuite = suite;
        this.orderDetails.add(orderDetail);
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
                        detail -> detail.orderedItem != null ? detail.orderedItem.name : "维修").orElse("维修");
            default:
                return "未知服务";
        }
    }
}
