package com.daqula.carmore.repository;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Agency;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AgencyRepository extends PagingAndSortingRepository<Agency, Long>,JpaSpecificationExecutor<Agency> {


    @Query("SELECT o FROM Agency o WHERE o.erpUser.username LIKE ? AND o.organizations = ?")
    Page<Agency> findByNameLikeAndOrganization(String username, List<Organization> organizations, Pageable pageable);


    Page<Agency> findByOrganizations(List<Organization> organizations, Pageable pageable);

    Agency findByErpUser(ERPUser user);

    @Query("SELECT o FROM Agency o WHERE o.erpUser.username LIKE ?")
    Page<Agency> findByNameLike(String name, Pageable pageable);
}
