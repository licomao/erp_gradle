package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.CustomerPurchasedSuiteItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.CustomerPurchasedSuiteRepositoryInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CustomerPurchasedSuiteRepository extends
        PagingAndSortingRepository<CustomerPurchasedSuite, Long>, JpaSpecificationExecutor
        , CustomerPurchasedSuiteRepositoryInterface{

    List<CustomerPurchasedSuite> findByCustomer(Customer customer);

    Page<CustomerPurchasedSuite> findByCustomer(Customer customer, Pageable pageable);

    @Query("SELECT c FROM CustomerPurchasedSuite c, VehicleInfo v, CustomerProfile p, Suite su " +
           "WHERE v MEMBER OF p.vehicles " +
           "AND v.plateNumber = ?1 " +
           "AND p.customer = c.customer " +
            "AND c.enabled = true " +
            "AND c.suite.id = su.id " +
            "AND     (year(CURRENT_DATE())*365+month(CURRENT_DATE())*30+day(CURRENT_DATE())) -" +
            "            (year(c.startDate)*365+month(c.startDate)*30+day(c.startDate)) <  su.expiation " )
    Page<CustomerPurchasedSuite> findByVehiclePlateNumber(String plateNumber, Pageable pageable);

    @Query("SELECT cps.customer.mobile, cps.suite.suiteType, cps.startDate, cps.enabled, cps.shop.name, erp.realName " +
            "FROM CustomerPurchasedSuite cps, CustomerERPProfile erp " +
            "WHERE cps.customer = erp.customer " +
            "AND (erp.realName like ? OR cps.customer.mobile like ?) " )
    Page<Object[]> findList(String realName,String mobile,Pageable Pageable);

    Long countBySuiteIdAndShopIdAndCustomer(long suiteId, long shopId, Customer customer);

    @Query("SELECT c FROM CustomerPurchasedSuite c, VehicleInfo v, CustomerERPProfile p, Suite su " +
            "WHERE v MEMBER OF p.vehicles " +
            "AND v.plateNumber = ?1 " +
            "AND p.organization = ?2 " +
            "AND p.customer = c.customer " +
            "AND c.enabled = true " +
            "AND c.suite.id = su.id " +
            "AND     (year(CURRENT_DATE())*365+month(CURRENT_DATE())*30+day(CURRENT_DATE())) -" +
            "            (year(c.startDate)*365+month(c.startDate)*30+day(c.startDate)) <  su.expiation " )
    Page<CustomerPurchasedSuite> findByVehiclePlateNumberAndOrganization(String plateNumber, Organization organization, Pageable pageRequest);

}
