package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.OperationItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by shd
 */
public interface OperationItemRepository extends PagingAndSortingRepository<OperationItem, Long> {

    Page<OperationItem> findByNameLikeAndCarLevel(String name,String CarLevel, Pageable pageable);

    Page<OperationItem> findByNameLikeAndCarLevelAndOperationType(String name,String CarLevel,int operationType, Pageable pageable);
}
