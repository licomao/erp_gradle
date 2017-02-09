package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CustomerPurchasedSuiteItemRepository extends
        PagingAndSortingRepository<CustomerPurchasedSuiteItem, Long>, JpaSpecificationExecutor {

        @Query("SELECT p FROM CustomerPurchasedSuiteItem p where p.purchasedSuite = ?1 AND p.deleted = false ")
        List<CustomerPurchasedSuiteItem> findByPurchasedSuite(CustomerPurchasedSuite purchasedSuite);

}
