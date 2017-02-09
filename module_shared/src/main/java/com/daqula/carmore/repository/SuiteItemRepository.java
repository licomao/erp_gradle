package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.SuiteItem;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.CustomSuite;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by swj on 2015/10/9.
 */
public interface SuiteItemRepository extends CrudRepository<SuiteItem, Long>,PagingAndSortingRepository<SuiteItem, Long>, JpaSpecificationExecutor<SkuItem> {
    List<SuiteItem> findBySkuItemAndCostAndDeleted(CustomStockItem customStockItem, Double aDouble, boolean deleted);

    /*@Query("SELECT o FROM SuiteItem o, CustomSuite a WHERE o.=a.id")
    List<SuiteItem> findBySuiteAndDeleted(CustomSuite customSuite, boolean b);*/


//    Page<SuiteItem> findBy
}
