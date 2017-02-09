package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.PurchaseOrder;
import com.daqula.carmore.model.order.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 采购单明细repository
 * Created by swj on 2015/9/23.
 */
public interface PurchaseOrderDetailRepository extends CrudRepository<PurchaseOrderDetail, Long>
        ,PagingAndSortingRepository<PurchaseOrderDetail,Long>,JpaSpecificationExecutor {

}
