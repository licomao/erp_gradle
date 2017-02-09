package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.CustomSuite;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CustomSuiteRepository extends PagingAndSortingRepository<CustomSuite, Long>,JpaSpecificationExecutor<CustomSuite> {
    //List<CustomSuite> findSuiteByOrganizationAndSaleCategory(Organization organization, int saleCategory);
    List<CustomSuite> findByOrganizationAndEnabled(Organization organization,boolean enabled);

    Page<CustomSuite> findByOrganization(Organization organization,Pageable pageable);

    Page<CustomSuite> findByOrganizationAndNameLike(Organization organization, String name, Pageable pageable);

    @Query("SELECT count(o.id) FROM CustomSuite o WHERE o.organization = :organization AND o.enabled = :enabled")
    long findTotalByOrganizationAndEnabled(@Param("organization") Organization organization,@Param("enabled") boolean enabled);


}
