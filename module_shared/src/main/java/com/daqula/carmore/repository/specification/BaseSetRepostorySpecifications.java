package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.shop.FixedAsset;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/9/16.
 */
public class BaseSetRepostorySpecifications {
    public static Specification<BaseSet> filterHasOperationPrice(double operationPrice) {
        return (root, query, cb) -> operationPrice != 0 ? cb.equal(root.get("operationPrice"), operationPrice) : null;
    }

    public static Specification<BaseSet> filterHasPosRate(String posRate) {
        return (root, query, cb) -> posRate != null ? cb.equal(root.get("posRate"), posRate) : null;
    }

    public static Specification<BaseSet> filterHasPosTopRate(String posTopRate) {
        return (root, query, cb) -> posTopRate != null ? cb.equal(root.get("posTopRate"), posTopRate) : null;
    }

}
