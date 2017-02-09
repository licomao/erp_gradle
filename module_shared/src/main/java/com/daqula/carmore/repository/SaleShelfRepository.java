package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.SaleShelf;
import com.daqula.carmore.repository.impl.SaleShelfRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SaleShelfRepository extends PagingAndSortingRepository<SaleShelf, Long>,
        JpaSpecificationExecutor, SaleShelfRepositoryInterface {

    @Query("select s.suite, s.price from SaleShelf s where s.organization=?1 and s.saleCategory=?2")
    List<Object[]> findSuiteByOrganizationAndSaleCategory(Organization organization, int saleCategory);

    @Query("select s.skuItem, s.price from SaleShelf s where s.organization=?1 and s.saleCategory=?2")
    List<Object[]> findSkuByOrganizationAndSaleCategory(Organization organization, int saleCategory);

    SaleShelf findBySuiteAndOrganization(Suite suite, Organization organization);

    SaleShelf findBySkuItemAndOrganization(SkuItem skuItem, Organization organization);

    @Query("select distinct s.skuItem.brandName from SaleShelf s where s.skuItem.accessoryCategory=?1")
    List<String> getBrandNameByAccessoryCategory(Integer accessoryCategory);
}
