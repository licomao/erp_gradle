package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.order.SettleAccounts;
import java.util.List;

/**
 * Created by Administrator on 2015/8/25.
 */
public interface SettleAccountsRepositoryInterface {
     List<SettleAccounts> queryTodaySettleRecord(Long shopId);
     double qureryTotalAmountsByShopId(Long shopId);
}
