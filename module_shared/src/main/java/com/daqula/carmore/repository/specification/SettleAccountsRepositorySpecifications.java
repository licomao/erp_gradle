package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.SettleAccounts;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

/**
 * Created by mdc on 2015/10/14.
 */
public class SettleAccountsRepositorySpecifications {

    public static Specification<SettleAccounts> filterByCalDate(Date calDate) {
        return (root, query, cb) -> calDate != null ? cb.equal(root.get("calDate"), calDate) : null;
    }
    public static Specification<SettleAccounts> filterByShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }
}
