package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.*;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.CustomerProfileRepositoryInterface;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CustomerProfileRepository extends PagingAndSortingRepository<CustomerProfile, Long>,
        CustomerProfileRepositoryInterface {

    @Query("SELECT p FROM CustomerAppProfile p WHERE p.customer = ?1")
    CustomerAppProfile findAppProfileByCustomer(Customer customer);

    @Query("SELECT p FROM CustomerERPProfile p WHERE p.customer = ?1 AND p.organization = ?2 AND p.deleted = false")
    CustomerERPProfile findERPProfileByCustomer(Customer customer, Organization org);

    @Query("SELECT p FROM CustomerERPProfile p WHERE ?1 MEMBER OF p.vehicles")
    CustomerERPProfile findByVehicles(VehicleInfo vehicleInfo);

    @Query("SELECT p FROM CustomerERPProfile p WHERE p.organization = ?1")
    List<CustomerERPProfile> findERPProfileByOrganization(Organization org);

    @Query("SELECT p FROM CustomerERPProfile p WHERE p.customer = ?1 AND p.organization = ?2")
    CustomerAppProfile findAppProfileByCustomer(Customer customer, Organization org);


    @Query("SELECT p FROM CustomerERPProfile p WHERE ?1 MEMBER OF p.vehicles AND p.organization.id = ?2")
    CustomerERPProfile findERPProfileByPlateNumberAndOrganizationId(VehicleInfo vehicleInfo, Long orgId);

    @Query("SELECT p FROM CustomerAppProfile p WHERE ?1 MEMBER OF p.vehicles AND p.bindingShop.organization.id = ?2")
    CustomerAppProfile findAppProfileByPlateNumberAndOrganizationId(VehicleInfo vehicleInfo, Long orgId);
}
