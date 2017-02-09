package com.daqula.carmore.repository;

import com.daqula.carmore.model.order.OrderDetail;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by shd.
 */
public interface OrderDetailRepository extends PagingAndSortingRepository<OrderDetail, Long> {

    @Query("SELECT o,s FROM OrderDetail o ,SettleOrder s  WHERE s.shop.id = ?1 AND s.deleted = false AND s.createdDate > ?2 AND s.createdDate < ?3 AND o.orderedItem.name like ?4 AND o MEMBER OF s.orderDetails ")
    Page<Object> searchOrderDetailAndItem (Long shopid, DateTime createDateStart, DateTime createDateEnd,String skuItemName ,Pageable pageable);

    /**
     * 查询list 无品名
     * @param shopid
     * @param createDateStart
     * @param createDateEnd
     * @param pageable
     * @return
     */
    @Query("SELECT o,s FROM OrderDetail o ,SettleOrder s  WHERE s.shop.id = ?1 AND s.deleted = false AND s.createdDate > ?2 AND s.createdDate < ?3 AND o MEMBER OF s.orderDetails ")
    Page<Object> searchOrderDetail (Long shopid, DateTime createDateStart, DateTime createDateEnd ,Pageable pageable);

    /**
     * Excel 导出
     * @param shopid
     * @param createDateStart
     * @param createDateEnd
     * @return
     */
    @Query("SELECT o FROM OrderDetail o ,SettleOrder s  WHERE s.shop.id = ?1 AND s.deleted = false AND s.createdDate > ?2 AND s.createdDate < ?3 AND o MEMBER OF s.orderDetails ")
    List<OrderDetail> searchOrderDetail (Long shopid, DateTime createDateStart, DateTime createDateEnd);

    @Query("SELECT o FROM OrderDetail o ,SettleOrder s  WHERE s.shop.id = ?1 AND s.finishDate > ?2 AND s.finishDate < ?3 AND s.deleted = false AND s.isFinish = true AND o MEMBER OF s.orderDetails ")
    List<OrderDetail>getOrderDetailInfo(Long shopId,DateTime createDateStart, DateTime createDateEnd);
}
