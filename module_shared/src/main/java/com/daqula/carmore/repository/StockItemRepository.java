package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.StockItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface StockItemRepository extends PagingAndSortingRepository<StockItem, Long>, JpaSpecificationExecutor<StockItem> {

    List<StockItem> findByName(String name);

    @Query("select s from StockItem s where s.name like CONCAT(?1, '%') and s.rootCategory = ?2 and s.deleted = false")
    Page<StockItem> findByNameLikeAndRootCategory(String name, Integer rootCategory, Pageable pageable);

    List<StockItem> findByAppSort(int appSort);

    @Query("select distinct s.brandName from StockItem s where s.accessoryCategory=?1")
    List<String> getBrandNameByAccessoryCategory(Integer accessoryCategory);
}
