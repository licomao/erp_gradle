package com.daqula.carmore.repository;

import com.daqula.carmore.model.TempSettleOrder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TempSettleOrderRepository extends
        PagingAndSortingRepository<TempSettleOrder, Long>, JpaSpecificationExecutor<TempSettleOrder> {}
