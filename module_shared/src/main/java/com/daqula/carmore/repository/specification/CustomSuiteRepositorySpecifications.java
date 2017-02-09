package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.order.SettleOrderHistory;
import com.daqula.carmore.model.shop.CustomSuite;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;

/**
 * Created by swj on 2015/10/13.
 */
public class CustomSuiteRepositorySpecifications {
    public static Specification<CustomSuite> filterOrganization(Organization organization) {
        return (root, query, cb) -> organization != null ? cb.equal(root.get("organization"), organization) : null;
    }

    public static Specification<CustomSuite> filterHasName(String name) {
        return (root, query, cb) -> name != null ? cb.like(root.get("name"), name) : null;
    }


   /* public static Specification<PurchaseOrder> filterHasPurchaseDateEnd(Date purchaseDateEnd) {
        return (root, query, cb) -> purchaseDateEnd != null ? cb.lessThan(root.get("createdDate"), purchaseDateEnd) : null;
    }*/

    public static Specification<CustomSuite> filterDeleteStatus() {
        return (root, query, cb) -> cb.notEqual(root.get("deleted"), true);
    }

}
