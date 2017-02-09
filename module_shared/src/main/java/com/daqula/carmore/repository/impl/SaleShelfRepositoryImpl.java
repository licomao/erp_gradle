package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.StockItem;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class SaleShelfRepositoryImpl implements SaleShelfRepositoryInterface {

    @Autowired
    private EntityManager em;


    @Override
    public List<StockItem> findAccessoriesByCategoryAndBrand(long vehicleModelId, Integer accessoryCategory,
                                                             String brandName, String param1, String param2,
                                                             String param3, String param4, String param5) {
        StringBuilder sql = new StringBuilder();
        sql.append(
            "SELECT " +
                "i.id,i.deleted,i.created_by,i.created_date,i.updated_by,i.updated_date,i.ver,i.uid," +
                "s.price,i.name,i.brand_name,i.sku_type,i.description,i.root_category," +
                "i.secondary_category_id, i.need_appointment, i.cover_image_url," +
                "i.cost, i.accessory_category,i.bar_code,i.is_distribution,i.app_sort,i.is_app_sale" +
                ",i.supplier_id, i.param1,i.param2,i.param3,i.param4,i.param5 " +
            "FROM " +
                "sale_shelf s INNER JOIN sku_item i on i.id = s.sku_item_id " +
            "WHERE i.accessory_category = ").append(accessoryCategory);
        if (brandName != null && !brandName.isEmpty()) {
            sql.append(" AND i.brand_name = ").append(brandName);
        }
        if (param1 != null && !param1.isEmpty()) {
            sql.append(" AND i.param1 in ").append(param1);
        }
        if (param2 != null && !param2.isEmpty()) {
            sql.append(" AND i.param2 in ").append(param2);
        }
        if (param3 != null && !param3.isEmpty()) {
            sql.append(" AND i.param3 in ").append(param3);
        }
        if (param4 != null && !param4.isEmpty()) {
            sql.append(" AND i.param4 in ").append(param4);
        }
        if (param5 != null && !param5.isEmpty()) {
            sql.append(" AND i.param5 in ").append(param5);
        }

        Query q = em.createNativeQuery(sql.toString(), StockItem.class);
        return q.getResultList();
    }

    private String processQueryStr(String param) {
        StringBuilder sb = new StringBuilder(param);
        if (sb.indexOf("(") < 0) {
            sb.insert(0, "(");
        }
        if (sb.indexOf(")") < 0) {
            sb.append(")");
        }
        return sb.toString();
    }
}
