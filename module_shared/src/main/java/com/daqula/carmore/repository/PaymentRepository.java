package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.repository.impl.PaymentRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> ,CrudRepository<Payment,Long>, JpaSpecificationExecutor, PaymentRepositoryInterface {
}
