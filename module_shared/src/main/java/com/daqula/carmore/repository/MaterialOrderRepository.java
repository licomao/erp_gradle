package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.MaterialOrder;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by mdc on 2015/9/16.
 */
public interface MaterialOrderRepository extends CrudRepository<MaterialOrder, Long>, PagingAndSortingRepository<MaterialOrder, Long>,JpaSpecificationExecutor {

    @Query("SELECT IFNULL(MAX(f.orderNum),0) + 1 FROM MaterialOrder f WHERE f.shop = ?1")
    Long findMaxOrderNum(Shop shop);

    @Query("SELECT p FROM MaterialOrder p WHERE p.shop = ?1 AND p.createdDate > ?2 AND p.createdDate < ?3 AND p.deleted = false")
    List<MaterialOrder> getMaterials(Shop shop,DateTime start, DateTime end);
}
