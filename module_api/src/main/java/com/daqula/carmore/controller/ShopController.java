package com.daqula.carmore.controller;

import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.order.Comment;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.pojo.AppSuite;
import com.daqula.carmore.pojo.CommentData;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.utils.JsonResultBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.daqula.carmore.utils.JsonResultBuilder.buildSuccessResult;

@RestController
public class ShopController {

    @Autowired
	ShopRepository shopRepository;

    @Autowired
    CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    @Autowired
    VehicleModelRepository vehicleModelRepository;

    @Autowired
    CareSuiteRepository careSuiteRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    SaleShelfRepository saleShelfRepository;

    @Autowired
    PresaleOrderRepository presaleOrderRepository;

    /**
     * 商店列表 /api/shops
     * @param page 第几页
     * @param rows 每页记录数
     * @param latitude 纬度
     * @param longitude 经度
     * @param radiusInKilometer 半径范围
     * @return {"result":[{"id":1,"name":"龙里店","address":"上海市闵行区龙里路","openingHours":"9:00-22:00","phone":"021-5833333","latitude":31.138849,"longitude":121.414835,"promotionTag":"换胎;保养;洗车;镀金;打蜡;抛光"},
	 * 						{"id":3,"name":"龙茗","address":"上海市闵行区龙茗路","openingHours":"9:00-22:00","phone":"021-5833333","latitude":31.138849,"longitude":121.414835,"promotionTag":"换胎;保养;洗车;镀金;打蜡;抛光"}],
	 * 						"msg":"","svrTime":1438095530628,"retCode":0}
     */
    @Transactional(readOnly = true)
	@RequestMapping(value = "/api/shops", method = RequestMethod.GET)
	public Map<String, Object> listShops(@RequestParam int page,
                                         @RequestParam int rows,
                                         @RequestParam int radiusInKilometer,
                                         @RequestParam double latitude,
                                         @RequestParam double longitude) {

		return buildSuccessResult(shopRepository.findShopNearBy(latitude, longitude, page, rows, radiusInKilometer));
	}

    /**
     * 查询门店详情
     *
     * @param shopId
     * @return
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/shop/{shopId}", method = RequestMethod.GET)
    public Map<String, Object> listShop(@PathVariable long shopId) {
        return buildSuccessResult(shopRepository.findOne(shopId));
    }

    /**
     * 门店套餐列表 /api/shop/{shopId}/suites
     *
     * @param shopId
     * @param saleCategory
     * @return
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/shop/{shopId}/suites", method = RequestMethod.GET)
    public Map<String, Object> listForSaleSuites(@PathVariable long shopId,
                                                 @RequestParam int saleCategory,
                                                 @AuthenticationPrincipal Customer customer) {
        Shop shop = shopRepository.findOne(shopId);
        List<Object[]> result = saleShelfRepository.
                findSuiteByOrganizationAndSaleCategory(shop.organization, saleCategory);
        List<Suite> suites = result.stream().map(objects -> {
            Suite suite = (Suite) objects[0];
            suite.price = (Double) objects[1];
            return suite;
        }).collect(Collectors.toList());
        List<AppSuite> appSuiteList = AppSuite.build(suites);

        if (customer != null) {
            List<CustomerPurchasedSuite> purchasedSuites = customerPurchasedSuiteRepository.findByCustomer(customer);
            appSuiteList.forEach(appSuite -> {
                purchasedSuites.stream()
                        .filter(purchasedSuite -> purchasedSuite.suite.id == appSuite.suiteId)
                        .findAny().ifPresent(purchasedSuite -> {
                    appSuite.startDate = purchasedSuite.startDate;
                    appSuite.purchasedSuiteId = purchasedSuite.id;
                    appSuite.suiteItems.forEach(item -> {
                        // 设置套餐项目的剩余次数和使用次数
                        purchasedSuite.purchasedSuiteItems.stream()
                                .filter(purchasedSuiteItem -> purchasedSuiteItem.suiteItem.id == item.suiteItemId)
                                .findAny().ifPresent(purchasedSuiteItem -> {
                            item.purchasedSuiteItemId = purchasedSuiteItem;
                            item.usedTimes = purchasedSuiteItem.usedTimes;
                            item.timesLeft = purchasedSuiteItem.getTimesLeft();
                            item.appointed = presaleOrderRepository
                                    .findByAppointedPresaleOrder(purchasedSuiteItem) != null;
                        });
                    });
                });
            });
        }

        return JsonResultBuilder.buildSuccessResult(appSuiteList);
    }


    /**
     * 门店套餐列表 /api/shop/{shopId}/sku
     * @param shopId
     * @param saleCategory
     * @return
     */
    @RequestMapping(value = "/api/shop/{shopId}/sku", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public Map<String, Object> listForSaleSkuItem(@PathVariable long shopId,
                                                  @RequestParam int saleCategory) {
        Shop shop = shopRepository.findOne(shopId);
        List<Object[]> result = saleShelfRepository.
                findSkuByOrganizationAndSaleCategory(shop.organization, saleCategory);
        List<SkuItem> skuItems = result.stream().map(objects -> {
            SkuItem skuItem = (SkuItem) objects[0];
            skuItem.price = (Double) objects[1];
            return skuItem;
        }).collect(Collectors.toList());
        return JsonResultBuilder.buildSuccessResult(skuItems);
    }

    /**
     * 推荐车型对应的保养产品列表(多种类型的item) /api/shop/{shopId}/caresuite
     * @param shopId
     * @return
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/shop/{shopId}/caresuite", method = RequestMethod.GET)
    public Map<String, Object> recommendCareSuite(@PathVariable long shopId,
                                                  @RequestParam int mileage,
                                                  @RequestParam int month) {
        Shop shop = shopRepository.findOne(shopId);
        return JsonResultBuilder.buildSuccessResult(
                careSuiteRepository.recommendCareSuite(mileage, month, shop.organization.id));
    }

    /**
     * 评论列表 /api/shop/{shopId}/comments
     * @param shopId
     * @param page 第几页
     * @param rows 每页记录数
     * @return
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/shop/{shopId}/comments", method = RequestMethod.GET)
    public Map<String, Object> listShopComments(@PathVariable long shopId,
                                                @RequestParam int page,
                                                @RequestParam int rows) {
        Shop shop = new Shop();
        shop.id = shopId;
        Page<Comment> pageData = commentRepository.findByShop(shop, new PageRequest(page-1, rows));
        return JsonResultBuilder.buildSuccessResult(CommentData.build(pageData.getContent()));
    }

}
