package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.CareSuite;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.CareSuiteRepositoryInterface;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CustomerERPProfileRepository extends PagingAndSortingRepository<CustomerERPProfile, Long>,CrudRepository<CustomerERPProfile, Long> {

  @Query("SELECT p From CustomerERPProfile p where p.organization = ?1  AND p.customer = ?2 and p.deleted = false")
  CustomerERPProfile findByOrganizationAndCustomer(Organization organization,Customer customer);
}
