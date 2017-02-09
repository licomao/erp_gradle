package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.StockItem;

import java.util.List;

public interface SaleShelfRepositoryInterface {

    List<StockItem> findAccessoriesByCategoryAndBrand(long vehicleModelId, Integer accessoryCategory,
                                                  String brandName, String param1, String param2,
                                                  String param3, String param4, String param5);
}
