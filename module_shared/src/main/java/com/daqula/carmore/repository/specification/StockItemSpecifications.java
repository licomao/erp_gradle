package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.StockItem;
import org.springframework.data.jpa.domain.Specification;

public class StockItemSpecifications {

    public static Specification<StockItem> filterByParam(String paramName, String paramValue) {
        return (root, query, cb) -> (paramValue != null && !"".equals(paramValue)) ?
                cb.equal(root.get(paramName), paramValue) : null;
    }

    public static Specification<StockItem> filterByAccessoryCategory(Integer accessoryCategory) {
        return (root, query, cb) -> accessoryCategory != null ?
                cb.equal(root.get("accessoryCategory"), accessoryCategory) : null;
    }
}
