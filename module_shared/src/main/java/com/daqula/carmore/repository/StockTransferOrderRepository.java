package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.StockTransferOrder;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by Administrator on 2015/10/7.
 */
public interface StockTransferOrderRepository extends CrudRepository<StockTransferOrder, Long>, PagingAndSortingRepository<StockTransferOrder, Long>,JpaSpecificationExecutor {

    @Query("SELECT IFNULL(MAX(f.orderNumber),0) + 1 FROM StockTransferOrder f WHERE f.inShop = ?1")
    Long findMaxOrderNum(Shop shop);
}
