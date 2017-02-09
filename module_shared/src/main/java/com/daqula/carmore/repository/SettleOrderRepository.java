package com.daqula.carmore.repository;


import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;
import java.util.UUID;

public interface SettleOrderRepository extends PagingAndSortingRepository<SettleOrder, Long>, JpaSpecificationExecutor<SettleOrder> {

    Page<SettleOrder> findBySaleNoViewAndShop(String saleNoView, Shop shop, Pageable pageable);

    Page<SettleOrder> findByShop(Shop shop, Pageable pageable);

    List<SettleOrder> findBySaleNoView(String saleNoView);

    @Query("SELECT s FROM SettleOrder s, CustomerERPProfile c WHERE s.saleNoView = ?1 AND s.shop = ?2 AND c.id = ?3 AND s.vehicleInfo MEMBER OF c.vehicles")
    Page<SettleOrder> findBySaleNoViewAndVehicleInfos(String saleNoView, Shop shop, long customerERPProfileId, Pageable pageable);

    @Query("SELECT s FROM SettleOrder s, CustomerERPProfile c WHERE s.shop = ?1 AND c.id = ?2 AND s.vehicleInfo MEMBER OF c.vehicles")
    Page<SettleOrder> findByShopAndCustomer(Shop shop, long customerERPProfileId, Pageable pageable);

    @Query("SELECT IFNULL(MAX(f.saleNo),0) + 1 FROM SettleOrder f WHERE f.shop = ?1")
    Long findMaxOrderNum(Shop shop);

    SettleOrder findByUid(UUID uid);

    SettleOrder findBySaleNoAndShop(long s, Shop shop);

    @Query("SELECT s FROM SettleOrder s WHERE s.customerPurchasedSuite.shop = ? AND s.customerPurchasedSuite.shop <> s.shop")
    Page<SettleOrder> findRemoteList(Shop shop,Pageable pageable);


    @Query("SELECT s FROM SettleOrder s WHERE s.customerPurchasedSuite = ? AND s.deleted = false")
    List<SettleOrder> findSuiteList(CustomerPurchasedSuite customerPurchasedSuite);

    @Query("SELECT s.saleNoView, c.realName, s.customer.mobile, s.vehicleInfo.plateNumber, s.receiver.name, s.createdDate, s.isFinish, s.finishDate, s.deleted, s.presaleOrder.id, s.customerPurchasedSuite.id, s.payment.cashAmount+s.payment.posAmount+s.payment.appAmount+s.payment.otherAmount , s.remark,s.id" +
                " FROM SettleOrder s, CustomerERPProfile c WHERE s.shop = ?1 AND s.saleNoView LIKE ?2 AND s.customer.mobile LIKE ?3 AND s.deleted = ?4 AND s.createdDate >= ?5 AND s.createdDate <= ?6 AND s.customer.id=c.customer.id AND s.vehicleInfo.plateNumber like ?7   And (s.isFinish = ?8 or s.isFinish = ?9)  AND c.organization=?10 order by s.createdDate desc")
    Page<Object> searchsettleinfoWithTime(Shop shop, String saleNoView, String tel,boolean isDeleted, DateTime createDateStart, DateTime createDateEnd, String plateNumber,boolean isFone,boolean isFTwo,Organization organization, Pageable pageable);

    @Query("SELECT s.saleNoView, c.realName, s.customer.mobile, s.vehicleInfo.plateNumber, s.receiver.name, s.createdDate, s.isFinish, s.finishDate, s.deleted, s.presaleOrder.id, s.customerPurchasedSuite.id, s.payment.cashAmount+s.payment.posAmount+s.payment.appAmount+s.payment.otherAmount, s.remark,s.id" +
            " FROM SettleOrder s, CustomerERPProfile c WHERE s.shop = ?1 AND s.saleNoView LIKE ?2 AND s.customer.mobile LIKE ?3 AND s.deleted = ?4 AND s.customer.id=c.customer.id  AND s.vehicleInfo.plateNumber LIKE ?5 And (s.isFinish = ?6 or s.isFinish = ?7) AND c.organization=?8  order by s.createdDate desc")
    Page<Object> searchsettleinfoWithoutTime(Shop shop, String saleNoView, String tel,boolean isDeleted,String plateNumber,boolean isFone,boolean isFTwo, Organization organization, Pageable pageable);
}

