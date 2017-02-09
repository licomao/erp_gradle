package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import com.daqula.carmore.model.order.OrderDetail;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.Date;

public class SettleOrderSpecifications {

    public static Specification<SettleOrder> filteredBySaleCategory(Integer saleCategory) {
        return (root, query, cb) -> saleCategory != null ? cb.equal(root.get("saleCategory"), saleCategory) : null;
    }

    public static Specification<SettleOrder> filteredByCommented(Boolean commented) {
        return (root, query, cb) -> commented != null ? cb.equal(root.get("commented"), commented) : null;
    }

    public static Specification<SettleOrder> createDaysLessThan(Integer days) {
        return (root, query, cb) -> days != null ? cb.greaterThanOrEqualTo(root.<DateTime>get("createdDate"), DateTime.now().minusDays(days)) : null;
    }

    public static Specification<SettleOrder> filteredByCustomer(Customer customer) {
        return (root, query, cb) -> customer != null ? cb.equal(root.get("customer"), customer) : null;
    }

    public static Specification<SettleOrder> createDayGreater(Date date) {
        return (root, query, cb) -> date != null ? cb.greaterThanOrEqualTo(root.get("finishDate"), date) : null;
    }
    public static Specification<SettleOrder> createDayLess(Date date) {
        return (root, query, cb) -> date != null ? cb.lessThanOrEqualTo(root.get("finishDate"), date) : null;
    }

    public static Specification<SettleOrder> filterByShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.<DateTime>get("shop"), shop) : null;
    }

    public static Specification<SettleOrder> isNotPurchaseSuiteOrder() {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<SettleOrder, OrderDetail> join = root.join("orderDetails");
            return cb.isNotNull(join.get("orderedItem"));
        };
    }

    public static Specification<SettleOrder> filteredByDeleted(Boolean deleted) {
        return (root, query, cb) -> deleted != null ? cb.equal(root.get("deleted"), deleted) : null;
    }
    public static Specification<SettleOrder> filteredByIsFinish(Boolean isFinish) {
        return (root, query, cb) -> isFinish != null ? cb.equal(root.get("isFinish"), isFinish) : null;
    }


    public static Specification<SettleOrder> filteredBySaleNoView(String saleNoView) {
        return (root, query, cb) -> saleNoView != null ? cb.equal(root.get("saleNoView"), saleNoView) : null;
    }

    public static Specification<SettleOrder> filteredBySaleNoMobile(String mobile) {
        return (root, query, cb) -> mobile != null ? cb.equal(root.get("customer").get("mobile"), mobile) : null;
    }

    public static Specification<SettleOrder> filteredByVehicleInfoId (Long id) {
        return (root, query, cb) -> id != null ? cb.equal(root.get("vehicleInfo").get("id"), id) : null;
    }

}
