package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import com.daqula.carmore.model.order.PresaleOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

/**
 * Created by Administrator on 2015/8/17.
 */
public interface PresaleOrderRepository extends PagingAndSortingRepository<PresaleOrder, Long>, JpaSpecificationExecutor {

    PresaleOrder findByUid(UUID uid);

    Page<PresaleOrder> findByCancelled(Boolean deleted, Pageable pageable);

    @Query("SELECT p FROM PresaleOrder p, OrderDetail o " +
           " WHERE o.fromPurchasedSuiteItem = ?1 " +
            "AND o MEMBER OF p.orderDetails " +
            "AND p.settleOrder IS NULL")
    PresaleOrder findByAppointedPresaleOrder(CustomerPurchasedSuiteItem purchasedItem);
}
