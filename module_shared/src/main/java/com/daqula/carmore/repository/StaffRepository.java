package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by chenxin on 2015/10/9 .
 */
public interface StaffRepository extends CrudRepository<Staff, Long>, JpaSpecificationExecutor {
    List<Staff> findByShopAndDeleted(Shop shop, boolean b);

    List<Staff> findByShopAndDeletedAndFingerPrintNotNull(Shop shop, boolean b);

    Staff findByIdentityCard(String idcard);

    Staff findByFingerPrint(String id);

    Staff findByIdAndStatusNot(Long id, String status);
}
