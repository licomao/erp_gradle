package com.daqula.carmore.repository;

import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import com.daqula.carmore.model.shop.StaffAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

import java.util.List;
/**
 * Created by chenxin on 2015/10/13 0013.
 */
public interface StaffAttendanceRepository  extends CrudRepository<StaffAttendance, Long>, JpaSpecificationExecutor {

    @Query("select distinct s.staff from StaffAttendance s where s.workDate = ?1 and s.staff.shop = ?2")
    List<Staff> findByWorkDateAndShop(Date workDate, Shop shop);

    StaffAttendance findByWorkDateAndStaff(Date workDate, Staff staff);
}
