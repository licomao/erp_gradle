package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class CustomerPurchasedSuiteSpecifications {

    public static Specification<CustomerPurchasedSuite> filterHasTimesLeft() {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<CustomerPurchasedSuite, CustomerPurchasedSuiteItem> join
                    = root.join("purchasedSuiteItems");
            return cb.greaterThan(join.get("timesLeft"), 0);
        };
    }

    public static Specification<CustomerPurchasedSuite> filterByCustomer(Customer customer) {
        return (root, query, cb) -> customer != null ? cb.equal(root.get("customer"), customer) : null;
    }

    public static Specification<CustomerPurchasedSuite> filterByNotOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.notEqual(root.get("shop").get("organization"), organization) : null;
    }

    public static Specification<CustomerPurchasedSuite> filterByOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("shop").get("organization"), organization) : null;
    }

    public static Specification<CustomerPurchasedSuite> filterByRealName(String realName){
        return (root, query, cb) -> realName != null ? cb.like(root.get("customerERPProfile").get("realName"), "%" + realName + "%") : null;
    }
    public static Specification<CustomerPurchasedSuite> filterByMobile(String mobile){
        return (root, query, cb) -> mobile != null ? cb.like(root.get("customer").get("mobile"), "%" + mobile + "%") : null;
    }
}
