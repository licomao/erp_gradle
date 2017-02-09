package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.Job;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by chenxin on 2015/10/9 .
 */
public interface JobRepository extends CrudRepository<Job, Long> , JpaSpecificationExecutor {
}
