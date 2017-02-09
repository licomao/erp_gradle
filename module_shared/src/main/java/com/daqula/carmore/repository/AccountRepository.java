package com.daqula.carmore.repository;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface AccountRepository extends PagingAndSortingRepository<ERPUser, Long> {

    Page<ERPUser> findByUsernameLikeAndOrganizationAndShopsAndDeleted(String username, Organization organization, List<Shop> shops,boolean deleted, Pageable pageable);

    Page<ERPUser> findByUsernameLikeAndOrganizationAndDeleted(String username, Organization organization,boolean deleted, Pageable pageable);
}
