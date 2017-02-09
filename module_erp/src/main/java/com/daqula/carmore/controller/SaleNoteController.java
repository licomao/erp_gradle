package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.CarmoreProperties;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.admin.OperationItem;
import com.daqula.carmore.model.customer.*;
import com.daqula.carmore.model.order.*;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.SettleOrderSpecifications;
import com.daqula.carmore.util.BeanUtil;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.OrderUtil;
import com.daqula.carmore.util.SessionUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

@Controller
public class SaleNoteController {

    private static final Logger log = LoggerFactory.getLogger(SaleNoteController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private VehicleInfoRepository vehicleInfoRepository;

    @Autowired
    private CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    @Autowired
    private CustomerPurchasedSuiteItemRepository customerPurchasedSuiteItemRepository;

    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    @Autowired
    private OperationItemRepository operationItemRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private BaseSetRepository baseSetRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OperationItemDetailRepository operationItemDetailRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffAttendanceRepository staffAttendanceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private StockingOrderRepository stockingOrderRepository;

    @Autowired
    private SettleOrderHistoryRepository settleOrderHistoryRepository;

    @Autowired
    CarmoreProperties carmoreProperties;

    @Autowired
    CampaignRepository campaignRepository;

    /**
     * 获取销售结单信息
     * @return
     */
    @RequestMapping("/salenote/searchcustominfo")
    public ModelAndView SearchCustomInfo(@AuthenticationPrincipal ERPUser user, String plateNumber) {
        ModelAndView mav = new ModelAndView("/salenote/searchcustominfo");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_SEARCHCUSTOMINFO)) {
            mav.setViewName("/noauthority");
        }

        if (plateNumber == null) {
            plateNumber = "";
        }
        mav.addObject("preSaleId", "");
        mav.addObject("plateNumber", plateNumber);

        return mav;
    }

    /**
     * 获取预约开单信息
     * @param plateNumber 车牌号
     * @return
     */
    @RequestMapping("/salenote/presale/searchpresaleinfo")
    @Transactional
    public ModelAndView toSavePreSale(String plateNumber, String id, @AuthenticationPrincipal ERPUser user) {

        ModelAndView mav = new ModelAndView("/salenote/searchcustominfo");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_SEARCHCUSTOMINFO)) {
            mav.setViewName("noauthority");
        }

        // 将APP存的顾客信息 保存至ERP端
//        PresaleOrder presaleOrder = presaleOrderRepository.findOne(Long.parseLong(id));
//        CustomerERPProfile customerERPProfile = customerProfileRepository.findERPProfileByCustomer(presaleOrder.customer, user.organization);

        List<VehicleInfo> vehicleInfos = vehicleInfoRepository.findByPlateNumberInCustomerERPProfile(plateNumber, user.organization);
        VehicleInfo vehicleInfoErp = vehicleInfos.get(0);

        VehicleInfo vehicleInfoApp = vehicleInfoRepository.findByPlateNumberInCustomerAppProfile(plateNumber);

        CustomerERPProfile customerERPProfile = customerProfileRepository.findERPProfileByPlateNumberAndOrganizationId(vehicleInfoErp, user.organization.id);

        if (customerERPProfile == null) {
            CustomerAppProfile customerAppProfile = customerProfileRepository.findAppProfileByPlateNumberAndOrganizationId(vehicleInfoApp, user.organization.id);

            CustomerERPProfile customerERPProfileFind = customerProfileRepository.findERPProfileByCustomer(customerAppProfile.customer, user.organization);

//            CustomerAppProfile customerAppProfile = customerProfileRepository.findAppProfileByCustomer(presaleOrder.customer, user.organization);

            if (customerERPProfileFind != null){
                for (VehicleInfo vehicle : customerAppProfile.vehicles) {
                    if (vehicle.plateNumber.equals(plateNumber)) {
                        if ( vehicle.plateNumber.equals(plateNumber)) {
                            if (customerERPProfileFind.vehicles == null ){
                                customerERPProfileFind.vehicles = new ArrayList<>();
                            }
                            VehicleInfo newVehicleInfo = new VehicleInfo();
                            BeanUtil.copyFields(vehicle, newVehicleInfo);
                            newVehicleInfo.id=0;
                            customerERPProfileFind.vehicles.add(newVehicleInfo);
                        }
                    }
                }
                customerProfileRepository.save(customerERPProfileFind);
            } else {
                customerERPProfile = new CustomerERPProfile();
                customerERPProfile.realName = customerAppProfile.nickName;
                customerERPProfile.gender = customerAppProfile.gender;
                customerERPProfile.organization = customerAppProfile.bindingShop.organization;
                customerERPProfile.customer = customerAppProfile.customer;
                customerERPProfile.vehicles = new ArrayList<>();
                for (VehicleInfo vehicle : customerAppProfile.vehicles) {
                    if ( vehicle.plateNumber.equals(plateNumber)) {
                        VehicleInfo newVehicleInfo = new VehicleInfo();
                        BeanUtil.copyFields(vehicle, newVehicleInfo);
                        newVehicleInfo.id=0;
                        customerERPProfile.vehicles.add(newVehicleInfo);
                    }
                }
                customerProfileRepository.save(customerERPProfile);
            }
        }

        mav.addObject("preSaleId", id);
        mav.addObject("plateNumber", plateNumber);

        return mav;
    }

    /**
     * 获取销售未结单信息
     * @return
     */
    @RequestMapping("/salenote/searchsettleinfo")
    public ModelAndView SearchSettleInfo(@AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/salenote/searchsettleinfo");
        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_SEARCHSETTLEINFO)) {
            mav.setViewName("/noauthority");
        }
        mav.addObject("shops",shops);
        return mav;
    }


    /**
     * 工时填写页面
     * @return
     */
    @RequestMapping("/salenote/workhours")
    @Transactional(readOnly = true)
    public ModelAndView toSaveHours(@ModelAttribute SettleOrder settleOrder) {
        ModelAndView mav = new ModelAndView("/salenote/workhours");

        mav.addObject("settleOrder",settleOrder);
        return mav;
    }

    /**
     * 生成会员卡销售开单
     * @param plateNumber
     * @param customsuiteid
     * @return
     */
    @RequestMapping("/salenote/customcard/tosave")
    @Transactional(readOnly = true)
    public ModelAndView toSaveCustomCard(String plateNumber,String customsuiteid) {
        ModelAndView mav = new ModelAndView("/salenote/customcardsalenote");
        Organization organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
        List<VehicleInfo> vehicleInfos = vehicleInfoRepository.findByPlateNumberInCustomerERPProfile(plateNumber, organization);
        VehicleInfo vehicleInfo = vehicleInfos.get(0);
        CustomerERPProfile customerERPProfile = customerProfileRepository.findByVehicles(vehicleInfo);
        SettleOrder settleOrder = new SettleOrder();

        settleOrder.vehicleInfo = vehicleInfo;

        //销售单号

        Shop shop = (Shop)request.getSession().getAttribute("SHOP");
        Long newOrderNum = settleOrderRepository.findMaxOrderNum(shop);
        settleOrder.saleNoView = OrderUtil.getViewOrderNumber(organization, shop, OrderUtil.ORDER_TYPE_SALE, newOrderNum);
        settleOrder.saleNo = newOrderNum;
        System.out.println(customsuiteid);
        mav.addObject("customSuiteId", Long.parseLong(customsuiteid));
        CustomerPurchasedSuite customerPurchasedSuite = customerPurchasedSuiteRepository.findOne(Long.parseLong(customsuiteid));
        mav.addObject("customerPurchasedSuite",customerPurchasedSuite);

        mav.addObject("settleOrder", settleOrder);
        mav.addObject("customerERPProfile", customerERPProfile);

        //基础设置
        organization = organizationRepository.findOne(organization.id);
        BaseSet baseSet = baseSetRepository.findByShop(shop);
        if(baseSet == null) {
            baseSet = new BaseSet();
            baseSet.operationPrice = 0.0;
            baseSet.posRate = "0";
            baseSet.posTopRate = "0";
        }
        mav.addObject("baseSet", baseSet);

        //汽车排量
        mav.addObject("engineDisplacement",settleOrder.vehicleInfo.engineDisplacement);

        //今天已考勤员工
        Date today = new Date((new SimpleDateFormat("yyyy/MM/dd")).format(new Date()));
        List<Staff> staffs = staffAttendanceRepository.findByWorkDateAndShop(today, shop);
        mav.addObject("staffList", staffs);

        //组织及门店信息
        mav.addObject("organizationInfo",organization.name + "(" + shop.name + ")");
        mav.addObject("shopInfo",shop);

        return mav;
    }

    /**
     * 获取未结算会员卡销售单
     * @param saleNoView 显示用销售单号
     * @param user 登陆者
     * @return
     */
    @RequestMapping("/salenote/shownotsettlecustomcard")
    @Transactional
    public ModelAndView showNotSettleCustomCard(@RequestParam String saleNoView,@AuthenticationPrincipal ERPUser user) {
        SettleOrder settleOrder = settleOrderRepository.findBySaleNoView(saleNoView).get(0);

        ModelAndView mav = new ModelAndView("/salenote/customcardsalenote");
        mav.addObject("settleOrder", settleOrder);
        CustomerERPProfile customerERPProfile = customerProfileRepository.findByVehicles(vehicleInfoRepository.findOne(settleOrder.vehicleInfo.id));
        mav.addObject("customerERPProfile", customerERPProfile);

        mav.addObject("customSuiteId", settleOrder.customerPurchasedSuite.id);
        mav.addObject("customerPurchasedSuite",settleOrder.customerPurchasedSuite);

        //今天已考勤员工
        Shop shop = (Shop)request.getSession().getAttribute("SHOP");
        Date today = new Date((new SimpleDateFormat("yyyy/MM/dd")).format(new Date()));
        List<Staff> staffs = staffAttendanceRepository.findByWorkDateAndShop(today, shop);
        mav.addObject("staffList", staffs);

        //基础设置
        Organization organization = organizationRepository.findOne(user.organization.id);
        BaseSet baseSet = baseSetRepository.findByShop(shop);
        if(baseSet == null) {
            baseSet = new BaseSet();
            baseSet.operationPrice = 0.0;
            baseSet.posRate = "0";
            baseSet.posTopRate = "0";
        }
        mav.addObject("baseSet", baseSet);

        //汽车排量
        mav.addObject("engineDisplacement",settleOrder.vehicleInfo.engineDisplacement);

        return mav;
    }

    /**
     * 生成普通销售开单
     * @param plateNumber 车牌号
     * @return
     */
    @RequestMapping("/salenote/salenote")
    @Transactional
    public ModelAndView toSave(@RequestParam(required = false) String plateNumber) {
        ModelAndView mav = new ModelAndView("/salenote/salenote");
        Organization organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");

        List<VehicleInfo> vehicleInfos = vehicleInfoRepository.findByPlateNumberInCustomerERPProfile(plateNumber, organization);
        VehicleInfo vehicleInfo = new  VehicleInfo();
        if (vehicleInfos.size() > 0) {
            vehicleInfo = vehicleInfos.get(0);
        }
        CustomerERPProfile customerERPProfile = customerProfileRepository.findByVehicles(vehicleInfo);
        SettleOrder settleOrder = new SettleOrder();

        settleOrder.vehicleInfo = vehicleInfo;

        //销售单号
        Shop shop = (Shop)request.getSession().getAttribute("SHOP");
        if(shop == null){
            return new ModelAndView("/login");
        }
        Long newOrderNum = settleOrderRepository.findMaxOrderNum(shop);
        settleOrder.saleNoView = OrderUtil.getViewOrderNumber(organization, shop, OrderUtil.ORDER_TYPE_SALE, newOrderNum);
        settleOrder.saleNo = newOrderNum;

        if (settleOrder.orderDetails == null)
            settleOrder.orderDetails = new ArrayList<>();

        mav.addObject("settleOrder", settleOrder);
        mav.addObject("customerERPProfile", customerERPProfile);

        //今天已考勤员工
        Date today = new Date((new SimpleDateFormat("yyyy/MM/dd")).format(new Date()));
        List<Staff> staffs = staffAttendanceRepository.findByWorkDateAndShop(today, shop);
        mav.addObject("staffList", staffs);

        //基础设置
        organization = organizationRepository.findOne(organization.id);
        BaseSet baseSet = baseSetRepository.findByShop(shop);
        if(baseSet == null) {
            baseSet = new BaseSet();
            baseSet.operationPrice = 0.0;
            baseSet.posRate = "0";
            baseSet.posTopRate = "0";
        }
        mav.addObject("baseSet", baseSet);

        //汽车排量
        mav.addObject("engineDisplacement",settleOrder.vehicleInfo.engineDisplacement);


        //组织及门店信息
        mav.addObject("organizationInfo",organization.name + "(" + shop.name + ")");
        mav.addObject("shopInfo",shop);
//        organization.name
//        shop.p
        return mav;
    }

    /**
     * 获取未结算销售单
     * @param saleNoView 显示用销售单号
     * @return
     */
    @RequestMapping("/salenote/shownotsettleinfo")
    @Transactional(readOnly = true)
    public ModelAndView showNotSettleInfo(@RequestParam String saleNoView,@AuthenticationPrincipal ERPUser user) {
        SettleOrder settleOrder = settleOrderRepository.findBySaleNoView(saleNoView).get(0);

        if (settleOrder.customerPurchasedSuite != null) {
            return new ModelAndView("redirect:/salenote/shownotsettlecustomcard?saleNoView="+saleNoView);
        }
        ModelAndView mav = new ModelAndView("/salenote/salenote");
        mav.addObject("settleOrder", settleOrder);
        CustomerERPProfile customerERPProfile = customerProfileRepository.findByVehicles(vehicleInfoRepository.findOne(settleOrder.vehicleInfo.id));
        mav.addObject("customerERPProfile", customerERPProfile);

        //今天已考勤员工
        Shop shop = (Shop)request.getSession().getAttribute("SHOP");
        Date today = new Date((new SimpleDateFormat("yyyy/MM/dd")).format(new Date()));
        List<Staff> staffs = staffAttendanceRepository.findByWorkDateAndShop(today, shop);
        mav.addObject("staffList", staffs);

        //基础设置
        Organization organization = organizationRepository.findOne(user.organization.id);
        BaseSet baseSet = baseSetRepository.findByShop(shop);

        if(baseSet == null) {
            baseSet = new BaseSet();
            baseSet.operationPrice = 0.0;
            baseSet.posRate = "0";
            baseSet.posTopRate = "0";
        }
        mav.addObject("baseSet", baseSet);

        //汽车排量
        mav.addObject("engineDisplacement",settleOrder.vehicleInfo.engineDisplacement);


        return mav;
    }

    /**
     * 显示会员销售订单信息
     * @return
     */
    @RequestMapping("/salenote/showcardsuite")
    @ResponseBody
    public Map<String, Object> showsaleNoteInfo(String customSuiteId,@AuthenticationPrincipal ERPUser user) {

        //销售内容
        CustomerPurchasedSuite customerPurchasedSuite = customerPurchasedSuiteRepository.findOne(Long.parseLong(customSuiteId));
        List<SettleOrder> settleOrders = settleOrderRepository.findSuiteList(customerPurchasedSuite);
        List<CustomerPurchasedSuiteItem> purchasedSuiteItems  = customerPurchasedSuiteItemRepository.findByPurchasedSuite(customerPurchasedSuite);
        for (SettleOrder so : settleOrders){
            for (OrderDetail od : so.orderDetails){
                for(CustomerPurchasedSuiteItem cs :purchasedSuiteItems ) {
                    if (cs.customStockItem != null){
                        if (cs.customStockItem.id == 1){
                            cs.customStockItem =  cs.suiteItem.skuItem;
                            cs.cost = cs.suiteItem.cost;
                        }
                    }
                    if (od.orderedItem.id == cs.customStockItem.id){
                        cs.usedTimes = cs.usedTimes + od.count;
                        break;
                    }
                }
            }
        }
        Date orderDate = stockingOrderRepository.findLastStockingDate(customerPurchasedSuite.shop);
        CustomStockItem customStockItem;
        for (CustomerPurchasedSuiteItem ps : purchasedSuiteItems){
            customStockItem = new CustomStockItem();
            customStockItem.name = ps.customStockItem.name;
            customStockItem.cost = ps.cost;
            if (ps.customStockItem.rootCategory == 17){
                ps.stockItemNumber = 999;
            } else {
                ps.stockItemNumber  = customStockItemRepository.getStockNumber(customerPurchasedSuite.shop, user.organization, customStockItem, orderDate);
            }
        }
        return map(entry("rows", purchasedSuiteItems));
    }


    /**
     * 根据车牌号获取所有的会员卡信息
     * @param plateNumber 车牌号
     * @return
     */
    @RequestMapping("/salenote/customersuite/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                          @RequestParam String sord, @RequestParam String sidx,
                                                          @RequestParam String plateNumber,@AuthenticationPrincipal ERPUser user) {
        Organization organization = new Organization();
        organization.id = user.organization.id;
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        if(plateNumber == null) {
            return null;
        }
        plateNumber = plateNumber.trim();
//        Page<CustomerPurchasedSuite> customerPurchasedSuitePage = customerPurchasedSuiteRepository.findByVehiclePlateNumber(plateNumber, pageRequest);
        Page<CustomerPurchasedSuite> customerPurchasedSuitePage = customerPurchasedSuiteRepository.findByVehiclePlateNumberAndOrganization(plateNumber, organization, pageRequest);

        return JqGridDataGenerator.getDataJson(customerPurchasedSuitePage);
    }

    @RequestMapping("/salenote/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public CustomerERPProfile listData(@RequestParam String plateNumber, @AuthenticationPrincipal ERPUser user) {
        CustomerERPProfile customerERPProfile = null;
        List<VehicleInfo> vehicleInfos = vehicleInfoRepository.findByPlateNumberInCustomerERPProfile(plateNumber, user.organization);

        VehicleInfo vehicleInfo = null;
        if (vehicleInfos.size()>0){
            vehicleInfo = vehicleInfos.get(0);
        }
        if(vehicleInfo != null) {
            customerERPProfile = customerProfileRepository.findByVehicles(vehicleInfo);
        }
        return customerERPProfile;
    }

    /**
     * 获取库存信息
     * @param customStockItem 库存信息
     * @return
     */
    @RequestMapping(value = "/salenote/item/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> showShop(@RequestParam int page, @RequestParam int rows,
                                          @RequestParam String sord, @RequestParam String sidx,
                                        CustomStockItem customStockItem, @AuthenticationPrincipal ERPUser user) {

        Shop shop = new Shop();
        shop.id = ((Shop)request.getSession().getAttribute("SHOP")).id;
        Date orderDate = stockingOrderRepository.findLastStockingDate(shop);
        int totalCount = customStockItemRepository.calCustomStockInfoCounts(shop, user.organization, customStockItem, orderDate);
        List<CustomStockItem> customStockItems = customStockItemRepository.calCustomStockInfo(page, rows, shop, user.organization, customStockItem, orderDate);
        return JqGridDataGenerator.getNativeDataJson(customStockItems, totalCount, rows, page);
    }

    /**
     * 获取作业项目
     * @param operationItem operationItem
     * @return
     */
    @RequestMapping("/salenote/project")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> showProject(@RequestParam int page, @RequestParam int rows,
                                           @RequestParam String sord, @RequestParam String sidx,
                                           OperationItem operationItem, String engineDisplacement) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Page<OperationItem> operationItemPage = null;

        String name = operationItem.name;
        if(StringUtils.isEmpty(name)) {
            name = "%%";
        } else {
            name = "%" + name + "%";
        }

        if (operationItem.operationType == 0) {
            operationItemPage = operationItemRepository.findByNameLikeAndCarLevel(name, engineDisplacement, pageRequest);
        } else {
            operationItemPage = operationItemRepository.findByNameLikeAndCarLevelAndOperationType(name, engineDisplacement, operationItem.operationType, pageRequest);
        }

        return JqGridDataGenerator.getDataJson(operationItemPage);
    }

    /**
     * 打印结算订单
     * @param saleNoView 结算订单号
     * @return
     */
    @RequestMapping(value="/salenote/salenoteprint")
    @ResponseBody
    @Transactional
    public ModelAndView saleNotePrint(String saleNoView) {
        ModelAndView mav = new ModelAndView("/salenote/salenoteprint");
        double operationPrice = 0;
        SettleOrder settleOrder = settleOrderRepository.findBySaleNoView(saleNoView).get(0);
        CustomerERPProfile customerERPProfile = customerProfileRepository.findByVehicles(settleOrder.vehicleInfo);

        mav.addObject("settleOrder", settleOrder);
        mav.addObject("customerERPProfile", customerERPProfile);

//        organization = organizationRepository.findOne(organization.id);
        Organization organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
        Shop shop = (Shop)request.getSession().getAttribute("SHOP");
        organization = organizationRepository.findOne(organization.id);
        BaseSet baseSet = baseSetRepository.findByShop(settleOrder.shop);

        if (baseSet != null) {
            operationPrice = baseSet.operationPrice;
        } else {
            baseSet = new BaseSet();
            baseSet.operationPrice = 0.0;
            baseSet.posRate = "0";
            baseSet.posTopRate = "0";
        }
        mav.addObject("baseSet",baseSet);
        mav.addObject("opreationPrice",operationPrice);
//        shop.p
        //组织及门店信息
        mav.addObject("organizationInfo",organization.name + "(" + shop.name + ")");
        mav.addObject("shopInfo",shop);

        return mav;
    }

    /**
     * 打印会员卡结算订单
     * @param saleNoView 结算订单号
     * @return
     */
    @RequestMapping(value="/salenote/customcardprint")
    @ResponseBody
    @Transactional
    public ModelAndView customerCardPrint(String saleNoView) {
        ModelAndView mav = new ModelAndView("/salenote/customcardprint");

        SettleOrder settleOrder = settleOrderRepository.findBySaleNoView(saleNoView).get(0);
        CustomerERPProfile customerERPProfile = customerProfileRepository.findByVehicles(settleOrder.vehicleInfo);

//        organization = organizationRepository.findOne(organization.id);
        BaseSet baseSet = baseSetRepository.findByShop(settleOrder.shop);
        if(baseSet == null) {
            baseSet = new BaseSet();
            baseSet.operationPrice = 0.0;
            baseSet.posRate = "0";
            baseSet.posTopRate = "0";
        }
        mav.addObject("baseSet", baseSet);

        mav.addObject("settleOrder", settleOrder);
        mav.addObject("customerERPProfile", customerERPProfile);

        return mav;
    }

    /**
     * 作废销售单
     * @param saleNoView 结算订单号
     * @return
     */
    @RequestMapping(value="/salenote/delete")
    @Transactional
    public ModelAndView delete(String saleNoView) {
        ModelAndView mav = new ModelAndView("/message");

        SettleOrder settleOrder = settleOrderRepository.findBySaleNoView(saleNoView).get(0);
        settleOrder.deleted = !settleOrder.deleted;
        settleOrderRepository.save(settleOrder);

        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/salenote/searchsettleinfo");
        return mav;
    }

    /**
     * 作废销售单
     * @param saleNoView 结算订单号
     * @return
     */
    @RequestMapping(value="/salenote/salenoteView")
    @Transactional(readOnly = true)
    public ModelAndView salenoteView(String saleNoView) {
        ModelAndView mav = new ModelAndView("/salenote/salenoteView");

        double operationPrice = 0;
        SettleOrder settleOrder = null;
        List<SettleOrder> settleOrderList =  settleOrderRepository.findBySaleNoView(saleNoView);
        if (settleOrderList.size() > 0){
            settleOrder = settleOrderList.get(0);
        } else {
            mav = new ModelAndView("/message");
            mav.addObject("message", "销售单号有误，未查到相关信息！");
            mav.addObject("responsePage", "../purchaseorder/search/list");
            return mav;
        }

        CustomerERPProfile customerERPProfile = customerProfileRepository.findByVehicles(settleOrder.vehicleInfo);

        mav.addObject("settleOrder", settleOrder);
        mav.addObject("customerERPProfile", customerERPProfile);

        Organization organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
        organization = organizationRepository.findOne(organization.id);
        BaseSet baseSet = baseSetRepository.findByShop(settleOrder.shop);
        if(baseSet == null) {
            baseSet = new BaseSet();
            baseSet.operationPrice = 0.0;
            baseSet.posRate = "0";
            baseSet.posTopRate = "0";
        }
        mav.addObject("baseSet", baseSet);

        if(baseSet != null) {
            operationPrice = baseSet.operationPrice;
        }
        mav.addObject("opreationPrice",operationPrice);

        return mav;
    }

    /**
     * 保存结算订单
     * @param nextOrSettle 保存 or 直接结算
     * @param salenotedata orderdetails字符串
     * @param receiver 接车人员
     * @param customerERPProfile custom信息
     * @param settleOrder 结算订单
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/salenote/save", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ModelAndView saveSettleOrder(String nextOrSettle, String salenotedata,String workhoursdata, Long receiver,
                                        @ModelAttribute CustomerERPProfile customerERPProfile,
                                        @Valid @ModelAttribute SettleOrder settleOrder, BindingResult bindingResult) {
        ModelAndView mav;
        String saleNoView = "";

        if (bindingResult.hasErrors()) {
            return new ModelAndView("/salenote/salenote");
        }

        //详细订单
        List<OrderDetail> orderDetails = new ArrayList<>();
        if(!StringUtils.isEmpty(salenotedata)) {
            String[] rows = salenotedata.split(";");
            for (int i = 0; i < rows.length; i++) {
                String[] row = rows[i].split(",");
                OrderDetail orderDetail = new OrderDetail();
                //商品
                CustomStockItem customStockItem = new CustomStockItem();
                customStockItem.id = Long.parseLong(row[0]);
                orderDetail.orderedItem = customStockItem;
                //数量
                orderDetail.count = (int) (Double.parseDouble(row[2]));
                //折扣率
                if (row[3].equals("")) {
                    orderDetail.discount = Double.parseDouble("0");
                } else {
                    orderDetail.discount = Double.parseDouble(row[3]);
                }
                if (!row[4].equals("")) {
                    orderDetail.discountPrice = Double.parseDouble(row[4]);
                }
                //实际应收金额
                orderDetail.receivable = Double.parseDouble(row[5]);
                //施工人员
                Long id = Long.parseLong(row[6]);
                orderDetail.merchandier = staffRepository.findOne(id);
                //成本价格
                orderDetail.cost = Double.parseDouble(row[7]);
                if (row.length > 8) {//存在审核人员
                    orderDetail.discountGranter = erpUserRepository.findOne(Long.parseLong(row[8]));
                }
                orderDetails.add(orderDetail);
            }
        }

        //商品明细
        List<OperationItemDetail> operationItemDetails = new ArrayList<>();
        if(!StringUtils.isEmpty(workhoursdata)) {
            String[] worksRows = workhoursdata.split(";");
            for (int i = 0; i < worksRows.length; i++) {
                String[] row = worksRows[i].split(",");

                OperationItemDetail operationitemdetail = new OperationItemDetail();
                operationitemdetail.operationItem = operationItemRepository.findOne(Long.parseLong(row[0]));
                operationitemdetail.sum = Double.parseDouble(row[1]);

                operationItemDetails.add(operationitemdetail);
            }
        }

        if( settleOrder.id == 0 ) {//新建
            //详细订单
            settleOrder.orderDetails = new ArrayList<>();
            settleOrder.orderDetails.addAll(orderDetails);
            Organization organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
            Shop shop = (Shop)request.getSession().getAttribute("SHOP");
            Long newOrderNum = settleOrderRepository.findMaxOrderNum(shop);
            settleOrder.saleNoView = OrderUtil.getViewOrderNumber(organization, shop, OrderUtil.ORDER_TYPE_SALE, newOrderNum);
            settleOrder.saleNo = newOrderNum;
            settleOrder.shop = shopRepository.findOne(shop.id);

            //顾客信息
//            Customer customer = customerRepository.findByMobileAndOrganization(customerERPProfile.customer.mobile, customerERPProfile.organization);
            List<Customer> customers = customerRepository.findByMobile(customerERPProfile.customer.mobile);
//            Customer customer = customerRepository.findByMobile(customerERPProfile.customer.mobile);
            settleOrder.customer = customers.get(0);
            settleOrder.payment.customer = settleOrder.customer;

            //工时
            settleOrder.operationItemDetails = new ArrayList<>();
            settleOrder.operationItemDetails.addAll(operationItemDetails);

            //接车人员
            Staff staff = staffRepository.findOne(receiver);
            settleOrder.receiver = staff;

            if(nextOrSettle.equals("0")) {//结算
                settleOrder.isFinish = true;
                settleOrder.finishDate = DateTime.now();
            }
            saleNoView = settleOrderRepository.save(settleOrder).saleNoView;
        }else {//修改
            SettleOrder settleOrderUpdate = settleOrderRepository.findOne(settleOrder.id);
            //详细订单
            settleOrderUpdate.orderDetails.clear();
            settleOrderUpdate.orderDetails.addAll(orderDetails);

            Shop shop = (Shop)request.getSession().getAttribute("SHOP");
            settleOrderUpdate.shop = shopRepository.findOne(shop.id);

            //顾客信息
//            Customer customer = customerRepository.findByMobileAndOrganization(customerERPProfile.customer.mobile, customerERPProfile.organization);
            List<Customer> customers = customerRepository.findByMobile(customerERPProfile.customer.mobile);//那就改成LIST 后面拿get0
            settleOrderUpdate.customer = customers.get(0);
            settleOrderUpdate.payment.customer = settleOrderUpdate.customer;

            //收款机
            settleOrderUpdate.payment.cashAmount = settleOrder.payment.cashAmount;
            settleOrderUpdate.payment.posAmount = settleOrder.payment.posAmount;
            settleOrderUpdate.payment.appAmount = settleOrder.payment.appAmount;
            settleOrderUpdate.payment.otherAmount = settleOrder.payment.otherAmount;
            settleOrderUpdate.remark = settleOrder.remark;
            //工时
            settleOrderUpdate.operationItemDetails.clear();
            settleOrderUpdate.operationItemDetails.addAll(operationItemDetails);

            //接车人员
            Staff staff = staffRepository.findOne(receiver);
            settleOrderUpdate.receiver = staff;

            if(nextOrSettle.equals("0")) {//结算
                settleOrderUpdate.isFinish = true;
                settleOrderUpdate.finishDate = DateTime.now();
            }

            saleNoView = settleOrderUpdate.saleNoView;
        }
        VehicleInfo vehicleInfo = vehicleInfoRepository.findOne(settleOrder.vehicleInfo.id);
        vehicleInfo.lastMaintenanceMileage = settleOrder.vehicleInfo.lastMaintenanceMileage;
        vehicleInfoRepository.save(vehicleInfo);

        if(nextOrSettle.equals("0")) {//结算
            mav = new ModelAndView("redirect:/salenote/salenoteprint?saleNoView=" + saleNoView );

        } else {//保存
            mav = new ModelAndView("redirect:/salenote/shownotsettleinfo?saleNoView=" + saleNoView );
        }

        return mav;
    }


    /**
     * 保存会员卡订单
     * @param salenotedata orderdetails字符串
     * @param receiver 接车人员
     * @param customerERPProfile 顾客信息
     * @param settleOrder 订单内容
     * @return
     */
    @RequestMapping(value = "/salenote/customcard/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView saveCustomCardOrder(String nextOrSettle, String salenotedata, String workhoursdata ,Long receiver,String customSuiteId,
                                        @ModelAttribute CustomerERPProfile customerERPProfile,
                                        @ModelAttribute SettleOrder settleOrder) {
        ModelAndView mav;
        String saleNoView = "";

        CustomerPurchasedSuite customerPurchasedSuite = customerPurchasedSuiteRepository.findOne(Long.parseLong(customSuiteId));

        //详细订单
        List<OrderDetail> orderDetails = new ArrayList<>();
        if(!StringUtils.isEmpty(salenotedata)) {
            String[] rows = salenotedata.split(";");
            for (int i = 0; i < rows.length; i++) {
                String[] row = rows[i].split(",");

                OrderDetail orderDetail = new OrderDetail();

                //商品
                CustomStockItem customStockItem = new CustomStockItem();
                customStockItem.id = Long.parseLong(row[0]);
                orderDetail.orderedItem = customStockItem;
                //数量
                orderDetail.count = Integer.parseInt(row[1]);
                //施工人员
                Long id = Long.parseLong(row[2]);
                orderDetail.merchandier = staffRepository.findOne(id);
                //成本价格
                orderDetail.cost = Double.parseDouble(row[3]);

                orderDetails.add(orderDetail);

                if(nextOrSettle.equals("0")) {//结算
                    //更新会员卡套餐剩余数量
//                    for (int j = 0; j < customerPurchasedSuite.purchasedSuiteItems.size(); j++) {
//                        if (customerPurchasedSuite.purchasedSuiteItems.get(j).id == Long.parseLong(row[4])) {
//                            customerPurchasedSuite.purchasedSuiteItems.get(j).usedTimes += Integer.parseInt(row[1]);
//                            break;
//                        }
//                    }
                }
            }
        }

        //商品明细
        List<OperationItemDetail> operationItemDetails = new ArrayList<>();
        if(!StringUtils.isEmpty(workhoursdata)) {
            String[] worksRows = workhoursdata.split(";");
            for (int i = 0; i < worksRows.length; i++) {
                String[] row = worksRows[i].split(",");

                OperationItemDetail operationitemdetail = new OperationItemDetail();
                operationitemdetail.operationItem = operationItemRepository.findOne(Long.parseLong(row[0]));
                operationitemdetail.sum = Double.parseDouble(row[1]);

                operationItemDetails.add(operationitemdetail);
            }
        }

        if( settleOrder.id == 0 ) {//新建
            //详细订单
            settleOrder.orderDetails = new ArrayList<>();
            settleOrder.orderDetails.addAll(orderDetails);
            Organization organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
            Shop shop = (Shop)request.getSession().getAttribute("SHOP");
            shop = shopRepository.findOne(shop.id);
            Long newOrderNum = settleOrderRepository.findMaxOrderNum(shop);
            settleOrder.saleNoView = OrderUtil.getViewOrderNumber(organization, shop, OrderUtil.ORDER_TYPE_SALE, newOrderNum);
            settleOrder.saleNo = newOrderNum;
            settleOrder.shop = shop;

            //顾客信息
            List<Customer> customers = customerRepository.findByMobile(customerERPProfile.customer.mobile);
            settleOrder.customer = customers.get(0);
            settleOrder.payment.customer = settleOrder.customer;

            //工时
            settleOrder.operationItemDetails = new ArrayList<>();
            settleOrder.operationItemDetails.addAll(operationItemDetails);

            //接车人员
            Staff staff = staffRepository.findOne(receiver);
            settleOrder.receiver = staff;

            //会员卡信息
            settleOrder.customerPurchasedSuite = customerPurchasedSuite;

            SettleOrder savedSettleOrder = settleOrderRepository.save(settleOrder);
            if(nextOrSettle.equals("0")) {//结算

                //结算flag
                settleOrder.isFinish = true;
                settleOrder.finishDate = DateTime.now();

                //异地消费
                Shop belongShop = customerPurchasedSuite.shop;
                if(shop!=null && belongShop!=null && shop.id != belongShop.id){
                    SettleOrderHistory settleOrderHistory = new SettleOrderHistory();
                    settleOrderHistory.belongShop = belongShop;
                    settleOrderHistory.shop = shop;
                    settleOrderHistory.settleOrder = savedSettleOrder;
                    settleOrderHistoryRepository.save(settleOrderHistory);
                }
            }
            saleNoView = savedSettleOrder.saleNoView;

        }else {//修改
            SettleOrder settleOrderUpdate = settleOrderRepository.findOne(settleOrder.id);
            //详细订单
            settleOrderUpdate.orderDetails.clear();
            settleOrderUpdate.orderDetails.addAll(orderDetails);

            Shop shop = (Shop)request.getSession().getAttribute("SHOP");
            shop = shopRepository.findOne(shop.id);
            settleOrderUpdate.shop = shop;

            //顾客信息
            List<Customer> customers = customerRepository.findByMobile(customerERPProfile.customer.mobile);
            settleOrderUpdate.customer = customers.get(0);
            settleOrderUpdate.payment.customer = settleOrderUpdate.customer;

            //工时
            settleOrderUpdate.operationItemDetails.clear();
            settleOrderUpdate.operationItemDetails.addAll(operationItemDetails);
            settleOrderUpdate.remark = settleOrder.remark;

            //接车人员
            Staff staff = staffRepository.findOne(receiver);
            settleOrderUpdate.receiver = staff;

            //会员卡信息
            settleOrderUpdate.customerPurchasedSuite = customerPurchasedSuite;

            if(nextOrSettle.equals("0")) {//结算
                //结算flag
                settleOrderUpdate.isFinish = true;
                settleOrderUpdate.finishDate = DateTime.now();
                //异地消费
                Shop belongShop = customerPurchasedSuite.shop;
                if(shop!=null && belongShop!=null && shop.id != belongShop.id){
                    SettleOrderHistory settleOrderHistory = new SettleOrderHistory();
                    settleOrderHistory.belongShop = belongShop;
                    settleOrderHistory.shop = shop;
                    settleOrderHistory.settleOrder = settleOrderUpdate;
                    settleOrderHistoryRepository.save(settleOrderHistory);
                }
            }

            saleNoView = settleOrderUpdate.saleNoView;
        }
        VehicleInfo vehicleInfo = vehicleInfoRepository.findOne(settleOrder.vehicleInfo.id);
        vehicleInfo.lastMaintenanceMileage = settleOrder.vehicleInfo.lastMaintenanceMileage;
        vehicleInfoRepository.save(vehicleInfo);

        if(nextOrSettle.equals("0")) {//结算
            mav = new ModelAndView("redirect:/salenote/salenoteprint?saleNoView=" + saleNoView );
        } else {//保存
            mav = new ModelAndView("redirect:/salenote/shownotsettlecustomcard?saleNoView=" + saleNoView );
            System.out.println("*******************" + mav.getViewName());
        }


        Organization organization = (Organization)request.getSession().getAttribute("ORGANIZATIONS");
        Shop shop = (Shop)request.getSession().getAttribute("SHOP");
        //组织及门店信息
        mav.addObject("organizationInfo",organization.name + "(" + shop.name + ")");
        mav.addObject("shopInfo",shop);

        return mav;
    }

    /**
     * 保存工时
     * @param workhoursdata 工时明细
     * @param settleOrder 订单内容
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/salenote/workhours/save", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ModelAndView saveWorkHours(String workhoursdata,@ModelAttribute SettleOrder settleOrder, BindingResult bindingResult) {
        SettleOrder settleOrderUpdate = settleOrderRepository.findOne(settleOrder.id);

        settleOrderUpdate.payment.cashAmount += settleOrder.payment.cashAmount;
        settleOrderUpdate.payment.posAmount += settleOrder.payment.posAmount;
        settleOrderUpdate.payment.appAmount += settleOrder.payment.appAmount;

        List<OperationItemDetail> operationItemDetails = new ArrayList<>();
        String[] rows = workhoursdata.split(";");
        settleOrderUpdate.operationItemDetails.clear();
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i].split(",");

            OperationItemDetail operationitemdetail = new OperationItemDetail();
            operationitemdetail.operationItem = operationItemRepository.findOne(Long.parseLong(row[0]));
            operationitemdetail.sum = Double.parseDouble(row[1]);

            operationItemDetails.add(operationItemDetailRepository.save(operationitemdetail));
        }

        settleOrderUpdate.operationItemDetails.addAll(operationItemDetails);
        settleOrderUpdate.isFinish = true;
        SettleOrder settleOrderUpdated = settleOrderRepository.save(settleOrderUpdate);

        ModelAndView mav = new ModelAndView("redirect:/salenote/salenoteprint?saleNoView=" + settleOrderUpdated.saleNoView);
        return mav;
    }

    /**
     * 获取订单内容
     * @param saleNoView 销售单号
     * @param tel 客户手机
     * @param isDeleted 是否作废
     * @return
     */
    @RequestMapping(value = "/salenote/settleList/searchdata", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                      @RequestParam String sord, @RequestParam String sidx,
                                                      String saleNoView, String tel, Boolean isDeleted, String createdDate,String shopId, String plateNumber,String isFinished,@AuthenticationPrincipal ERPUser user) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Shop  shop = shopRepository.findOne(Long.parseLong(shopId));
        if (saleNoView == null) {
            saleNoView = "";
        }
        if (tel == null) {
            tel = "";
        }
        if (plateNumber == null) {
            plateNumber = "";
        }
        boolean isFone = false;
        boolean isFTwo = true;
        if (!StringUtils.isEmpty(isFinished)){
            isFone = Boolean.valueOf(isFinished);
            isFTwo = Boolean.valueOf(isFinished);
        }

        plateNumber = "%" + plateNumber + "%";
        Page<Object> pageData = null;
        if (createdDate == null){
            pageData = settleOrderRepository.searchsettleinfoWithoutTime(shop, "%" + saleNoView + "%", "%" + tel + "%", isDeleted,plateNumber,isFone,isFTwo,user.organization, pageRequest);
        } else {
                DateTime startDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(createdDate + " 00:00:00");
                DateTime endDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(createdDate + " 23:59:59");
                pageData = settleOrderRepository.searchsettleinfoWithTime(shop, "%" + saleNoView + "%", "%" + tel + "%", isDeleted, startDate, endDate,plateNumber ,isFone,isFTwo,user.organization,  pageRequest);
        }

        return JqGridDataGenerator.getDataJson(pageData);
    }

    @RequestMapping(value = "/salenote/settlelist/mindicount", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getMinDiscount(String settleId){
        SettleOrder settleOrder = settleOrderRepository.findOne(Long.parseLong(settleId));
        double discount = 1;
        for (OrderDetail orderDetail : settleOrder.orderDetails){
            if(orderDetail.discountGranter == null){
                orderDetail.discount = 1;
            }
            if (discount > orderDetail.discount) {
                discount = orderDetail.discount;
            }
        }
        DecimalFormat df   = new DecimalFormat("######0");
        return  map(entry("discount",  df.format(discount * 100) + "%"),
                    entry("size",settleOrder.orderDetails.size())
                );
    }


    /**
     * 申请授权
     *
     * @param username 授权者账户
     * @param password 授权者密码
     * @return
     */
    @RequestMapping(value = "/salenote/authority/check", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public Map<String, Object> checkAuthority(String username, String password, Long id) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        Map<String, Object> map = new HashMap<>();
        ERPUser erpUser;
        if(id == null){
            erpUser = erpUserRepository.findByUsername(username);
//            erpUser = erpUserRepository.findByUsernameAndPassword(username, password);
            if(erpUser == null || !bCryptPasswordEncoder.matches(password, erpUser.password)){
                erpUser = null;
            }
        }else {
            erpUser = erpUserRepository.findOne(id);
        }
        boolean result = true;
        if (erpUser != null) {
            if(!erpUser.checkAuthority(AuthorityConst.MANAGE_ORG_DISCOUNTAPPROVE)){
                //用户无权限
                map.put("message","您没有该权限!");
                result = false;
            } else {
                map.put("erpUserRealName", erpUser.realName);
                map.put("erpUserId", erpUser.id);
            }
        } else {
            map.put("message","用户名或密码错误!");
            result = false;
        }
        map.put("result",result);
        return map;
    }
}
