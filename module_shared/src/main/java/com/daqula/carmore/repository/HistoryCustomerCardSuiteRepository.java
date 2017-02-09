package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.HistoryCustomerCardSuite;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by mdc on 2016/2/4.
 */
public interface HistoryCustomerCardSuiteRepository extends CrudRepository<HistoryCustomerCardSuite, Long>, PagingAndSortingRepository<HistoryCustomerCardSuite, Long>,JpaSpecificationExecutor {

    @Query("SELECT f from HistoryCustomerCardSuite f where f.deleted = true")
    List<HistoryCustomerCardSuite> findHistoryInfo();

}
