package com.daqula.carmore.controller;

import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.StockItem;
import com.daqula.carmore.model.template.VehicleModel;
import com.daqula.carmore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.utils.JsonResultBuilder.buildSuccessResult;

@RestController
public class AccessoryController {

    @Autowired
    private SkuItemRepository skuItemRepository;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private SaleShelfRepository saleShelfRepository;

    @Autowired
    private RecommendedAccessoryRepository recommendedAccessoryRepository;

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    /**
     * 产品明细 /api/accessory/{itemId}
     * @param accessoryId
     * @return
     */
    @RequestMapping(value = "/api/accessory/{accessoryId}", method = RequestMethod.GET)
    public Map<String, Object> accessoryDetail(@PathVariable long accessoryId) {
        return buildSuccessResult(skuItemRepository.findOne(accessoryId));
    }

    /**
     * 根据车型,分类,品牌查询配件 /api/accessories
     * @param vehicleModelId
     * @param accessoryCategory
     * @param brandName
     * @return
     */
    @RequestMapping(value = "/api/accessories", method = RequestMethod.GET)
    public Map<String, Object> findAccessoriesByCategoryAndBrand(@RequestParam long vehicleModelId,
                                                                 @RequestParam Integer accessoryCategory,
                                                                 @RequestParam(required = false) String brandName,
                                                                 @RequestParam(required = false) String param1,
                                                                 @RequestParam(required = false) String param2,
                                                                 @RequestParam(required = false) String param3,
                                                                 @RequestParam(required = false) String param4,
                                                                 @RequestParam(required = false) String param5) {
        if (accessoryCategory == StockItem.CATEGORY_ACCESSORY_TIRE) {
            VehicleModel vehicleModel = vehicleModelRepository.findOne(vehicleModelId);
            // 车型为空，返回空结果
            if (vehicleModel == null) return buildSuccessResult(new ArrayList<>());

            String[] frontTireParams = vehicleModel.frontTire.split("[/ R]");
            String[] backTireParams = vehicleModel.backTire.split("[/ R]");
            param1 = "('"+frontTireParams[0]+"','"+backTireParams[0]+"')";
            param2 = "('"+frontTireParams[1]+"','"+backTireParams[1]+"')";
            param3 = "('"+frontTireParams[3]+"','"+backTireParams[3]+"')";
        }

        return buildSuccessResult(saleShelfRepository.findAccessoriesByCategoryAndBrand(vehicleModelId, accessoryCategory,
                brandName, param1, param2, param3, param4, param5));
    }

    /**
     * 配件首页推荐商品 /api/accessories/recommend
     * @return
     */
    @RequestMapping(value = "/api/accessories/recommend", method = RequestMethod.GET)
    public Map<String, Object> recommendAccessories() {
        List<SkuItem> skuItems = new ArrayList<>();
        recommendedAccessoryRepository.findAll().forEach(recommendedAccessory -> {
            recommendedAccessory.skuItem.price = recommendedAccessory.price;
            skuItems.add(recommendedAccessory.skuItem);
        });
        return buildSuccessResult(skuItems);
    }

    /**
     * 查询配件品牌 /api/accessories/brands/{accessoryCategory}
     * @param accessoryCategory
     * @return
     */
    @RequestMapping(value = "/api/accessories/brands/{accessoryCategory}", method = RequestMethod.GET)
    public Map<String, Object> findBrandsByCategory(@PathVariable Integer accessoryCategory) {
        return buildSuccessResult(saleShelfRepository.getBrandNameByAccessoryCategory(accessoryCategory));
    }
}
