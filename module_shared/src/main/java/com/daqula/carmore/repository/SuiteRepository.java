package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.Suite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SuiteRepository extends PagingAndSortingRepository<Suite, Long>	{

    Page<Suite> findByNameLike(String name ,Pageable pageable);

    List<Suite> findByNameLike(String name);

}
