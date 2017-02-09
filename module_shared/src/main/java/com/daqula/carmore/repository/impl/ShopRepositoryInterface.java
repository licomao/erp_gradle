package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.Shop;

import java.util.List;

public interface ShopRepositoryInterface {

    List<Shop> findShopNearBy(double lat, double lng, int page, int pageSize, int radiusInkilometer);  //查找附近门店

    List<Shop> findShopByNameAndOrgLike(String orgName, String shopName ,int page, int row, String sord); //根据门店名 组织名模糊查询

    int isCodeCanUse(String code ,long id, long orgId); //判断shopCode 是否可用
}
