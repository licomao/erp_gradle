package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.RefundOrder;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

/**
 * Created by swj on 2015/9/27.
 */
public class RefundOrderRepostorySpecifications {
    public static Specification<RefundOrder> filterHasOrderNumber(String orderNumberView) {
        return (root, query, cb) -> orderNumberView != null ? cb.like(root.get("orderNumberView"), "%" + orderNumberView + "%") : null;
    }

    public static Specification<RefundOrder> filterHasOrderStatus(int orderStatus) {
        return (root, query, cb) -> orderStatus >= 0 ? cb.equal(root.get("orderStatus"), orderStatus) : null;
    }

    public static Specification<RefundOrder> filterHasRefundDateStart(Date refundDateStart) {
        return (root, query, cb) -> refundDateStart != null ? cb.greaterThan(root.get("createdDate"), refundDateStart) : null;
    }

    public static Specification<RefundOrder> filterHasRefundDateEnd(Date refundDateEnd) {
        return (root, query, cb) -> refundDateEnd != null ? cb.lessThan(root.get("createdDate"), refundDateEnd) : null;
    }

    public static Specification<RefundOrder> filterDeleteStatus() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

    public static Specification<RefundOrder> filterByShop(Shop shop) {

        return ((root, query, cb) -> shop != null ? cb.equal(root.get("refundShop"),shop):null);
    }
}
