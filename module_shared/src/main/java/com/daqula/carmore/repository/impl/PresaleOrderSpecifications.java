package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.order.PresaleOrder;
import org.springframework.data.jpa.domain.Specification;

public class PresaleOrderSpecifications {

    public static Specification<PresaleOrder> filteredBySaleCategory(Integer saleCategory) {
        return (root, query, cb) -> saleCategory != null ? cb.equal(root.get("saleCategory"), saleCategory) : null;
    }

    public static Specification<PresaleOrder> settleOrderIsNull() {
        return (root, query, cb) -> cb.isNull(root.get("settleOrder"));
    }

    public static Specification<PresaleOrder> filteredByCustomer(Customer customer) {
        return (root, query, cb) -> customer !=null ? cb.equal(root.get("customer"), customer) : null;
    }
}
