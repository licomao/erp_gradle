package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.CustomStockItemRepositoryInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by mdc on 2015/9/17.
 */
public interface CustomStockItemRepository extends PagingAndSortingRepository<CustomStockItem, Long>,JpaSpecificationExecutor, CustomStockItemRepositoryInterface {

    List<CustomStockItem> findByName(String name);

    List<CustomStockItem> findByOrganization(Organization organization);

    List<CustomStockItem> findByNameAndOrganization(String name,Organization organization);

    @Query("select s from CustomStockItem s  where s.name like CONCAT(?1, '%') and s.rootCategory = ?2 and s.deleted = false")
    Page<CustomStockItem> findByNameLikeAndRootCategory(String name, Integer rootCategory, Pageable pageable);

    @Query("select s from CustomStockItem s  where s.name like CONCAT(?1, '%') and s.deleted = false")
    Page<CustomStockItem> findByNameLike(String name, Pageable pageable);

    List<CustomStockItem> findByAppSort(int appSort);

    @Query("select distinct s.brandName from CustomStockItem s where s.accessoryCategory=?1")
    List<String> getBrandNameByAccessoryCategory(Integer accessoryCategory);

    Page<CustomStockItem> findByOrganizationAndNameLikeAndRootCategory(Organization organization, String itemName, int rootCategory, Pageable pageable);
}
