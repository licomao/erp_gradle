package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.HistoryCareSuiteItem;
import com.daqula.carmore.repository.impl.HistoryCareSuiteItemRepositoryInterface;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by mdc on 2016/2/4.
 */
public interface HistoryCareSuiteItemRepository extends PagingAndSortingRepository<HistoryCareSuiteItem, Long>, HistoryCareSuiteItemRepositoryInterface {
}
