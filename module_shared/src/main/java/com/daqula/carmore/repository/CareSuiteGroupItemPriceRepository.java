package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.CareSuiteGroupItemPrice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CareSuiteGroupItemPriceRepository extends CrudRepository<CareSuiteGroupItemPrice, Long> {
    @Query("select sum(p.overriddenPrice) from CareSuiteGroupItemPrice p where p.careSuiteGroupItem.id in ?1 ")
    double calculatePrice(List<Long> careSuiteGroupItemIds);
}
