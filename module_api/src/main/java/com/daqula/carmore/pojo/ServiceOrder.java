package com.daqula.carmore.pojo;

import com.daqula.carmore.model.order.Comment;
import com.daqula.carmore.model.order.OrderDetail;
import com.daqula.carmore.model.order.PresaleOrder;
import com.daqula.carmore.model.order.SettleOrder;
import org.joda.time.DateTime;

import javax.persistence.ElementCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 拼装数据给客户端使用
 */
public class ServiceOrder {

    /** 订单创建中，未付款 */
    public static final int ORDER_STATE_DRAFT = 0;
    /** 已付款，后面未确认 */
    public static final int ORDER_STATE_PAID = 1;
    /** 订单已预约 */
    public static final int ORDER_STATE_SUBSCRIBED = 2;
    /** 订单待评价 */
    public static final int ORDER_STATE_NEED_COMMENT = 3;
    /** 订单已完成 */
    public static final int ORDER_STATE_DONE = 4;

    public UUID uid;

    /** 要在哪个门店进行服务 */
    public long shopId;

    /** 门店名称 */
    public String shopName;

    /** 预约了哪台车去做服务 */
    public long vehicleInfoId;

    /** 预约了哪台车去做服务 */
    public String vehicleName;

    /** 预约时间 */
    public DateTime appointmentDate;

    /** 服务完成时间 */
    public DateTime closeDate;

    /** 订单详情 */
    public List<OrderDetail> orderDetails;

    /** 付款总额 */
    public double payment;

    /** 订单状态 */
    public int state;

    /** 上架销售分类 */
    public int saleCategory;

    /** 订单标题 */
    public String title;

    /** 维修预约的图片URL */
    public List<String> images;

    /** 维修预约，顾客提交的描述 */
    public String description;

    //**************************************************************************
    // Builder Methods
    //**************************************************************************

    public static ServiceOrder buildFromSettleOrder(SettleOrder settleOrder, int state) {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.uid = settleOrder.uid;
        serviceOrder.state = state;
        if (settleOrder.presaleOrder != null) {
            serviceOrder.appointmentDate = settleOrder.presaleOrder.appointmentDate;
        }
        serviceOrder.shopId = settleOrder.shop.id;
        serviceOrder.shopName = settleOrder.shop.name;
        if (settleOrder.payment != null) {
            serviceOrder.payment = settleOrder.payment.amount;
        }
        serviceOrder.orderDetails = settleOrder.orderDetails;
        if (settleOrder.vehicleInfo != null) {
            serviceOrder.vehicleInfoId = settleOrder.vehicleInfo.id;
            serviceOrder.vehicleName = settleOrder.vehicleInfo.getName();
        }
        serviceOrder.closeDate = settleOrder.createdDate;
        serviceOrder.saleCategory = settleOrder.saleCategory;
        serviceOrder.title = settleOrder.getTitle();
        return serviceOrder;
    }

    public static ServiceOrder buildFromPresaleOrder(PresaleOrder presaleOrder, int state) {
        ServiceOrder serviceOrder = new ServiceOrder();
        serviceOrder.uid = presaleOrder.uid;
        serviceOrder.state = state;
        serviceOrder.appointmentDate = presaleOrder.appointmentDate;
        serviceOrder.shopId = presaleOrder.shop.id;
        serviceOrder.shopName = presaleOrder.shop.name;
        if (presaleOrder.payment != null) serviceOrder.payment = presaleOrder.payment.amount;
        serviceOrder.orderDetails = presaleOrder.orderDetails;
        if (presaleOrder.vehicleInfo != null) {
            serviceOrder.vehicleInfoId = presaleOrder.vehicleInfo.id;
            serviceOrder.vehicleName = presaleOrder.vehicleInfo.getName();
        }
        if (presaleOrder.settleOrder != null) {
            serviceOrder.closeDate = presaleOrder.settleOrder.createdDate;
        }
        serviceOrder.saleCategory = presaleOrder.saleCategory;
        serviceOrder.title = presaleOrder.getTitle();
        serviceOrder.images = presaleOrder.images;
        serviceOrder.description = presaleOrder.description;
        return serviceOrder;
    }

    public static List<ServiceOrder> buildListFromPresaleOrders(List<PresaleOrder> presaleOrders, int state) {
        List<ServiceOrder> serviceOrders = new ArrayList<>();
        if (presaleOrders != null) {
            presaleOrders.forEach(presaleOrder -> {
                serviceOrders.add(ServiceOrder.buildFromPresaleOrder(presaleOrder, state));
            });
        }
        return serviceOrders;
    }

    public static List<ServiceOrder> buildListFromSettleOrders(List<SettleOrder> settleOrders, int state) {
        List<ServiceOrder> serviceOrders = new ArrayList<>();
        if (settleOrders != null) {
            settleOrders.forEach(settleOrder -> {
                serviceOrders.add(ServiceOrder.buildFromSettleOrder(settleOrder, state));
            });
        }
        return serviceOrders;
    }
}
