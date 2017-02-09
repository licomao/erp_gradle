package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.CareSuiteGroupItem;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CareSuiteGroupItemRepository extends PagingAndSortingRepository<CareSuiteGroupItem, Long>{

    List<CareSuiteGroupItem> findByIdIn(List<Long> ids);

}
