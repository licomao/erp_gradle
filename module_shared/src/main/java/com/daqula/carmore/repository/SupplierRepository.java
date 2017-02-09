package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.Supplier;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.SupplierRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SupplierRepository extends PagingAndSortingRepository<Supplier,Long>, SupplierRepositoryInterface,JpaSpecificationExecutor {

    List<Supplier> findByOrganization(Organization organization);

    List<Supplier> findByOrganizationAndName(Organization organization,String name);

    List<Supplier> findByOrganizationAndNameAndDeleted(Organization organization,String name,boolean deleted);

    List<Supplier> findByOrganizationAndDeleted(Organization organization, boolean deleted);
}
