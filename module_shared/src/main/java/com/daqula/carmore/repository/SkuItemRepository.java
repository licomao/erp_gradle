package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.SkuItem;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SkuItemRepository extends PagingAndSortingRepository<SkuItem, Long>, JpaSpecificationExecutor<SkuItem> {

}
