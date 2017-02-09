package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.StockingOrder;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.impl.StockingOrderRepositoryInterface;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by mdc on 2015/9/24.
 */
public interface StockingOrderRepository extends CrudRepository<StockingOrder, Long>, PagingAndSortingRepository<StockingOrder, Long>,JpaSpecificationExecutor, StockingOrderRepositoryInterface {

    @Query("SELECT IFNULL(MAX(f.orderNumber), 0) + 1 FROM StockingOrder f WHERE f.shop = ?1")
    Long findMaxOrderNum(Shop shop);

    @Query("SELECT p from StockingOrder p WHERE (p.stockingDate >= ?1 and p.stockingDate < ?2) and p.shop = ?3 and p.deleted = false  order by p.stockingDate desc ")
    List<StockingOrder> findOrderCal(DateTime startTime, DateTime endTime,Shop shop);



}
