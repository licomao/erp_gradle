package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.SettleOrderHistory;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;

import java.util.List;

/**
 * Created by swj on 2015/10/9.
 */
public interface SettleOrderHistoryRepositoryInterface {

    /**
     * 根据关键字查询 (realName mobile) 分页数据
     *
     * @param keyWord
     * @param page
     * @param rows
     * @return
     */
    public List<SettleOrderHistory> findListByOrganizationAndKeyWordAndShop(Organization organization, String keyWord, Shop shop, int page, int rows);

    /**
     * 根据关键字查询 (realName mobile) 分页条目
     *
     * @param keyWord
     * @return
     */
    public long findCountByOrganizationAndKeyWordAndShop(Organization organization, String keyWord, Shop shop);
}
