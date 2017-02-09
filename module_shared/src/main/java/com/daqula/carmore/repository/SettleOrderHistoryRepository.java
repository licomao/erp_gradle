package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.SettleOrderHistory;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.impl.SettleOrderHistoryRepositoryInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * Created by swj on 2015/10/13.
 */
public interface SettleOrderHistoryRepository extends PagingAndSortingRepository<SettleOrderHistory, Long>,
        JpaSpecificationExecutor<SettleOrderHistory>, CrudRepository<SettleOrderHistory, Long>, SettleOrderHistoryRepositoryInterface {

    @Modifying
    @Query("UPDATE SettleOrderHistory his SET his.isSignFor = ? WHERE his.id = ?")
    int setIsSignForById(Boolean isSignFor, long id);



    @Query(value = "SELECT s FROM SettleOrderHistory s WHERE s.belongShop.organization = :organization " +
            "AND s.shop = :shop " +
            "AND (s.settleOrder.saleNoView LIKE %:keyWord%)")
    Page<SettleOrderHistory> findByOrganizationAndKeyWordAndShop(@Param("organization") Organization organization,
                                                                 @Param("keyWord") String keyWord,
                                                                 @Param("shop") Shop shop, Pageable Pageable);
}
