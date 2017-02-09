package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.customer.HistoryCareSuiteItem;

import java.util.List;

/**
 * Created by mdc on 2016/2/4.
 */
public interface HistoryCareSuiteItemRepositoryInterface {
    List<HistoryCareSuiteItem> getItemsInfo(String cardNo);
}
