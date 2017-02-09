package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.PurchaseOrderDetail;
import com.daqula.carmore.model.order.RefundOrderDetail;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 退货明细repository
 * Created by swj on 2015/9/28.
 */
public interface RefundOrderDetailRepository extends CrudRepository<RefundOrderDetail, Long>
        ,PagingAndSortingRepository<RefundOrderDetail,Long>,JpaSpecificationExecutor {


}
