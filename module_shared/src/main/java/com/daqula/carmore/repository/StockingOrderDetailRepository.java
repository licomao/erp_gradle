package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.StockingOrderDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by mdc on 2015/9/29.
 */
public interface StockingOrderDetailRepository extends CrudRepository<StockingOrderDetail, Long>, PagingAndSortingRepository<StockingOrderDetail, Long>,JpaSpecificationExecutor {
}
