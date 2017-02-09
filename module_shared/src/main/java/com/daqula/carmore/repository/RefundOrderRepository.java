package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.RefundOrder;
import com.daqula.carmore.model.shop.Shop;
import com.pingplusplus.model.Refund;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by swj on 2015/9/27.
 */
public interface RefundOrderRepository extends CrudRepository<RefundOrder, Long>
        , PagingAndSortingRepository<RefundOrder, Long>, JpaSpecificationExecutor {


        @Query("SELECT IFNULL(MAX(f.orderNumber),0) + 1 FROM RefundOrder f WHERE f.refundShop = ?1")
        Long findMaxOrderNum(Shop shop);

        RefundOrder findByOrderNumberAndRefundShop(long orderNumber,Shop refundShop);

}
