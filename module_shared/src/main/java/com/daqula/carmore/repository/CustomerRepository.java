package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>{
//    Customer findByMobile(String mobile);
    Customer findByToken(String token);

    List<Customer> findByMobile(String mobile);
//    Customer findByMobileAndOrganization(String mobile, Organization organization);
}
