package com.daqula.carmore.controller;

import com.daqula.carmore.ErrorCode;
import com.daqula.carmore.exception.BizException;
import com.daqula.carmore.model.TempPresaleOrder;
import com.daqula.carmore.model.TempSettleOrder;
import com.daqula.carmore.model.admin.CareSuite;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.customer.*;
import com.daqula.carmore.model.order.Comment;
import com.daqula.carmore.model.order.PresaleOrder;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.SaleShelf;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.pojo.ServiceOrder;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.impl.PresaleOrderSpecifications;
import com.daqula.carmore.repository.specification.SettleOrderSpecifications;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;
import static com.daqula.carmore.utils.JsonResultBuilder.buildSuccessResult;

@SuppressWarnings("unchecked")
@RestController
public class OrderController {

    @Autowired
    private SkuItemRepository skuItemRepository;

    @Autowired
    private SuiteRepository suiteRepository;

    @Autowired
    private PresaleOrderRepository presaleOrderRepository;

    @Autowired
    private TempPresaleOrderRepository tempPresaleOrderRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    @Autowired
    private TempSettleOrderRepository tempSettleOrderRepository;

    @Autowired
    private CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    @Autowired
    private CareSuiteRepository careSuiteRepository;

    @Autowired
    private CareSuiteGroupItemRepository careSuiteGroupItemRepository;

    @Autowired
    private VehicleInfoRepository vehicleInfoRepository;

    @Autowired
    private CustomerPurchasedSuiteItemRepository customerPurchasedSuiteItemRepository;

    @Autowired
    private SaleShelfRepository saleShelfRepository;

    @Autowired
	CustomerProfileRepository customerProfileRepository;

    @Autowired
	CommentRepository commentRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    CareSuiteGroupItemPriceRepository careSuiteGroupItemPriceRepository;

    /**
     * 下单 - 洗车/会员卡套餐
     */
    @Transactional
	@RequestMapping(value = "/api/orders/suite/new", method = RequestMethod.POST)
	public Map<String, Object> createSuiteOrder(@RequestParam Long shopId,
                                                @RequestParam Long suiteId,
                                                @AuthenticationPrincipal Customer customer) {
        // 判断是否已购买过同一套餐
        if (customerPurchasedSuiteRepository.countBySuiteIdAndShopIdAndCustomer(suiteId, shopId, customer) > 0)
            throw new BizException(ErrorCode.DUPLICATED_PURCHASED, "You have purchased this suite already.");

        TempSettleOrder tempSettleOrder = new TempSettleOrder();
        tempSettleOrder.shopId = shopId;
        tempSettleOrder.suiteId = suiteId;
        tempSettleOrder.customerId = customer.id;
        Suite suite = suiteRepository.findOne(suiteId);
        if (suite == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity Suite is not found");
        }
        tempSettleOrder.price = suite.price;
        tempSettleOrder = tempSettleOrderRepository.save(tempSettleOrder);
		return buildSuccessResult(map(
                        entry("price", suite.price),
                        entry("tempSettleOrderId", tempSettleOrder.id))
        );
	}

    /**
     * 划扣套餐
     */
    @Transactional
    @RequestMapping(value = "/api/orders/suite/appointment", method = RequestMethod.POST)
	public Map<String, Object> makeAppointment(@RequestParam Long purchasedSuiteItemId,
                                               @RequestParam String vehicleInfoUid,
                                               @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) DateTime appointmentDate,
                                               @AuthenticationPrincipal Customer customer) {
        CustomerPurchasedSuiteItem appointmentItem = customerPurchasedSuiteItemRepository.findOne(purchasedSuiteItemId);
        if (appointmentItem.getTimesLeft() == 0) {
            throw new BizException(ErrorCode.NO_TIMES_LEFT, "CustomerPurchasedSuiteItem<" + appointmentItem.id + "> times left is zero");
        }
        if (presaleOrderRepository.findByAppointedPresaleOrder(appointmentItem) != null) {
            throw new BizException(ErrorCode.UNFINISHED_APPOINTMENT, "CustomerPurchasedSuiteItem< "
                    + appointmentItem.id + "> has unfinished appointment");
        }
        CustomerPurchasedSuite customerPurchasedSuite = appointmentItem.purchasedSuite;
        VehicleInfo vehicleInfo = getVehicleInfo(vehicleInfoUid);
        PresaleOrder presaleOrder = new PresaleOrder();
        presaleOrder.fillSuite(appointmentDate, customer, vehicleInfo, appointmentItem.saleCategory,
                customerPurchasedSuite, appointmentItem);
        presaleOrderRepository.save(presaleOrder);
        return buildSuccessResult(ServiceOrder.buildFromPresaleOrder(presaleOrder, ServiceOrder.ORDER_STATE_SUBSCRIBED));
    }

    /**
     * 下单 - 购买美容/配件
     */
    @Transactional
    @RequestMapping(value = "/api/orders/sku/new", method = RequestMethod.POST)
	public Map<String, Object> createSkuItemOrder(@RequestParam Long shopId,
                                                  @RequestParam Long skuId,
                                                  @RequestParam Integer count,
                                                  @RequestParam UUID vehicleInfoUid,
                                                  @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) DateTime appointmentDate,
                                                  @AuthenticationPrincipal Customer customer) {
        TempPresaleOrder tempPresaleOrder = new TempPresaleOrder();
        tempPresaleOrder.shopId = shopId;
        tempPresaleOrder.skuId = skuId;
        tempPresaleOrder.skuCount = count;
        tempPresaleOrder.vehicleInfoUid = vehicleInfoUid;
        tempPresaleOrder.customerId = customer.id;
        tempPresaleOrder.appointmentDate = appointmentDate;
        SkuItem skuItem = skuItemRepository.findOne(skuId);
        if (skuItem == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity SkuItem is not found");
        }
        Shop shop = shopRepository.findOne(shopId);
        if (shop == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity Shop is not found");
        }
        SaleShelf saleShelf = saleShelfRepository.findBySkuItemAndOrganization(skuItem, shop.organization);
        tempPresaleOrder.price = saleShelf.price*count;
        tempPresaleOrder.saleCategory = saleShelf.saleCategory;
        tempPresaleOrder.orderType = TempPresaleOrder.ORDER_TYPE_SKU_ITEM;
        tempPresaleOrder = tempPresaleOrderRepository.save(tempPresaleOrder);
		return buildSuccessResult(map(
                entry("price", tempPresaleOrder.price),
                entry("tempPresaleOrderId", tempPresaleOrder.id))
        );
	}

    /**
     * 下单 - 维修预约
     */
    @Transactional
    @RequestMapping(value = "/api/orders/repair/new", method = RequestMethod.POST)
    public Map<String, Object> createRepairOrder(@RequestParam Long shopId,
                                                 @RequestParam String vehicleInfoUid,
                                                 @RequestParam String description,
                                                 @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) DateTime appointmentDate,
                                                 @RequestParam(required = false) List<String> images,
                                                 @AuthenticationPrincipal Customer customer) {
        PresaleOrder presaleOrder = new PresaleOrder();
        Shop shop = new Shop();
        shop.id = shopId;
        presaleOrder.fillRepairPresale(appointmentDate, customer, getVehicleInfo(vehicleInfoUid), shop, images, description);
        presaleOrder = presaleOrderRepository.save(presaleOrder);
        return buildSuccessResult(ServiceOrder.buildFromPresaleOrder(presaleOrder, ServiceOrder.ORDER_STATE_SUBSCRIBED));
    }

    /**
     * 下单 - 购买保养套餐
     */
    @Transactional
    @RequestMapping(value = "/api/orders/care/new", method = RequestMethod.POST)
	public Map<String, Object> createCareOrder(@RequestParam long shopId,
                                               @RequestParam List<Long> careSuiteGroupItemIds,
                                               @RequestParam long careSuiteId,
                                               @RequestParam UUID vehicleInfoUid,
                                               @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) DateTime appointmentDate,
                                               @AuthenticationPrincipal Customer customer) {
        TempPresaleOrder tempPresaleOrder = new TempPresaleOrder();
        tempPresaleOrder.shopId = shopId;
        tempPresaleOrder.careSuiteGroupItemIds = careSuiteGroupItemIds;
        tempPresaleOrder.careSuiteId = careSuiteId;
        tempPresaleOrder.vehicleInfoUid = vehicleInfoUid;
        tempPresaleOrder.appointmentDate = appointmentDate;
        tempPresaleOrder.customerId = customer.id;
        tempPresaleOrder.orderType = TempPresaleOrder.ORDER_TYPE_CARE;
        CareSuite careSuite = careSuiteRepository.findOne(careSuiteId);
        if (careSuite == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity CareSuite is not found");
        }
        double price = careSuiteGroupItemPriceRepository.calculatePrice(careSuiteGroupItemIds);
        tempPresaleOrder.price = price;
        tempPresaleOrder = tempPresaleOrderRepository.save(tempPresaleOrder);
		return buildSuccessResult(map(
                entry("price", price),
                entry("tempPresaleOrderId", tempPresaleOrder.id)
        ));
	}

    /**
     * 列出所有订单，已完成订单分页，其它状态订单不分页。只有在第一页时会列出其它状态订单
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/order", method = RequestMethod.GET)
    public Map<String, Object> listAllOrders(@RequestParam(required = false) Integer saleCategory,
                                             @RequestParam Integer page,
                                             @RequestParam Integer rows,
                                             @AuthenticationPrincipal Customer customer) {
        List<ServiceOrder> allServiceOrders = new ArrayList<>();

        if (page == 1) {
            allServiceOrders.addAll(getSubscribedServiceOrders(saleCategory, customer));
            allServiceOrders.addAll(getNeedCommentedServiceOrders(getListOrderQuerySpec(saleCategory, customer)));
        }
        allServiceOrders.addAll(getDoneServiceOrders(page, rows, getListOrderQuerySpec(saleCategory, customer)));

        return buildSuccessResult(allServiceOrders);
    }

    /**
     * 列出已预约订单
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/order/subscribed", method = RequestMethod.GET)
    public Map<String, Object> listSubscribedOrders(@RequestParam(required = false) Integer saleCategory,
                                                    @AuthenticationPrincipal Customer customer) {
        return buildSuccessResult(getSubscribedServiceOrders(saleCategory, customer));
    }

    /**
     * 列出待评价订单
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/order/need_comment", method = RequestMethod.GET)
    public Map<String, Object> listNeedCommentOrders(@RequestParam(required = false) Integer saleCategory,
                                                     @AuthenticationPrincipal Customer customer) {
        return buildSuccessResult(getNeedCommentedServiceOrders(getListOrderQuerySpec(saleCategory, customer)));
    }

    /**
     * 列出已完成订单
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/order/done", method = RequestMethod.GET)
    public Map<String, Object> listDoneOrders(@RequestParam(required = false) Integer saleCategory,
                                              @RequestParam Integer page,
                                              @RequestParam Integer rows,
                                              @AuthenticationPrincipal Customer customer) {
        return buildSuccessResult(getDoneServiceOrders(page, rows, getListOrderQuerySpec(saleCategory, customer)));
    }

    /**
     * 返回订单的评论内容
     */
    @RequestMapping(value = "/api/order/comment", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public Map<String, Object> getComment(@RequestParam UUID settleOrderUid) {

        return buildSuccessResult(commentRepository.findBySettleOrderUid(settleOrderUid));
    }

    /**
     * 列出已完成订单
     */
    @Transactional
    @RequestMapping(value = "/api/order/comment", method = RequestMethod.POST)
    public Map<String, Object> commentOrder(@RequestParam UUID settleOrderUid,
                                            @RequestParam String commentContent,
                                            @RequestParam(required = false) Integer rating1,
                                            @RequestParam(required = false) Integer rating2,
                                            @RequestParam(required = false) Integer rating3,
                                            @RequestParam(required = false) Integer rating4,
                                            @RequestParam(required = false) Integer rating5,
                                            @RequestParam(required = false) Integer rating6,
                                            @RequestParam(required = false) Integer rating7,
                                            @AuthenticationPrincipal Customer customer) {
        CustomerAppProfile customerAppProfile = customerProfileRepository.findAppProfileByCustomer(customer);
        SettleOrder settleOrder = settleOrderRepository.findByUid(settleOrderUid);
        Comment comment = new Comment();
        comment.comment = commentContent;
        comment.customer = customerAppProfile;
        comment.shop = settleOrder.shop;
        comment.settleOrder = settleOrder;
        if (rating1 != null) comment.rating1 = rating1;
        if (rating2 != null) comment.rating2 = rating2;
        if (rating3 != null) comment.rating3 = rating3;
        if (rating4 != null) comment.rating4 = rating4;
        if (rating5 != null) comment.rating5 = rating5;
        if (rating6 != null) comment.rating6 = rating6;
        if (rating7 != null) comment.rating7 = rating7;
        commentRepository.save(comment);
        settleOrder.commented = true;
        settleOrderRepository.save(settleOrder);
        return buildSuccessResult();
    }

    //**************************************************************************
    // Util Methods
    //**************************************************************************

    public Specifications<SettleOrder> getListOrderQuerySpec(Integer saleCategory, Customer customer) {
        return Specifications
                .where(SettleOrderSpecifications.filteredBySaleCategory(saleCategory))
                .and(SettleOrderSpecifications.filteredByCustomer(customer));
    }

    public List<ServiceOrder> getDoneServiceOrders(Integer page, Integer rows, Specifications settleOrderSpec) {
        Page<SettleOrder> finishedOrderPage = settleOrderRepository.findAll(settleOrderSpec.
                and(SettleOrderSpecifications.filteredByCommented(true)).
                and(SettleOrderSpecifications.isNotPurchaseSuiteOrder()), new PageRequest(page - 1, rows));
        return ServiceOrder.buildListFromSettleOrders(finishedOrderPage.getContent(),
                ServiceOrder.ORDER_STATE_DONE);
    }


    public List<ServiceOrder> getNeedCommentedServiceOrders(Specifications settleOrderSpec) {
        List<SettleOrder> needCommentOrders = settleOrderRepository.findAll(settleOrderSpec.
                and(SettleOrderSpecifications.filteredByCommented(false)).
                and(SettleOrderSpecifications.createDaysLessThan(30)).
                and(SettleOrderSpecifications.isNotPurchaseSuiteOrder()));
        return ServiceOrder.buildListFromSettleOrders(needCommentOrders, ServiceOrder.ORDER_STATE_NEED_COMMENT);
    }

/*    private List<SettleOrder> filterPurchasedSuites(List<SettleOrder> originList) {
        // 过滤orderedItem为空的订单,例如购买套餐的订单
        List<SettleOrder> resultList = new ArrayList<>();
        originList.forEach(settleOrder -> {
            if (settleOrder.orderDetails != null
                    && settleOrder.orderDetails.size() == 1
                    && settleOrder.orderDetails.get(0).orderedItem != null) {
                resultList.add(settleOrder);
            }
        });
        return resultList;
    }*/

    public List<ServiceOrder> getSubscribedServiceOrders(Integer saleCategory, Customer customer) {
        Specifications preSaleOrderSpec = Specifications.where(
                PresaleOrderSpecifications.filteredBySaleCategory(saleCategory)).
                and(PresaleOrderSpecifications.filteredByCustomer(customer));
        List<PresaleOrder> presaleOrders = presaleOrderRepository.findAll(
                preSaleOrderSpec.and(PresaleOrderSpecifications.settleOrderIsNull()));
        return ServiceOrder.buildListFromPresaleOrders(presaleOrders, ServiceOrder.ORDER_STATE_SUBSCRIBED);
    }

    public VehicleInfo getVehicleInfo(String vehicleInfoUid) {
        VehicleInfo vehicleInfo = vehicleInfoRepository.findByUid(UUID.fromString(vehicleInfoUid));
        if (vehicleInfo == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity VehicleInfo is not found");
        }
        return vehicleInfo;
    }

}
