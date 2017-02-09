package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 基础数据设置repository
 * Created by Administrator on 2015/9/21.
 */
public interface BaseSetRepository extends CrudRepository<BaseSet, Long>, PagingAndSortingRepository<BaseSet, Long>,JpaSpecificationExecutor {

    BaseSet findByOrganization(Organization organization);

    BaseSet findByShop(Shop shop);
}
