package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 采购单repository
 * Created by swj on 2015/9/23.
 */
public interface PurchaseOrderRepository extends PagingAndSortingRepository<PurchaseOrder,Long>, JpaSpecificationExecutor {



        @Query("SELECT IFNULL(MAX(f.orderNumber),0) + 1 FROM PurchaseOrder f WHERE f.purchaseShop = ?1")
        Long findMaxOrderNum(Shop shop);

        /*@Query("SELECT COUNT(f.id) FROM PurchaseOrder f WHERE f.orderNumber = ?1")*/
        PurchaseOrder findByOrderNumberAndPurchaseShop(long count,Shop purchaseShop);

//        @Query("SELECT f  FROM PurchaseOrder f WHERE f.orderNumberView like ?1")
        PurchaseOrder findByOrderNumberView(String orderNumberView);
}
