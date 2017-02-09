package com.daqula.carmore.controller;

import com.daqula.carmore.CarmoreProperties;
import com.daqula.carmore.ErrorCode;
import com.daqula.carmore.exception.BizException;
import com.daqula.carmore.model.TempPresaleOrder;
import com.daqula.carmore.model.TempSettleOrder;
import com.daqula.carmore.model.admin.CareSuite;
import com.daqula.carmore.model.admin.CareSuiteGroupItem;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.customer.CustomerPurchasedSuite;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.model.order.PresaleOrder;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.shop.SaleShelf;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.pojo.ServiceOrder;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.utils.SignUtil;
import com.pingplusplus.Pingpp;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Webhooks;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.daqula.carmore.utils.JsonResultBuilder.buildResult;
import static com.daqula.carmore.utils.JsonResultBuilder.buildSuccessResult;

@RestController
public class PaymentController {

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    SkuItemRepository skuItemRepository;

    @Autowired
    SuiteRepository suiteRepository;

    @Autowired
    PresaleOrderRepository presaleOrderRepository;

    @Autowired
    TempPresaleOrderRepository tempPresaleOrderRepository;

    @Autowired
    SettleOrderRepository settleOrderRepository;

    @Autowired
    TempSettleOrderRepository tempSettleOrderRepository;

    @Autowired
    CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    @Autowired
    CareSuiteRepository careSuiteRepository;

    @Autowired
    CareSuiteGroupItemRepository careSuiteGroupItemRepository;

    @Autowired
    VehicleInfoRepository vehicleInfoRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SaleShelfRepository saleShelfRepository;

    @Autowired
    CarmoreProperties carmoreProperties;

    @Autowired
    CustomerProfileRepository customerProfileRepository;

    @Autowired
    ResourceLoader resourceLoader;

    private static String ORDER_ID_PREFIX_PRE_SALE = "PreSale";

    private static String ORDER_ID_PREFIX_SETTLE = "Settle";

    /**
     * 获取支付charge对象  /api/payment/charge
     * @param channel
     * @param tempPresaleOrderId
     * @param tempSettleOrderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/payment/charge", method = RequestMethod.GET)
    public Map<String, Object> charge(@RequestParam String channel,
                                      @RequestParam(required = false) Long tempPresaleOrderId,
                                      @RequestParam(required = false) Long tempSettleOrderId) throws Exception {
        Pingpp.apiKey = carmoreProperties.getPingppApiKey();
        Map<String, Object> chargeMap = new HashMap<>();
        Map<String, String> extra = new HashMap<>();
        Map<String, String> metadata = new HashMap<>();
        double amount = 0.0;
        // todo subject body ip这些怎么设置
        chargeMap.put("currency", "cny");
        chargeMap.put("subject", "Your Subject");
        chargeMap.put("body", "Your Body");
        if (tempPresaleOrderId != null && tempPresaleOrderId != 0) {
            chargeMap.put("order_no", ORDER_ID_PREFIX_PRE_SALE + tempPresaleOrderId);
            TempPresaleOrder tempPresaleOrder = tempPresaleOrderRepository.findOne(tempPresaleOrderId);
            metadata.put("uid", tempPresaleOrder.uid.toString());
            amount = tempPresaleOrder.price;
        } else if (tempSettleOrderId != null && tempSettleOrderId != 0) {
            chargeMap.put("order_no", ORDER_ID_PREFIX_SETTLE + tempSettleOrderId);
            TempSettleOrder tempSettleOrder = tempSettleOrderRepository.findOne(tempSettleOrderId);
            amount = tempSettleOrder.price;
            metadata.put("uid", tempSettleOrder.uid.toString());
        } else {
            throw new BizException(ErrorCode.INVALID_PAYMENT, "temp payment record is not specified");

            // html5测试用
//            chargeMap.put("order_no", "test123");
//            amount = 1.0;
        }
        // html5测试用
//        if ("alipay_wap".equals(channel)) {
//            extra.put("success_url", "http://qc.tunnel.mobi/api/pay/success");
//        }
        chargeMap.put("metadata", metadata);
        chargeMap.put("extra", extra);
        chargeMap.put("amount", (int)amount*100);
        chargeMap.put("channel", channel);
        chargeMap.put("client_ip", "127.0.0.1");
        Map<String, String> app = new HashMap<>();
        app.put("id",carmoreProperties.getPingppAppId());
        chargeMap.put("app", app);
        Charge charge = Charge.create(chargeMap);
        return buildSuccessResult(charge);

    }

    /**
     * 客户端主动查询支付状态生成订单
     * @param chargeId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/payment/charge/query", method = RequestMethod.POST)
    @Transactional
	public Map<String, Object> queryCharge(@RequestParam String chargeId) throws Exception {
        Pingpp.apiKey = carmoreProperties.getPingppApiKey();
        Charge charge = Charge.retrieve(chargeId);
        if (charge.getPaid()) {
            Object order = createOrFindOrder(charge);
            if (order instanceof PresaleOrder) {
                return buildSuccessResult(ServiceOrder.buildFromPresaleOrder((PresaleOrder)order, ServiceOrder.ORDER_STATE_SUBSCRIBED));
            } else {
                return buildSuccessResult(ServiceOrder.buildFromSettleOrder((SettleOrder)order, ServiceOrder.ORDER_STATE_DONE));
            }
        } else {
            return buildResult(ErrorCode.CHARGE_NOT_PAID, "Charge is not paid");
        }
    }

    /**
     * ping++ webHook
     * @param request
     * @param response
     * @param body
     * @throws Exception
     */
    @Transactional
    @RequestMapping(value = "/api/payment/hook", method = RequestMethod.POST)
    public void webHook(HttpServletRequest request, HttpServletResponse response,
                        @RequestBody String body) throws Exception{
        boolean isValid = SignUtil.verifyData(body.getBytes(),
                Base64.decodeBase64(request.getHeader("x-pingplusplus-signature")),
                SignUtil.getPubKey(resourceLoader));
        if (isValid) {
            Event event = Webhooks.eventParse(body);
            if ("charge.succeeded".equals(event.getType())) {
                Map<String, Object> dataMap = (Map)event.getData().get("object");
                Charge charge = new Charge();
                charge.setOrderNo((String) dataMap.get("order_no"));
                double amount = (double)dataMap.get("amount");
                charge.setAmount((int)amount);
                charge.setPaid((boolean) dataMap.get("paid"));
                charge.setId((String) dataMap.get("order_no"));
                if (carmoreProperties.isCreateOrderByWebHook()) {
                    createOrFindOrder(charge);
                }
                // ping++要求返回200，否则会连续发8次webHook
                response.setStatus(200);
            }
        }
    }

    public Object createOrFindOrder(Charge charge) {
        Payment payment = new Payment();
        payment.chargeId = charge.getId();
        payment.amount = charge.getAmount()/100;

        Object order = null;
        Customer customer = null;
        Shop shop = null;
        String orderNo = charge.getOrderNo();
        if (orderNo.startsWith(ORDER_ID_PREFIX_PRE_SALE)) {
            TempPresaleOrder tempPresaleOrder = tempPresaleOrderRepository.findOne(
                    Long.parseLong(orderNo.split(ORDER_ID_PREFIX_PRE_SALE)[1]));
            order = findOrderByUid(tempPresaleOrder.uid, true);
            customer = customerRepository.findOne(tempPresaleOrder.customerId);
            shop = shopRepository.findOne(tempPresaleOrder.shopId);
            payment.customer = customer;
            if (order == null) {
                payment = paymentRepository.save(payment);
                // presale order 有配件和美容套餐两种
                if (tempPresaleOrder.orderType.equals(TempPresaleOrder.ORDER_TYPE_SKU_ITEM)) {
                    return createAccessoryOrder(tempPresaleOrder.shopId, tempPresaleOrder.skuId,
                            tempPresaleOrder.skuCount, tempPresaleOrder.vehicleInfoUid,
                            tempPresaleOrder.appointmentDate, customer, tempPresaleOrder.uid,
                            tempPresaleOrder.saleCategory, payment);
                } else {
                    return createCareOrder(tempPresaleOrder.shopId, tempPresaleOrder.careSuiteGroupItemIds,
                            tempPresaleOrder.careSuiteId, tempPresaleOrder.vehicleInfoUid, tempPresaleOrder.appointmentDate,
                            customerRepository.findOne(tempPresaleOrder.customerId), tempPresaleOrder.uid, payment);
                }
            }
        } else if (orderNo.startsWith(ORDER_ID_PREFIX_SETTLE)) {
            TempSettleOrder tempSettleOrder = tempSettleOrderRepository.findOne(
                    Long.parseLong(orderNo.split(ORDER_ID_PREFIX_SETTLE)[1]));
            order = findOrderByUid(tempSettleOrder.uid, false);
            customer = customerRepository.findOne(tempSettleOrder.customerId);
            shop = shopRepository.findOne(tempSettleOrder.shopId);
            payment.customer = customer;
            if (order == null) {
                payment = paymentRepository.save(payment);
                return createSuiteOrder(tempSettleOrder.shopId, tempSettleOrder.suiteId,
                        customer, tempSettleOrder.uid, payment);
            }
        }

        bindShopIfNeeded(customer, shop);

        return order;
    }

    public Object findOrderByUid(UUID uid, boolean isPresale) {
        if (isPresale) {
            return presaleOrderRepository.findByUid(uid);
        } else {
            return settleOrderRepository.findByUid(uid);
        }
    }

/*    @RequestMapping(value = "/api/payment/push", method = RequestMethod.GET)
    public void push(@RequestParam String content, @RequestParam String title, @RequestParam int messageType) {
        XingeApp xinge = new XingeApp(ACCESS_ID, SECRET_KEY);
        Message message = new Message();
        // 秒为单位，10表示10分钟
		message.setExpireTime((int)TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES));
		message.setTitle(title);
		message.setContent(content);
		message.setType(messageType);

        // todo push to IOS, 单个账号怎么push
		JSONObject ret = xinge.pushAllDevice(XingeApp.DEVICE_ANDROID, message);
        System.out.println(ret.toString());
    }*/

    public SettleOrder createSuiteOrder(Long shopId, Long suiteId, Customer customer, UUID uuid, Payment payment) {
        // 洗车，会员卡套餐只有settleorder，美容有settle和presale，美容预约每次产生一条presale， sku只有settle
        // 只有洗车美容会员卡需要purchaseSuite， orderdetial针对每个purchaseitem都要生成一条
        Shop shop = getShop(shopId);
        Suite suite = suiteRepository.findOne(suiteId);
        if (suite == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity Suite is not found");
        }
        // 创建临时订单，支付成功才能预约
        CustomerPurchasedSuite customerPurchasedSuite = CustomerPurchasedSuite.build(customer, suite, shop);
        customerPurchasedSuiteRepository.save(customerPurchasedSuite);
        SettleOrder settleOrder = new SettleOrder();
        SaleShelf saleShelf = saleShelfRepository.findBySuiteAndOrganization(suite, shop.organization);
        if (saleShelf == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity SaleShelf is not found");
        }
        settleOrder.fill(shop, saleShelf.saleCategory, customer, suite, uuid, payment);
        settleOrder = settleOrderRepository.save(settleOrder);
		return settleOrder;
    }

    public PresaleOrder createAccessoryOrder(Long shopId, Long skuId,
                                             Integer skuCount, UUID vehicleInfoUid,
                                             DateTime appointmentDate, Customer customer,
                                             UUID uuid, int saleCategory, Payment payment) {
        SkuItem skuItem = skuItemRepository.findOne(skuId);
        if (skuItem == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity SkuItem is not found");
        }
        PresaleOrder presaleOrder = new PresaleOrder();
        presaleOrder.fillSkuItemPresale(appointmentDate, customer, getVehicleInfo(vehicleInfoUid),
                getShop(shopId), skuItem, skuCount, uuid, saleCategory, payment);
        presaleOrder = presaleOrderRepository.save(presaleOrder);
		return presaleOrder;
	}

    public PresaleOrder createCareOrder(long shopId, List<Long> careSuiteGroupItemIds,
                                        long careSuiteId, UUID vehicleInfoUid, DateTime appointmentDate,
                                        Customer customer, UUID uuid, Payment payment) {
        List<CareSuiteGroupItem> careSuiteGroupItems =
                careSuiteGroupItemRepository.findByIdIn(careSuiteGroupItemIds);
        CareSuite careSuite = careSuiteRepository.findOne(careSuiteId);
        PresaleOrder presaleOrder = new PresaleOrder();
        presaleOrder.fillCareSuitePresale(appointmentDate, customer, getVehicleInfo(vehicleInfoUid),
                getShop(shopId), careSuiteGroupItems, careSuite, uuid, payment);
        presaleOrder = presaleOrderRepository.save(presaleOrder);
		return presaleOrder;
	}

    public VehicleInfo getVehicleInfo(UUID vehicleInfoUid) {
        VehicleInfo vehicleInfo = vehicleInfoRepository.findByUid(vehicleInfoUid);
        if (vehicleInfo == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity VehicleInfo is not found");
        }
        return vehicleInfo;
    }

    public Shop getShop(Long shopId) {
        Shop shop = shopRepository.findOne(shopId);
        if (shop == null) {
            throw new BizException(ErrorCode.ENTITY_NOT_FOUND, "Entity Shop is not found");
        }
        return shop;
    }

    public void bindShopIfNeeded(Customer customer, Shop shop) {
        CustomerAppProfile profile = customerProfileRepository.findAppProfileByCustomer(customer);

        if (profile.bindingShop == null) {
            profile.bindingShop = shop;
        }
        customerProfileRepository.save(profile);
    }
}