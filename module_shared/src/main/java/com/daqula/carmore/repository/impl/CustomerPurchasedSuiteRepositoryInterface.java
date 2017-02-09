package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by swj on 2015/10/9.
 */
public interface CustomerPurchasedSuiteRepositoryInterface {

    /**
     * 根据关键字查询 (realName mobile) 分页数据
     *
     * @param keyWord
     * @param page
     * @param rows
     * @return
     */
    public List<Object[]> findListByKeyWordAndShop(String keyWord, Shop shop, Long orgId, int page, int rows, String sord, String sidx,DateTime calDateStart,DateTime calDateEnd);

    /**
     * 根据关键字查询 (realName mobile) 分页条目
     *
     * @param keyWord
     * @return
     */
    public long findCountByKeyWordAndShop(String keyWord, Shop shop, Long orgId,DateTime calDateStart,DateTime calDateEnd);

    /**
     * 导出
     * @param keyWord
     * @param shop
     * @param id
     * @param filterTimeStart
     * @param filterTimeEnd
     * @return
     */
    List<Object[]> findListByKeyWordAndShop(String keyWord, Shop shop, Long id, DateTime filterTimeStart, DateTime filterTimeEnd);

}
