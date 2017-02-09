package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.impl.ShopRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ShopRepository extends PagingAndSortingRepository<Shop, Long>, ShopRepositoryInterface, JpaSpecificationExecutor {

    List<Shop> findByOrganization(Organization organization);

   @Query("SELECT s FROM Shop s, ERPUser u WHERE s MEMBER OF u.shops AND u.id = ?")
    List<Shop> findByUserId(long id);

    Shop findByName(String name);
}
