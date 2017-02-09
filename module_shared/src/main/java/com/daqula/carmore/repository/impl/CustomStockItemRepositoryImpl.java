package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mdc on 2015/9/22.
 */
public class CustomStockItemRepositoryImpl implements CustomStockItemRepositoryInterface {

    @Autowired
    private EntityManager em;



    @Override
    public List<CustomStockItem> calCustomStockInfo(int page, int pageSize, Shop shop, Organization organization, CustomStockItem customStockItem,Date createdDate ) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT  (@rowno\\:=@rowno + 1) as id,")
                .append("cs.sku_type,")
                .append("cs.created_by,")
                .append("cs.created_date, ")
                .append("cs.deleted, ")
                .append("cs.uid, ")
                .append("cs.updated_date, ")
                .append("cs.updated_by, ")
                .append("cs.ver, ")
                .append("cs.name, ")
                .append("cs.is_app_sale, ")
                .append("cs.supplier_id,")
                .append("cs.brand_name, ")
                .append("cs.description,")
                .append("cs.price, ")
                .append("cs.root_category,")
                .append("cs.app_sort, ")
                .append("cs.cover_image_url, ")
                .append("cs.bar_code, ")
                .append("case cs.root_category when 17 then cs.cost" +
                        " ELSE IFNULL(gdts.cost,0) END  as cost, ")
                .append("cs.is_distribution, ")
                .append("cs.organization_id, ")
                .append("cs.param1, ")
                .append("cs.param2, ")
                .append("cs.param3,")
                .append("cs.param4,")
                .append("cs.param5,")
                .append("cs.labor_hours,")
                .append("cs.secondary_category_id,")
                .append("cs.id as view_id, ")
                .append("cs.need_appointment,")
                .append("cs.accessory_category,")
                .append("IFNULL(gdts.number,0)as number")
                .append(" from sku_item cs")
                .append(" left join ( select  dts.custom_stock_item_id ,dts.cost, sum(dts.number)as number  from ( ")
                //-union采购单
                .append(" select pod.custom_stock_item_id as custom_stock_item_id, pod.price as cost, sum(pod.number)as number  from purchase_order po right join purchase_order_detail pod on po.id = pod.purchase_order_id ")
                .append(" where po.purchase_shop_id = ?1 ");
        if (createdDate != null) sqlBuffer.append("and po.updated_date >= ?2 ");
        sqlBuffer.append(" and po.order_status = 3 group by pod.custom_stock_item_id, pod.price ")
                .append(" union all")
                //-union盘点单
                .append(" select sod.custom_stock_item_id as custom_stock_item_id, sod.stock_cost as cost, sum(sod.calculate_number)as number from stocking_order so right join stocking_order_detail sod on so.id = sod.stocking_order_id")
                .append(" where so.shop_id = ?1 ")
                .append(" and so.deleted = false and so.stocking_status = 1 ");
        if (createdDate != null) sqlBuffer.append("and so.created_date >= ?2 ");
        sqlBuffer.append(" group by sod.custom_stock_item_id, sod.stock_cost ")
                .append(" union all")
                //-union供应商退货单
                .append(" select rod.custom_stock_item_id as custom_stock_item_id, rod.cost as cost,(0 -  sum(rod.number))as number from refund_order ro right join refund_order_detail rod on ro.id = rod.refund_order_id ")
                .append(" where ro.deleted = false and ro.order_status = 1  ");
        if(createdDate != null )sqlBuffer.append("and ro.updated_date >= ?2 ");
        sqlBuffer.append("and ro.refund_shop_id = ?1 group by rod.custom_stock_item_id, rod.cost")
                .append(" union all")
                        //-join 销售开单
                .append(" select sod.ordered_item_id as custom_stock_item_id, sod.cost as cost,(0 -  sum(sod.count))as number from settle_order so right join order_detail sod on so.id = sod.settle_order_id ")
                .append(" where so.deleted = false ");
        if(createdDate != null) sqlBuffer.append("and so.finish_date >= ?2 ");
        sqlBuffer.append(" and so.shop_id = ?1 group by sod.ordered_item_id, sod.cost")
                .append(" union all ")
                        //--join耗材领用
                .append(" select mods.custom_stock_item_id as custom_stock_item_id, mods.cost as cost, (0 - sum(mods.number))as number from material_order mo right join material_order_detail mods on mo.id = mods.material_order_id")
                .append(" where mo.deleted = false " );
        if(createdDate != null )sqlBuffer.append("and mo.created_Date >= ?2 ");
        sqlBuffer.append(" and mo.shop_id = ?1  group by mods.custom_stock_item_id, mods.cost ")
                .append(" union all ")
                        //-库存调拨 调入
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, sum(stod.number)as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id ")
                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.in_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost ")
                .append(" union all ")
                        //-库存调拨 调出
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, (0 -  sum(stod.number))as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id")
                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.out_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost")
                .append(" )dts group by dts.custom_stock_item_id, dts.cost) gdts")
                .append(" on gdts.custom_stock_item_id = cs.id ,(select @rowno\\:=0) t")
                .append(" where cs.organization_id = ?4 and cs.deleted = false");
//                .append(" where cs.organization_id = ?4 and cs.root_category = ?5 ");
        if (StringUtils.hasLength(customStockItem.name)) {
            sqlBuffer.append(" and (cs.name like ?3 or cs.bar_code like ?3)");
        }
        if(customStockItem.rootCategory != 99) {
            sqlBuffer.append(" and cs.root_category = ?5 ");
        }
        if(customStockItem.secondaryCategory != null) {
            sqlBuffer.append(" and cs.secondary_category_id = ?6 ");
        }


        sqlBuffer.append(" order by number desc");

        Query q = em.createNativeQuery(sqlBuffer.toString(), CustomStockItem.class).setMaxResults(pageSize)
                .setFirstResult((page - 1) * pageSize);

        q.setParameter(1,shop.id);
        if (createdDate != null) q.setParameter(2,createdDate);
        if (StringUtils.hasLength(customStockItem.name)) {
            q.setParameter(3, "%" + customStockItem.name + "%");
        }
        q.setParameter(4,organization.id);
        if(customStockItem.rootCategory != 99){
            q.setParameter(5,customStockItem.rootCategory);
        }
        if(customStockItem.secondaryCategory != null) {
            q.setParameter(6,customStockItem.secondaryCategory.id);
        }
//        q.setParameter(5,customStockItem.rootCategory);
        List<CustomStockItem> resultList = q.getResultList();
        return resultList;
    }

    @Override
    public int calCustomStockInfoCounts(Shop shop, Organization organization, CustomStockItem customStockItem, Date createdDate) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT count(*) ")
                .append(" from sku_item cs")
                .append(" left join ( select  dts.custom_stock_item_id ,dts.cost, sum(dts.number)as number  from ( ")
                        //-union采购单
                .append(" select pod.custom_stock_item_id as custom_stock_item_id, pod.price as cost, sum(pod.number)as number  from purchase_order po right join purchase_order_detail pod on po.id = pod.purchase_order_id ")
                .append(" where po.purchase_shop_id = ?1 ");
        if (createdDate != null) sqlBuffer.append("and po.updated_date >= ?2 ");
        sqlBuffer.append(" and po.order_status = 3 group by pod.custom_stock_item_id, pod.price ")
                .append(" union all")
                        //-union盘点单
                .append(" select sod.custom_stock_item_id as custom_stock_item_id, sod.stock_cost as cost, sum(sod.calculate_number)as number from stocking_order so right join stocking_order_detail sod on so.id = sod.stocking_order_id")
                .append(" where so.shop_id = ?1 ")
                .append(" and so.deleted = false and so.stocking_status = 1 ");
        if (createdDate != null) sqlBuffer.append("and so.created_date >= ?2 ");
        sqlBuffer.append(" group by sod.custom_stock_item_id, sod.stock_cost ")
                .append(" union all")
                        //-union供应商退货单
                .append(" select rod.custom_stock_item_id as custom_stock_item_id, rod.cost as cost,(0 -  sum(rod.number))as number from refund_order ro right join refund_order_detail rod on ro.id = rod.refund_order_id ")
                .append(" where ro.deleted = false and ro.order_status = 1  ");
        if(createdDate != null )sqlBuffer.append("and ro.updated_date >= ?2 ");
        sqlBuffer.append("and ro.refund_shop_id = ?1 group by rod.custom_stock_item_id, rod.cost")
                .append(" union all")
                        //-join 销售开单
                .append(" select sod.ordered_item_id as custom_stock_item_id, sod.cost as cost,(0 -  sum(sod.count))as number from settle_order so right join order_detail sod on so.id = sod.settle_order_id ")
                .append(" where  so.deleted = false ");
        if(createdDate != null) sqlBuffer.append("and so.finish_date >= ?2 ");
        sqlBuffer.append(" and so.shop_id = ?1 group by sod.ordered_item_id, sod.cost")
                .append(" union all ")
                        //--join耗材领用
                .append(" select mods.custom_stock_item_id as custom_stock_item_id, mods.cost as cost, (0 - sum(mods.number))as number from material_order mo right join material_order_detail mods on mo.id = mods.material_order_id")
                .append(" where mo.deleted = false " );
        if(createdDate != null )sqlBuffer.append("and mo.created_Date >= ?2 ");
        sqlBuffer.append(" and mo.shop_id = ?1  group by mods.custom_stock_item_id, mods.cost ")
                .append(" union all ")
                        //-库存调拨 调入
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, sum(stod.number)as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id ")
                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.in_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost ")
                .append(" union all ")
                        //-库存调拨 调出
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, (0 -  sum(stod.number))as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id")
                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.out_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost")
                .append(" )dts group by dts.custom_stock_item_id, dts.cost) gdts")
                .append(" on gdts.custom_stock_item_id = cs.id ,(select @rowno\\:=0) t")
                .append(" where cs.organization_id = ?4  and cs.deleted = false");
//                .append(" where cs.organization_id = ?4  and cs.root_category = ?5");
        if (StringUtils.hasLength(customStockItem.name)) {
            sqlBuffer.append(" and (cs.name like ?3 or cs.bar_code like ?3)");
        }
        if(customStockItem.rootCategory != 99) {
            sqlBuffer.append(" and cs.root_category = ?5 ");
        }
        if(customStockItem.secondaryCategory != null) {
            sqlBuffer.append(" and cs.secondary_category_id = ?6 ");
        }
        Query q = em.createNativeQuery(sqlBuffer.toString());
        q.setParameter(1, shop.id);
        if (createdDate != null) q.setParameter(2,createdDate);
        if (StringUtils.hasLength(customStockItem.name)) {
            q.setParameter(3, "%" + customStockItem.name + "%");
        }
        q.setParameter(4,organization.id);
        if(customStockItem.rootCategory != 99){
            q.setParameter(5,customStockItem.rootCategory);
        }
        if(customStockItem.secondaryCategory != null) {
            q.setParameter(6,customStockItem.secondaryCategory.id);
        }

        int totalCounts = Integer.parseInt(q.getSingleResult().toString());

        return totalCounts;

    }

    @Override
    public List<CustomStockItem> calForStockingOrder(Shop shop, Organization organization, Date createdDate) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT  (@rowno\\:=@rowno + 1) as id,")
                .append("cs.sku_type,")
                .append("cs.created_by,")
                .append("cs.created_date, ")
                .append("cs.deleted, ")
                .append("cs.uid, ")
                .append("cs.updated_date, ")
                .append("cs.updated_by, ")
                .append("cs.ver, ")
                .append("cs.name, ")
                .append("cs.is_app_sale, ")
                .append("cs.brand_name, ")
                .append("cs.description,")
                .append("cs.cover_image_url, ")
                .append("cs.price, ")
                .append("cs.root_category,")
                .append("cs.app_sort, ")
                .append("cs.bar_code, ")
                .append("IFNULL(gdts.cost,0)as cost, ")
                .append("cs.is_distribution, ")
                .append("cs.organization_id, ")
                .append("cs.param1, ")
                .append("cs.param2, ")
                .append("cs.param3,")
                .append("cs.param4,")
                .append("cs.param5,")
                .append("cs.labor_hours,")
                .append("cs.secondary_category_id,")
                .append("cs.id as view_id, ")
                .append("cs.need_appointment,")
                .append("cs.accessory_category,")
                .append("cs.supplier_id,")
                .append("IFNULL(gdts.number,0)as number")
                .append(" from sku_item cs")
                .append(" right join ( select  dts.custom_stock_item_id ,dts.cost, sum(dts.number)as number  from ( ")
                        //-union采购单
                .append(" select pod.custom_stock_item_id as custom_stock_item_id, pod.price as cost, sum(pod.number)as number  from purchase_order po right join purchase_order_detail pod on po.id = pod.purchase_order_id ")
                .append(" where po.purchase_shop_id = ?1 ");
        if (createdDate != null) sqlBuffer.append("and po.updated_date >= ?2 ");
        sqlBuffer.append(" and po.order_status = 3 group by pod.custom_stock_item_id, pod.price ")
                .append(" union all")
                        //-union盘点单
                .append(" select sod.custom_stock_item_id as custom_stock_item_id, sod.stock_cost as cost, sum(sod.calculate_number)as number from stocking_order so right join stocking_order_detail sod on so.id = sod.stocking_order_id")
                .append(" where so.shop_id = ?1 ")
                .append(" and so.deleted = false and so.stocking_status = '1' ");
        if (createdDate != null) sqlBuffer.append("and so.created_date >= ?2 ");
        sqlBuffer.append(" group by sod.custom_stock_item_id, sod.stock_cost ")
                .append(" union all")
                        //-union供应商退货单
                .append(" select rod.custom_stock_item_id as custom_stock_item_id, rod.cost as cost,(0 -  sum(rod.number))as number from refund_order ro right join refund_order_detail rod on ro.id = rod.refund_order_id ")
                .append(" where ro.deleted = false and ro.order_status = 1  ");
        if(createdDate != null )sqlBuffer.append("and ro.updated_date >= ?2 ");
        sqlBuffer.append("and ro.refund_shop_id = ?1 group by rod.custom_stock_item_id, rod.cost")
                .append(" union all")
                        //-join 销售开单
                .append(" select sod.ordered_item_id as custom_stock_item_id, sod.cost as cost,(0 -  sum(sod.count))as number from settle_order so right join order_detail sod on so.id = sod.settle_order_id ")
                .append(" where so.deleted = false ");
        if(createdDate != null) sqlBuffer.append("and so.finish_date >= ?2 ");
        sqlBuffer.append(" and so.shop_id = ?1 group by sod.ordered_item_id, sod.cost")
                .append(" union all ")
                        //--join耗材领用
                .append(" select mods.custom_stock_item_id as custom_stock_item_id, mods.cost as cost, (0 - sum(mods.number))as number from material_order mo right join material_order_detail mods on mo.id = mods.material_order_id")
                .append(" where mo.deleted = false " );
        if(createdDate != null )sqlBuffer.append("and mo.created_Date >= ?2 ");
        sqlBuffer.append(" and mo.shop_id = ?1  group by mods.custom_stock_item_id, mods.cost ")
                .append(" union all ")
                        //-库存调拨 调入
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, sum(stod.number)as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id ")
                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.in_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost ")
                .append(" union all ")
                        //-库存调拨 调出
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, (0 -  sum(stod.number))as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id")
                .append(" where sto.deleted = false and sto.transfer_status = 3  ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.out_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost")
                .append(" )dts group by dts.custom_stock_item_id, dts.cost) gdts")
                .append(" on gdts.custom_stock_item_id = cs.id ,(select @rowno\\:=0) t")
                .append(" where cs.organization_id = ?4 and cs.root_category <> 17  and cs.deleted = false");
        sqlBuffer.append(" order by cs.brand_name asc");
        Query q = em.createNativeQuery(sqlBuffer.toString(),CustomStockItem.class);

        q.setParameter(1,shop.id);
        if (createdDate != null) q.setParameter(2,createdDate);
        q.setParameter(4,organization.id);
        List<CustomStockItem> resultList = q.getResultList();
        return resultList;
    }


    @Override
    public int getStockNumber(Shop shop, Organization organization, CustomStockItem customStockItem, Date createdDate) {
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT  ")
                .append("IFNULL(gdts.number,0)as number")
                .append(" from sku_item cs")
                .append(" left join ( select  dts.custom_stock_item_id ,dts.cost, sum(dts.number)as number  from ( ")
                        //-union采购单
                .append(" select pod.custom_stock_item_id as custom_stock_item_id, pod.price as cost, sum(pod.number)as number  from purchase_order po right join purchase_order_detail pod on po.id = pod.purchase_order_id ")
                .append(" where po.purchase_shop_id = ?1 ");
        if (createdDate != null) sqlBuffer.append("and po.updated_date >= ?2 ");
        sqlBuffer.append(" and po.order_status = 3 group by pod.custom_stock_item_id, pod.price ")
                .append(" union all")
                        //-union盘点单
                .append(" select sod.custom_stock_item_id as custom_stock_item_id, sod.stock_cost as cost, sum(sod.calculate_number)as number from stocking_order so right join stocking_order_detail sod on so.id = sod.stocking_order_id")
                .append(" where so.shop_id = ?1 ")
                .append(" and so.deleted = false and so.stocking_status = 1 ");
        if (createdDate != null) sqlBuffer.append("and so.created_date >= ?2 ");
        sqlBuffer.append(" group by sod.custom_stock_item_id, sod.stock_cost ")
                .append(" union all")
                        //-union供应商退货单
                .append(" select rod.custom_stock_item_id as custom_stock_item_id, rod.cost as cost,(0 -  sum(rod.number))as number from refund_order ro right join refund_order_detail rod on ro.id = rod.refund_order_id ")
                .append(" where ro.deleted = false and ro.order_status = 1  ");
        if(createdDate != null )sqlBuffer.append("and ro.updated_date >= ?2 ");
        sqlBuffer.append("and ro.refund_shop_id = ?1 group by rod.custom_stock_item_id, rod.cost")
                .append(" union all")
                        //-join 销售开单
                .append(" select sod.ordered_item_id as custom_stock_item_id, sod.cost as cost,(0 -  sum(sod.count))as number from settle_order so right join order_detail sod on so.id = sod.settle_order_id ")
                .append(" where so.deleted = false ");
        if(createdDate != null) sqlBuffer.append("and so.finish_date >= ?2 ");
        sqlBuffer.append(" and so.shop_id = ?1 group by sod.ordered_item_id, sod.cost")
                .append(" union all ")
                        //--join耗材领用
                .append(" select mods.custom_stock_item_id as custom_stock_item_id, mods.cost as cost, (0 - sum(mods.number))as number from material_order mo right join material_order_detail mods on mo.id = mods.material_order_id")
                .append(" where mo.deleted = false " );
        if(createdDate != null )sqlBuffer.append("and mo.created_Date >= ?2 ");
        sqlBuffer.append(" and mo.shop_id = ?1  group by mods.custom_stock_item_id, mods.cost ")
                .append(" union all ")
                        //-库存调拨 调入
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, sum(stod.number)as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id ")
                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.in_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost ")
                .append(" union all ")
                        //-库存调拨 调出
                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, (0 -  sum(stod.number))as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id")
                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
        sqlBuffer.append("and sto.out_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost")
                .append(" )dts group by dts.custom_stock_item_id, dts.cost) gdts")
                .append(" on gdts.custom_stock_item_id = cs.id ,(select @rowno\\:=0) t")
                .append(" where cs.organization_id = ?4 and cs.deleted = false");
        if (StringUtils.hasLength(customStockItem.name)) {
            sqlBuffer.append(" and cs.name = ?3");
        }
        sqlBuffer.append(" and gdts.cost = ?5 ");


        sqlBuffer.append(" order by number desc");

        Query q = em.createNativeQuery(sqlBuffer.toString());
        q.setParameter(1,shop.id);
        if (createdDate != null) q.setParameter(2,createdDate);
        if (StringUtils.hasLength(customStockItem.name)) {
            q.setParameter(3,   customStockItem.name );
        }

        q.setParameter(4,organization.id);
        q.setParameter(5,customStockItem.cost);
        List<BigDecimal> results = q.getResultList();
        BigDecimal result = new BigDecimal(0);
        if (results.size() > 0){
            result =  results.get(0);
        }
        return result.intValue();
    }

//    @Override
//    public List<CustomStockItem> getStockNumber(Shop shop, Organization organization, CustomStockItem customStockItem, Date createdDate) {
//        StringBuffer sqlBuffer = new StringBuffer();
//        sqlBuffer.append("SELECT  (@rowno\\:=@rowno + 1) as id,")
//                .append("cs.sku_type,")
//                .append("cs.created_by,")
//                .append("cs.created_date, ")
//                .append("cs.deleted, ")
//                .append("cs.uid, ")
//                .append("cs.updated_date, ")
//                .append("cs.updated_by, ")
//                .append("cs.ver, ")
//                .append("cs.name, ")
//                .append("cs.is_app_sale, ")
//                .append("cs.supplier_id,")
//                .append("cs.brand_name, ")
//                .append("cs.description,")
//                .append("cs.price, ")
//                .append("cs.root_category,")
//                .append("cs.app_sort, ")
//                .append("cs.cover_image_url, ")
//                .append("cs.bar_code, ")
//                .append("case cs.root_category when 17 then cs.cost" +
//                        " ELSE IFNULL(gdts.cost,0) END  as cost, ")
//                .append("cs.is_distribution, ")
//                .append("cs.organization_id, ")
//                .append("cs.param1, ")
//                .append("cs.param2, ")
//                .append("cs.param3,")
//                .append("cs.param4,")
//                .append("cs.param5,")
//                .append("cs.labor_hours,")
//                .append("cs.secondary_category_id,")
//                .append("cs.id as view_id, ")
//                .append("cs.need_appointment,")
//                .append("cs.accessory_category,")
//                .append("IFNULL(gdts.number,0)as number")
//                .append(" from sku_item cs")
//                .append(" left join ( select  dts.custom_stock_item_id ,dts.cost, sum(dts.number)as number  from ( ")
//                        //-union采购单
//                .append(" select pod.custom_stock_item_id as custom_stock_item_id, pod.price as cost, sum(pod.number)as number  from purchase_order po right join purchase_order_detail pod on po.id = pod.purchase_order_id ")
//                .append(" where po.purchase_shop_id = ?1 ");
//        if (createdDate != null) sqlBuffer.append("and po.updated_date >= ?2 ");
//        sqlBuffer.append(" and po.order_status = 3 group by pod.custom_stock_item_id, pod.price ")
//                .append(" union all")
//                        //-union盘点单
//                .append(" select sod.custom_stock_item_id as custom_stock_item_id, sod.stock_cost as cost, sum(sod.calculate_number)as number from stocking_order so right join stocking_order_detail sod on so.id = sod.stocking_order_id")
//                .append(" where so.shop_id = ?1 ")
//                .append(" and so.deleted = false and so.stocking_status = 1 ");
//        if (createdDate != null) sqlBuffer.append("and so.created_date >= ?2 ");
//        sqlBuffer.append(" group by sod.custom_stock_item_id, sod.stock_cost ")
//                .append(" union all")
//                        //-union供应商退货单
//                .append(" select rod.custom_stock_item_id as custom_stock_item_id, rod.cost as cost,(0 -  sum(rod.number))as number from refund_order ro right join refund_order_detail rod on ro.id = rod.refund_order_id ")
//                .append(" where ro.deleted = false and ro.order_status = 1  ");
//        if(createdDate != null )sqlBuffer.append("and ro.updated_date >= ?2 ");
//        sqlBuffer.append("and ro.refund_shop_id = ?1 group by rod.custom_stock_item_id, rod.cost")
//                .append(" union all")
//                        //-join 销售开单
//                .append(" select sod.ordered_item_id as custom_stock_item_id, sod.cost as cost,(0 -  sum(sod.count))as number from settle_order so right join order_detail sod on so.id = sod.settle_order_id ")
//                .append(" where so.deleted = false ");
//        if(createdDate != null) sqlBuffer.append("and so.finish_date >= ?2 ");
//        sqlBuffer.append(" and so.shop_id = ?1 group by sod.ordered_item_id, sod.cost")
//                .append(" union all ")
//                        //--join耗材领用
//                .append(" select mods.custom_stock_item_id as custom_stock_item_id, mods.cost as cost, (0 - sum(mods.number))as number from material_order mo right join material_order_detail mods on mo.id = mods.material_order_id")
//                .append(" where mo.deleted = false " );
//        if(createdDate != null )sqlBuffer.append("and mo.created_Date >= ?2 ");
//        sqlBuffer.append(" and mo.shop_id = ?1  group by mods.custom_stock_item_id, mods.cost ")
//                .append(" union all ")
//                        //-库存调拨 调入
//                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, sum(stod.number)as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id ")
//                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
//        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
//        sqlBuffer.append("and sto.in_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost ")
//                .append(" union all ")
//                        //-库存调拨 调出
//                .append(" select stod.custom_stock_item_id as custom_stock_item_id, stod.cost as cost, (0 -  sum(stod.number))as number from stock_transfer_order sto right join stock_transfer_order_detail stod on sto.id = stod.stock_transfer_order_id")
//                .append(" where sto.deleted = false and sto.transfer_status = 3 ");
//        if(createdDate != null )sqlBuffer.append("and sto.created_date >= ?2 ");
//        sqlBuffer.append("and sto.out_shop_id = ?1 group by stod.custom_stock_item_id, stod.cost")
//                .append(" )dts group by dts.custom_stock_item_id, dts.cost) gdts")
//                .append(" on gdts.custom_stock_item_id = cs.id ,(select @rowno\\:=0) t")
//                .append(" where cs.organization_id = ?4 and cs.deleted = false");
//        if (StringUtils.hasLength(customStockItem.name)) {
//            sqlBuffer.append(" and cs.name = ?3");
//        }
//        sqlBuffer.append(" and gdts.cost = ?5 ");
//
//
//        sqlBuffer.append(" order by number desc");
//
//        Query q = em.createNativeQuery(sqlBuffer.toString(), CustomStockItem.class);
//        q.setParameter(1,shop.id);
//        if (createdDate != null) q.setParameter(2,createdDate);
//        if (StringUtils.hasLength(customStockItem.name)) {
//            q.setParameter(3,   customStockItem.name );
//        }
//        System.out.println(customStockItem.name + "****" + customStockItem.cost);
//        q.setParameter(4,organization.id);
//        q.setParameter(5,customStockItem.cost);
//        List<CustomStockItem> resultList = q.getResultList();
//        return resultList;
//    }
}
