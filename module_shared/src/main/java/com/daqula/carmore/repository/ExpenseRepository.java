package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.Expense;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseRepository extends CrudRepository<Expense, Long>, JpaSpecificationExecutor {
    Expense findByYearAndMonthAndShopAndDeleted(int year, int month, Shop shop, boolean deleted);
//    Expense findByYearAndMonthAndShop(int year, int month, Shop shop);
}
