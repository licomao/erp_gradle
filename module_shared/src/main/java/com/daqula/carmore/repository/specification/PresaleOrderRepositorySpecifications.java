package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.PresaleOrder;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/10/11.
 */
public class PresaleOrderRepositorySpecifications {

    public static Specification<PresaleOrder> filterByCustomerMobile (String mobile) {
        return (root, query, cb) -> mobile != null ? cb.equal(root.get("customer").get("mobile"), mobile) : null;
    }

    public static Specification<PresaleOrder> filterByCancelled(boolean cancelled) {
        return (root, query, cb) ->  cb.equal(root.get("cancelled"), cancelled);
    }
    
    public static Specification<PresaleOrder> filterByShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }

    public static Specification<PresaleOrder> filterBySettleOrder(SettleOrder settleOrder) {
        return (root, query, cb) -> settleOrder != null ? cb.equal(root.get("settleOrder"), settleOrder) : root.get("settleOrder").isNull();
    }
}
