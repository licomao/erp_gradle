package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.SettleAccounts;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.impl.SettleAccountsRepositoryInterface;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by mdc on 2015/8/25.
 */
public interface SettleAccountsRepository extends PagingAndSortingRepository<SettleAccounts ,Long> ,SettleAccountsRepositoryInterface,CrudRepository<SettleAccounts,Long>,JpaSpecificationExecutor {
}
