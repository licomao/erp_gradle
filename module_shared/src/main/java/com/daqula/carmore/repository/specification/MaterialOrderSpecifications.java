package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.MaterialOrder;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/9/19.
 */
public class MaterialOrderSpecifications {

    public static Specification<MaterialOrder> filterByShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }

    public static Specification<MaterialOrder> filterByOrderNumber(String orderNumber) {
        return (root, query, cb) -> orderNumber != null ? cb.equal(root.get("orderNumber"), orderNumber) : null;
    }

    public static Specification<MaterialOrder> filterByOrderNumView (String orderNumView) {
        return (root, query, cb) -> orderNumView != null ? cb.equal(root.get("orderNumView"), orderNumView) : null;
    }

    public static Specification<MaterialOrder> filterByCreateDate(String useDate) {
        return (root, query, cb) -> useDate != null ? cb.equal(root.get("useDate"), useDate) : null;
    }

    public static Specification<MaterialOrder> filterByCreatedDateGreater(DateTime startDate) {
        return (root, query, cb) -> startDate != null ? cb.greaterThan(root.get("createdDate"), startDate) : null;
    }
    public static Specification<MaterialOrder> filterByCreatedDateLess(DateTime endDate) {
        return (root, query, cb) -> endDate != null ? cb.lessThan(root.get("createdDate"), endDate) : null;
    }

    public static Specification<MaterialOrder> filterByDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }
}
