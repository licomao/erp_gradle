package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.StockingOrder;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/12/1.
 */
public class StockingOrderSpecifications {
    public static Specification<StockingOrder> filterByOrderNumberView (String orderNumberView) {
        return (root, query, cb) -> orderNumberView != null ? cb.like(root.get("orderNumberView"), "%" + orderNumberView + "%") : null;
    }

    public static Specification<StockingOrder> filterByShop (Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }

    public static Specification<StockingOrder> filterByDeleted (boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted) ;
    }

    public static Specification<StockingOrder> filterByStockingDate(DateTime stockingDate) {
        return (root, query, cb) -> (stockingDate != null) ?
                cb.greaterThanOrEqualTo(root.get("stockingDate"), stockingDate) : null;
    }
    public static Specification<StockingOrder> filterByStockingDateEnd(DateTime stockingDate) {
        return (root, query, cb) -> (stockingDate != null) ?
                cb.lessThan(root.get("stockingDate"), stockingDate.plusDays(1)) : null;
    }
}
