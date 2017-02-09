package com.daqula.carmore.repository.specification;

import com.daqula.carmore.model.shop.FixedAsset;
import com.daqula.carmore.model.shop.Shop;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by mdc on 2015/9/16.
 */
public class FixedAssetRepostorySpecifications {
    public static Specification<FixedAsset> filterHasName(String name) {
        return (root, query, cb) -> name != null ? cb.equal(root.get("name"), name) : null;
    }

    public static Specification<FixedAsset> filterHasAssetStatus(int assetStatus) {
        return (root, query, cb) -> assetStatus != 99 ? cb.equal(root.get("assetStatus"), assetStatus) : null;
    }

    public static Specification<FixedAsset> filterByShop(Shop shop) {
        return (root, query, cb) -> shop != null ? cb.equal(root.get("shop"), shop) : null;
    }
}
