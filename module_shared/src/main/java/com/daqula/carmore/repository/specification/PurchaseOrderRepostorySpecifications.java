package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

/**
 * Created by swj on 2015/9/23.
 */
public class PurchaseOrderRepostorySpecifications {
    public static Specification<PurchaseOrder> filterHasOrderNumber(String orderNumber) {
        return (root, query, cb) -> orderNumber != null ? cb.like(root.get("orderNumberView"), "%" + orderNumber + "%") : null;
    }

    public static Specification<PurchaseOrder> filterHasOrderStatus(int orderStatus) {
        return (root, query, cb) -> orderStatus >= 0 ? cb.equal(root.get("orderStatus"), orderStatus) : null;
    }

    public static Specification<PurchaseOrder> filterHasPurchaseDateStart(Date purchaseDateStart) {
        return (root, query, cb) -> purchaseDateStart != null ? cb.greaterThan(root.get("createdDate"), purchaseDateStart) : null;
    }

    public static Specification<PurchaseOrder> filterHasPurchaseDateEnd(Date purchaseDateEnd) {
        return (root, query, cb) -> purchaseDateEnd != null ? cb.lessThan(root.get("createdDate"), purchaseDateEnd) : null;
    }

    public static Specification<PurchaseOrder> filterPurchaseType(int purchaseType){
        return (root, query, cb) -> purchaseType != 99 ? cb.equal(root.get("purchaseType"), purchaseType) : null;
    }

    public static Specification<PurchaseOrder> filterDeleteStatus(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

    public static Specification<PurchaseOrder> filterByShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("purchaseShop"), shop) : null;
    }
    public static Specification<PurchaseOrder> filterBySaleShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("saleShop"), shop) : null;
    }
}
