package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.PurchasePayment;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2016/1/29.
 */
public class PurchasePaymentSpecifications {

    public static Specification<PurchasePayment> filterByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<PurchasePayment> filterByDeleted(Boolean deleted) {
        return (root, query, cb) -> deleted != null ? cb.equal(root.get("deleted"), deleted) : null;
    }

    public static Specification<PurchasePayment> filterByPayType(int payType) {
        return (root, query, cb) -> payType > 0 ? cb.equal(root.get("payType"), payType) : null;
    }

    public static Specification<PurchasePayment> filterBySupplier(Supplier supplier) {
        return (root, query, cb) -> supplier != null ? cb.equal(root.get("supplier"), supplier) : null;
    }

    public static Specification<PurchasePayment> filterByPurchaseOrder(String orderNumberView) {
        return (root, query, cb) -> orderNumberView != null ? cb.like(root.get("purchaseOrder").get("orderNumberView"), orderNumberView) : null;
    }

    public static Specification<PurchasePayment> filterByPurchaseOrderShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("purchaseOrder").get("purchaseShop"), shop) : null;
    }
    public static Specification<PurchasePayment> filterByPurchaseOrderPurchaseType(int purchaseType) {
        return (root, query, cb) -> purchaseType != 99 ? cb.equal(root.get("purchaseOrder").get("purchaseType"), purchaseType) : null;
    }
}
