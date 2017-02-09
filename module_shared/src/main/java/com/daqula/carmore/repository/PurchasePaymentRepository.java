package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.PurchasePayment;
import com.daqula.carmore.repository.impl.PurchasePaymentRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 采购付款单Repository
 * Created by mdc on 2016/1/21.
 */
public interface PurchasePaymentRepository extends PagingAndSortingRepository<PurchasePayment,Long>, JpaSpecificationExecutor,PurchasePaymentRepositoryInterface {

}
