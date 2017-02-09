package com.daqula.carmore.repository;

import com.daqula.carmore.model.TempPresaleOrder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TempPresaleOrderRepository extends
        PagingAndSortingRepository<TempPresaleOrder, Long>, JpaSpecificationExecutor {}
