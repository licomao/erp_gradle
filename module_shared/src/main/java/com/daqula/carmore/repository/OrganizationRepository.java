package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.Agency;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.OrganizationRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long>, OrganizationRepositoryInterface, JpaSpecificationExecutor{
    Organization findByName(String name);

    Organization findBySerialNum(String serialNum);

    List<Organization> findByAgency(Agency agency);
}
