package com.daqula.carmore.repository;

import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ERPRoleRepository extends PagingAndSortingRepository<ERPRole, Long>,JpaSpecificationExecutor {

    List<ERPRole> findByOrganization(Organization organization);

    ERPRole findByRole(String role);

    Page<ERPRole> findByRoleAndOrganizationAndDeleted(String role, Organization organization, Boolean deleted, Pageable pageable);
}
