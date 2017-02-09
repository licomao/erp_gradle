package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.FixedAsset;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * 固定资产管理repository
 * Created by mdc on 2015/9/15.
 */
public interface FixedAssetRepository extends CrudRepository<FixedAsset, Long>, PagingAndSortingRepository<FixedAsset, Long>,JpaSpecificationExecutor {

    List<FixedAsset> findByShopAndAssetStatus(Shop shop, int assetStatus);
}
