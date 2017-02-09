package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.Expense;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by chenxin on 2015/10/9.
 */
public class ExpenseSpecifications {
    public static Specification<Expense> filterByOrganization (Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("shop").get("organization"), organization) : null;
    }

    public static Specification<Expense> filterByShop (Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }

    public static Specification<Expense> filterByYear (int year) {
        return (root, query, cb) -> year != 0 ? cb.equal(root.get("year"), year) : null;
    }

    public static Specification<Expense> filterByMonth (int month) {
        return (root, query, cb) -> month != 0 ? cb.equal(root.get("month"), month) : null;
    }

    public static Specification<Expense> filterByDeleted (Boolean deleted) {
        return (root, query, cb) -> cb.equal(root.get("deleted"), deleted);
    }

}
