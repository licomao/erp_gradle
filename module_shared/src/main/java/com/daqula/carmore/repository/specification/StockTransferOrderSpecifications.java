package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.StockTransferOrder;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/10/8.
 */
public class StockTransferOrderSpecifications {

    public static Specification<StockTransferOrder> filterByOrderNumberView(String orderNumberView) {
        return (root, query, cb) -> (orderNumberView != null) ?
                cb.like(root.get("orderNumberView"), orderNumberView) : null;
    }

    public static Specification<StockTransferOrder> filterByDeleted(boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

    public static Specification<StockTransferOrder> filterByTransferDateStart(DateTime transferDate) {
        return (root, query, cb) -> (transferDate != null) ?
                cb.greaterThanOrEqualTo(root.get("transferDate"), transferDate) : null;
    }
    public static Specification<StockTransferOrder> filterByTransferDateEnd(DateTime transferDate) {
        return (root, query, cb) -> (transferDate != null) ?
                cb.lessThanOrEqualTo(root.get("transferDate"), transferDate.plusDays(1)) : null;
    }

    public static Specification<StockTransferOrder> filterByShop(Shop shop) {
        return (root, query, cb) -> (shop != null) ?
                cb.or(root.get("inShop").in(shop), root.get("outShop").in(shop)) : null;
    }
}

