package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.CareSuite;
import com.daqula.carmore.repository.impl.CareSuiteRepositoryInterface;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CareSuiteRepository extends PagingAndSortingRepository<CareSuite, Long>, CareSuiteRepositoryInterface{
}
