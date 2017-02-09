package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SecondaryCategoryRepository extends PagingAndSortingRepository<SecondaryCategory, Long>, JpaSpecificationExecutor {

    List<SecondaryCategory> findByRootCategoryAndDeletedAndOrganization(Integer rootCategory, boolean deleted,Organization organization);

    Page<SecondaryCategory> findByRootCategoryAndDeleted(Integer rootCategory, boolean deleted, Pageable pageable);

    List<SecondaryCategory> findByOrganization(Organization organization);

    List<SecondaryCategory> findByOrganizationAndNameAndAdditionRate(Organization organization,String name,float additionRate);
}
