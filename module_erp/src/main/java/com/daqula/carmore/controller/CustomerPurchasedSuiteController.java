package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.SuiteItem;
import com.daqula.carmore.model.customer.*;
import com.daqula.carmore.model.order.OrderDetail;
import com.daqula.carmore.model.order.Payment;
import com.daqula.carmore.model.order.SettleOrder;
import com.daqula.carmore.model.order.SettleOrderHistory;
import com.daqula.carmore.model.shop.*;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by swj on 2015/10/8.
 */
@Controller
public class CustomerPurchasedSuiteController {

    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    @Autowired
    private CustomerPurchasedSuiteItemRepository customerPurchasedSuiteItemRepository;

    /**
     * 登录检验
     */
    @Autowired
    private ERPUserRepository erpUserRepository;

    /**
     * 用户
     */
    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    /**
     * 组织
     */
    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * 付款
     */
    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * 门店套餐模板
     */
    @Autowired
    private CustomSuiteRepository customSuiteRepository;

    /**
     * 用户已购买套餐
     */
    @Autowired
    private CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    /**
     * 销售结算单
     */
    @Autowired
    private SettleOrderRepository settleOrderRepository;

    /**
     * 门店
     */
    @Autowired
    private ShopRepository shopRepository;

    /**
     * 本地会员异地消费 记录表
     */
    @Autowired
    private SettleOrderHistoryRepository settleOrderHistoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerERPProfileRepository customerERPProfileRepository;

    @Autowired
    private SuiteItemRepository suiteItemRepository;

    @Autowired
    private VehicleInfoRepository vehicleInfoRepository;

    @Autowired
    private StaffRepository staffRepository;

    /**
     * 考勤人员
     */
    @Autowired
    private StaffAttendanceRepository staffAttendanceRepository;

//    @Autowired
//    private Sett

//    @Autowired
//    private StaffRepository staffRepository;

    /**
     * 本店会员异地消费 首页
     *
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/remote/list", method = RequestMethod.GET)
    public ModelAndView remoteList(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/customerpurchasesuite/remoteList");
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITEREMOTE)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        Organization organization = new Organization();
        organization.id = user.organization.id;
        List<Shop> shopList = shopRepository.findByOrganization(organization);
        mav.addObject("shopList", shopList);
        return mav;
    }

    /**
     * 本店会员异地消费 表格数据查询
     *
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param user
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/remote/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> remoteListData(@RequestParam int page, @RequestParam int rows,
                                              @RequestParam String sord, @RequestParam String sidx,
                                              @AuthenticationPrincipal ERPUser user, String keyWord,
                                              String shopId) {
        Shop shop = new Shop();
        if (StringUtils.isBlank(shopId))
            shop = null;
        else
            shop.id = Long.valueOf(shopId);
        Organization organization = new Organization();
        organization.id = user.organization.id;
        if (StringUtils.isBlank(keyWord))
            keyWord = "";
        List<SettleOrderHistory> resultList = settleOrderHistoryRepository.findListByOrganizationAndKeyWordAndShop(organization, keyWord, shop, page, rows);
        long total = settleOrderHistoryRepository.findCountByOrganizationAndKeyWordAndShop(organization, keyWord, shop);
        for (SettleOrderHistory settleOrderHistory : resultList) {

            CustomerERPProfile erpProfile = customerProfileRepository.findERPProfileByCustomer(settleOrderHistory.settleOrder.customer, organization);

            settleOrderHistory.settleOrder.updatedBy = erpProfile.realName; //updateBy临时存放realName 字段  做显示用

        }
        return JqGridDataGenerator.getNativeDataJson(resultList, (int) total, rows, page);
    }

    /**
     * 签收确认
     *
     * @param id
     * @return
     */

    @RequestMapping(value = "/customerpurchasesuite/remote/confirm", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public boolean changeIsSingFor(@RequestParam long id) {
        try {

            settleOrderHistoryRepository.setIsSignForById(true, id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 会员管理首页
     *
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/list", method = RequestMethod.GET)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user,HttpSession session) {
        ModelAndView mav = new ModelAndView("/customerpurchasesuite/list");
        List<Shop> shopList = SessionUtil.getShopList(session,organizationRepository,shopRepository);
        mav.addObject("shops", shopList);
        String  calDateStart = DateTime.now().toString("yyyy/MM/dd");
        String calDateEnd = DateTime.now().toString("yyyy/MM/dd");
        mav.addObject("calDateStart",calDateStart);
        mav.addObject("calDateEnd",calDateEnd);
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITE)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        return mav;
    }

    /**
     *
     * @throws IOException
     */
    @RequestMapping(value = "/customerpurchasesuite/detail/excel/export")
    public void download(String keyWord,Long shopId,String calDateStart,String calDateEnd,
                         HttpServletResponse res, @AuthenticationPrincipal ERPUser user) throws IOException {
        Organization organization = organizationRepository.findOne(user.organization.id);

        if (calDateStart == null || calDateStart.equals("")) {
            calDateStart = DateTime.now().toString("yyyy/MM/dd");
        }
        if (calDateEnd == null || calDateEnd.equals("")) {
            calDateEnd = DateTime.now().toString("yyyy/MM/dd");
        }
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd");
        DateTime filterTimeStart = DateTime.parse(calDateStart, format);
        DateTime filterTimeEnd = DateTime.parse(calDateEnd, format);
        filterTimeEnd = filterTimeEnd.plusDays(1);

        if (org.apache.commons.lang3.StringUtils.isBlank(keyWord)) {
            keyWord = "";
        }
        Shop shop = shopRepository.findOne(shopId);

        List<Object[]> result2 = customerPurchasedSuiteRepository.findListByKeyWordAndShop(keyWord, shop, user.organization.id,filterTimeStart,filterTimeEnd);

        List<ExcelExportCustomerPurchasedSuiteDetail> list = new ArrayList<>();
//        SimpleDateFormat format = new SimpleDateFormat("YYYY/MM/DD");
//        SimpleDateFormat formatTime = new SimpleDateFormat("YYYY/MM/DD");
        for (Object[] data : result2) {


            CustomerPurchasedSuite customerPurchasedSuite = (CustomerPurchasedSuite) data[6];
            List<CustomerPurchasedSuiteItem> byPurchasedSuite = customerPurchasedSuiteItemRepository.findByPurchasedSuite(customerPurchasedSuite);

            List<SettleOrder> settleOrders = settleOrderRepository.findSuiteList(customerPurchasedSuite);
            for (SettleOrder so : settleOrders){
                for (OrderDetail od : so.orderDetails){
                    for(CustomerPurchasedSuiteItem cs : byPurchasedSuite ) {
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

            for (CustomerPurchasedSuiteItem item : byPurchasedSuite) {
                ExcelExportCustomerPurchasedSuiteDetail detail = new ExcelExportCustomerPurchasedSuiteDetail();

                detail.phone = (String) data[0];
                Boolean status = (Boolean) data[3];
                detail.status = status?"有效":"失效";
                detail.shopName = (String) data[4];
                detail.name = (String) data[5];
                detail.suiteName = (String) data[7];

                detail.brand = item.customStockItem.brandName;
                detail.commodityName = item.customStockItem.name;
                detail.description = item.customStockItem.description;
                detail.ofee = item.cost;
                detail.usefulTime = item.getTimesLeft();
                detail.total = item.times;
                detail.isInfinite = item.times<=0?"是":"否";
                list.add(detail);
            }

        }
        OutputStream os = res.getOutputStream();
        try {
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=suiteitem_"+ organization.serialNum +".xls");
            res.setContentType("application/octet-stream; charset=utf-8");
            ExcelUtil<ExcelExportCustomerPurchasedSuiteDetail> util = new ExcelUtil<ExcelExportCustomerPurchasedSuiteDetail>(ExcelExportCustomerPurchasedSuiteDetail.class);// 创建工具类.
            util.exportExcel(list, "剩余次数", 65536, os);// 导出
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     *
     * @throws IOException
     */
    @RequestMapping(value = "/customerpurchasesuite/excel/export")
    public void downloadDetail(String keyWord,Long shopId,String calDateStart,String calDateEnd,
                         HttpServletResponse res, @AuthenticationPrincipal ERPUser user) throws IOException {
        Organization organization = organizationRepository.findOne(user.organization.id);

        if (calDateStart == null || calDateStart.equals("")) {
            calDateStart = DateTime.now().toString("yyyy/MM/dd");
        }
        if (calDateEnd == null || calDateEnd.equals("")) {
            calDateEnd = DateTime.now().toString("yyyy/MM/dd");
        }
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd");
        DateTime filterTimeStart = DateTime.parse(calDateStart, format);
        DateTime filterTimeEnd = DateTime.parse(calDateEnd, format);
        filterTimeEnd = filterTimeEnd.plusDays(1);

        if (org.apache.commons.lang3.StringUtils.isBlank(keyWord)) {
            keyWord = "";
        }
        Shop shop = shopRepository.findOne(shopId);

        List<Object[]> result2 = customerPurchasedSuiteRepository.findListByKeyWordAndShop(keyWord, shop, user.organization.id,filterTimeStart,filterTimeEnd);


        List<ExcelExportCustomerPurchasedSuite> list = new ArrayList<>();
        for (Object[] data : result2) {

            ExcelExportCustomerPurchasedSuite excelExportCustomerPurchasedSuite = new ExcelExportCustomerPurchasedSuite();

            excelExportCustomerPurchasedSuite.phone = (String) data[0];
            excelExportCustomerPurchasedSuite.startDate = (DateTime) data[2];
            Boolean status = (Boolean) data[3];
            excelExportCustomerPurchasedSuite.status = status?"有效":"失效";
            excelExportCustomerPurchasedSuite.shopName = (String) data[4];
            excelExportCustomerPurchasedSuite.name = (String) data[5];
            excelExportCustomerPurchasedSuite.suiteName = (String) data[7];
            Integer listday = (Integer) data[8];
            excelExportCustomerPurchasedSuite.listDay = listday >=0 ? listday.toString():"已过期";
            excelExportCustomerPurchasedSuite.saleName = (String) data[9];
            excelExportCustomerPurchasedSuite.suiteFee = (Double) data[10];
            excelExportCustomerPurchasedSuite.realFee = (Double) data[11];
            excelExportCustomerPurchasedSuite.offName = (String) data[12];

            list.add(excelExportCustomerPurchasedSuite);
        }
        OutputStream os = res.getOutputStream();
        try {
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=suite_"+ organization.serialNum +".xls");
            res.setContentType("application/octet-stream; charset=utf-8");
            ExcelUtil<ExcelExportCustomerPurchasedSuite> util = new ExcelUtil<ExcelExportCustomerPurchasedSuite>(ExcelExportCustomerPurchasedSuite.class);// 创建工具类.
            util.exportExcel(list, "套餐销售", 65536, os);// 导出
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }


        /**
         * 会员管理首页表格数据查询
         *
         * @param page    第几页
         * @param rows    单页最大记录数
         * @param sord    排序字段
         * @param sidx    排序方式
         * @param keyWord 关键字条件 同时查 mobile realName
         * @return
         */
    @RequestMapping(value = "/customerpurchasesuite/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,
                                        @RequestParam String keyWord,Long shopId,String calDateStart,String calDateEnd,
                                        HttpSession session, @AuthenticationPrincipal ERPUser user) {
        if (calDateStart == null || calDateStart.equals("")) {
            calDateStart = DateTime.now().toString("yyyy/MM/dd");
        }
        if (calDateEnd == null || calDateEnd.equals("")) {
            calDateEnd = DateTime.now().toString("yyyy/MM/dd");
        }
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd");
        DateTime filterTimeStart = DateTime.parse(calDateStart, format);
        DateTime filterTimeEnd = DateTime.parse(calDateEnd, format);
        filterTimeEnd = filterTimeEnd.plusDays(1);
        sidx = !org.springframework.util.StringUtils.isEmpty(sidx) ? sidx : "cps.createdDate";
        switch (sidx){
            case "2":
                sidx = "cps.createdDate";
                break;
            case "7":
                sidx = "cps.suite.name";
                break;
            case "0":
                sidx = "cps.customer.mobile";
                break;
            case "5":
                sidx = "erp.realName";
                break;
            case "3":
                sidx = "cps.enabled";
                break;
            case "4":
                sidx = "cps.shop.name";
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(keyWord)) {
            keyWord = "";
        }
        Shop shop = shopRepository.findOne(shopId);
        long total = customerPurchasedSuiteRepository.findCountByKeyWordAndShop(keyWord, shop, user.organization.id,filterTimeStart,filterTimeEnd);

        List<Object[]> result2 = customerPurchasedSuiteRepository.findListByKeyWordAndShop(keyWord, shop, user.organization.id, page, rows, sord, sidx,filterTimeStart,filterTimeEnd);
        return JqGridDataGenerator.getNativeDataJson(result2, (int) total, rows, page);
    }

    /**
     * 转到新增/编辑页面
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/tosave", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toSave(String id, @AuthenticationPrincipal ERPUser user, HttpSession session) throws ParseException {
//        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");
        Shop shop = (Shop) session.getAttribute("SHOP");
        Organization organization = organizationRepository.findOne(user.organization.id);
        long ids = user.organization.id;
        ModelAndView mav = new ModelAndView("/customerpurchasesuite/form");
        String responseOption = "create";
        CustomerPurchasedSuite customerPurchasedSuite = new CustomerPurchasedSuite();
        //根据组织获取套餐模板集合
        List<CustomSuite> customSuiteList = customSuiteRepository.findByOrganizationAndEnabled(organization, true);

        /**
         * 过滤明细中已逻辑删除的项目  开始  在readonly为true的请求下可用
         */
        for (int i = 0; i < customSuiteList.size(); i++) {
            CustomSuite customSuite = customSuiteList.get(i);
            List<SuiteItem> suiteItems = customSuite.suiteItems;
            for (int j = suiteItems.size() - 1; j >= 0; j--) {
                if (suiteItems.get(j).deleted) suiteItems.remove(j);
            }
        }
        /**
         * 过滤明细中已逻辑删除的项目  结束  在readonly为true的请求下可用
         */


        mav.addObject("customSuiteList", customSuiteList);

        List<CustomerERPProfile> customerERPProfileList = customerProfileRepository.findERPProfileByOrganization(organization);

        CustomerERPProfile customerERPProfile = new CustomerERPProfile();
        if (StringUtils.isNotBlank(id)) {
            responseOption = "update";
            customerPurchasedSuite = customerPurchasedSuiteRepository.findOne(Long.valueOf(id));
            customerERPProfile = customerProfileRepository.findERPProfileByCustomer(customerPurchasedSuite.customer, organization);

        }

        mav.addObject("pageContent", responseOption);
        mav.addObject("customerERPProfileList", customerERPProfileList);
        mav.addObject("customerPurchasedSuite", customerPurchasedSuite);
        mav.addObject("shop", shop);
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String s = format.format(d);
        Date today = format.parse(s);

        List<Staff> staffList = staffAttendanceRepository.findByWorkDateAndShop(today, shop);
        mav.addObject("staffList", staffList);
        mav.addObject("shopId",shop.id);

        mav.addObject("customerERPProfile", customerERPProfile);
        return mav;
    }

    /**
     * 查看会员卡详细
     * @param id
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/todetail", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toDetail(String id, @AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("customerpurchasesuite/detailForm");
        Shop shop = (Shop) session.getAttribute("SHOP");
        Organization organization = organizationRepository.findOne(user.organization.id);
        CustomerPurchasedSuite customerPurchasedSuite = customerPurchasedSuiteRepository.findOne(Long.valueOf(id));
        customerPurchasedSuite.purchasedSuiteItems = customerPurchasedSuiteItemRepository.findByPurchasedSuite(customerPurchasedSuite);
        List<SettleOrder> settleOrders = settleOrderRepository.findSuiteList(customerPurchasedSuite);
        for (SettleOrder so : settleOrders){
            for (OrderDetail od : so.orderDetails){
                for(CustomerPurchasedSuiteItem cs : customerPurchasedSuite.purchasedSuiteItems ) {
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


        CustomerERPProfile customerERPProfile = customerProfileRepository.findERPProfileByCustomer(customerPurchasedSuite.customer, organization);

        if(customerPurchasedSuite.settleOrderId != null){
            SettleOrder settleOrder = settleOrderRepository.findOne(customerPurchasedSuite.settleOrderId);
            customerPurchasedSuite.suite.price = settleOrder.payment.amount;
            mav.addObject("settleOrder", settleOrder);
        }

        mav.addObject("customerPurchasedSuite", customerPurchasedSuite);
        mav.addObject("createdDate",customerPurchasedSuite.startDate.toString("yyyy-MM-dd"));
        mav.addObject("shop", shop);
        mav.addObject("customerERPProfile", customerERPProfile);

        return mav;
    }

    /**
     * 保存方法
     *
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(CustomerPurchasedSuite customerPurchasedSuite
            , @ModelAttribute Payment payment, @RequestParam Double receivable, @RequestParam String rowDatas
            , @ModelAttribute SettleOrder settleOrder, HttpSession session, @AuthenticationPrincipal ERPUser user
            , @ModelAttribute CustomSuite customSuite, String customerErpProfileId) {

        try {
            CustomerERPProfile erpProfile = customerERPProfileRepository.findOne(Long.valueOf(customerErpProfileId));
            customerPurchasedSuite.customer = erpProfile.customer;
            settleOrder.customer = erpProfile.customer;
            Organization organization = organizationRepository.findOne(user.organization.id);
            payment.amount = receivable == null ? customerPurchasedSuite.suite.price : receivable;
            payment.customer = customerPurchasedSuite.customer;
            payment = paymentRepository.save(payment);
            settleOrder.close = false;
            settleOrder.commented = false;
            settleOrder.isFinish = true;
            settleOrder.finishDate = DateTime.now();
            Staff staff = new Staff();
            staff.id = customerPurchasedSuite.staff.id;
            settleOrder.receiver = staff;
            CustomerERPProfile customerERPProfile = customerERPProfileRepository.findByOrganizationAndCustomer(organization, customerPurchasedSuite.customer);
            settleOrder.vehicleInfo = (customerERPProfile.vehicles).size() > 0 ? customerERPProfile.vehicles.get(0) : new VehicleInfo();
            Shop shop = (Shop) session.getAttribute("SHOP");
            settleOrder.shop = shop;
            settleOrder.payment = payment;
            Long count = settleOrderRepository.findMaxOrderNum(shop);
            count = count - 1;
            do {
                count++;
            } while (settleOrderRepository.findBySaleNoAndShop(count, settleOrder.shop) != null);
            String saleNoView = OrderUtil.getViewOrderNumber(organization, shop, OrderUtil.ORDER_TYPE_SALE, count);
            settleOrder.saleNo = count;
            settleOrder.saleNoView = saleNoView;
            settleOrder.saleCategory = SaleShelf.CATEGORY_VIP;
            customerPurchasedSuite.shop = shop;
            customerPurchasedSuite.startDate = new DateTime();
            customerPurchasedSuite.enabled = true;
            customerPurchasedSuite.staff = staff;
            CustomerPurchasedSuite purchasedSuite = customerPurchasedSuiteRepository.save(customerPurchasedSuite);
            this.savePurchasedSuiteItems(rowDatas, purchasedSuite);
            settleOrder.customerPurchasedSuite = purchasedSuite;

            SettleOrder order = settleOrderRepository.save(settleOrder);
            purchasedSuite.settleOrderId = order.id;

            String message = "保存成功";
            ModelAndView mav = new ModelAndView("/message");
            mav.addObject("responsePage", "/customerpurchasesuite/list");
            mav.addObject("message", message);
            return mav;
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    private void savePurchasedSuiteItems(String rowDatas, CustomerPurchasedSuite customerPurchasedSuite) {
        String[] rowData = rowDatas.split(";");
        CustomerPurchasedSuiteItem customerPurchasedSuiteItem;
        for (String data : rowData) {
            String[] datas = data.split(",");
            CustomStockItem customStockItem ;
            if (datas.length >= 1) {
                long id = Long.valueOf(datas[0]);
                customerPurchasedSuiteItem = id != 0 ? customerPurchasedSuiteItemRepository.findOne(id) : new CustomerPurchasedSuiteItem();
                customStockItem = customStockItemRepository.findOne(Long.valueOf(datas[1]));
                if (datas[2].equals("true")) {
                    customerPurchasedSuiteItem.times = -1;
                } else {
                    customerPurchasedSuiteItem.times = Integer.valueOf(datas[3]);
                }
                customerPurchasedSuiteItem.cost = Double.parseDouble(datas[4]);
                customerPurchasedSuiteItem.usedTimes = 0;
                customerPurchasedSuiteItem.customStockItem = customStockItem;
                customerPurchasedSuiteItem.purchasedSuite = customerPurchasedSuite;
                customerPurchasedSuiteItemRepository.save(customerPurchasedSuiteItem);
            }

        }
    }

    /**
     * 申请授权 账户密码/指纹
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/authority/check", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    @ResponseBody
    public Map<String, Object> checkAuthority(String username, String password, Long id) {
        Map<String, Object> map = new HashMap<>();
        //TODO 加账户权限验证
        ERPUser erpUser = id != null ? erpUserRepository.findOne(id) : erpUserRepository.findByUsernameAndPasswordUseBCrypt(username, password);
        boolean result = true;
        if (erpUser != null) {
            if (!erpUser.checkAuthority(AuthorityConst.MANAGE_ORG_DISCOUNTAPPROVE)) {
                //用户无权限
                map.put("message", "您没有该权限!");
                result = false;
            }
//            map.put("result", true);
            map.put("erpUserRealName", erpUser.realName);
        } else {
            //用户不存在
            map.put("message", "用户名或密码错误!");
            result = false;
//            map.put("result", false);
        }
        map.put("result", result);
        return map;
    }

    /**
     * 启停用
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/customerpurchasesuite/enabled", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public boolean changeEnabled(@RequestParam Long id) {

        try {
            CustomerPurchasedSuite customerPurchasedSuite = customerPurchasedSuiteRepository.findOne(id);
            customerPurchasedSuite.enabled = !customerPurchasedSuite.enabled;
            SettleOrder settleOrder = settleOrderRepository.findOne(customerPurchasedSuite.settleOrderId);
            settleOrder.deleted = !customerPurchasedSuite.enabled;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

   /* *//**
     * 卡种设置 启停用
     *
     * @param id
     * @return
     *//*
    @RequestMapping(value = "/customerpurchasesuite/vipcard/enabled", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public boolean changeVipCardEnabled(@RequestParam Long id) {

        try {
            CustomSuite customSuite = customSuiteRepository.findOne(id);
            customSuite.enabled = !customSuite.enabled;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }*/

    /**
     * 会员套餐管理  页面  点击会员售卡时触发请求   校验是否存在可选会员套餐
     * 返回true false
     * true 存在   则返回页面后跳转会员售卡页面
     * false alert提醒没有套餐
     * @param user
     * @return
     *//*
    @RequestMapping(value = "/customerpurchasesuite/hasCustomSuite", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public boolean hasCustomSuite(@AuthenticationPrincipal ERPUser user){
        Organization organization = new Organization();
        organization.id = user.organization.id;
        long total = customSuiteRepository.findTotalByOrganizationAndEnabled(organization,true);
        if(total == 0)
            return false;
        else
            return true;
    }*/

   /* @RequestMapping(value = "/erpuser/excel/toupload",method = RequestMethod.GET)
    public ModelAndView toUpload(){
        ModelAndView mav = new ModelAndView();


    }*/

    /**
     * excel方式导入 erpUser  mobile realName;
     *
     * @param file
     * @param session
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/erpuser/excel/upload", method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public ModelAndView handleFileUpload(@RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        ModelAndView mav = new ModelAndView("/message");
        String message = "导入成功";

        Organization organization = new Organization();
        organization.id = ((Organization) session.getAttribute("ORGANIZATIONS")).id;

        String contentType = file.getContentType();
        String name = file.getName();
        String originalFilename = file.getOriginalFilename();
        String path = session.getServletContext().getRealPath("upload");
        String typeName = "";
        String[] split = originalFilename.split("\\.");
        if (split.length > 0) {
            typeName = split[split.length - 1];
            if (!"xls".equals(typeName) && !equals(typeName) && !"xlsx".equals(typeName)) {
                message = "文件类型错误!";

                mav.addObject("message", message);
                mav.addObject("responsePage", "/customer/list");
                return mav;
            }
        }
//        request

        byte[] bytes = file.getBytes();

        ExcelUtil<ExcelImportUserModel> util = new ExcelUtil<ExcelImportUserModel>(
                ExcelImportUserModel.class);

        try {
            List<ExcelImportUserModel> list = util.importExcel("Sheet1", file.getInputStream(), typeName);

            for (ExcelImportUserModel excelImportUserModel : list) {
                String mobile = excelImportUserModel.mobile;
                String realname = excelImportUserModel.realname;
                CustomerERPProfile customerERPProfile;

                Customer customer;
                List<Customer> customers = customerRepository.findByMobile(mobile);
                if (customers.size() == 0) {
                    customer = new Customer();
                    customer.mobile = mobile;
                    customer.token = UUID.randomUUID().toString();
                    customer = customerRepository.save(customer);

                    customerERPProfile = new CustomerERPProfile();
                    customerERPProfile.realName = realname;
                    customerERPProfile.customer = customer;
                    customerERPProfile.organization = organization;
                    customerERPProfileRepository.save(customerERPProfile);
                } else {
                    customer = customers.get(0);
                    customerERPProfile = customerProfileRepository.findERPProfileByCustomer(customer, organization);
                    if (customerERPProfile == null) {
                        customerERPProfile = new CustomerERPProfile();
                        customerERPProfile.realName = realname;
                        customerERPProfile.profileType = CustomerERPProfile.PROFILE_TYPE_ERP;
                        customerERPProfile.customer = customer;
                        customerERPProfile.organization = organization;
                        customerProfileRepository.save(customerERPProfile);
                    } else {
                        customerERPProfile.realName = realname;
                    }
                }
            }
        } catch (Exception e) {
            message = "内容格式错误!";
            e.printStackTrace();
        }

        mav.addObject("message", message);
        mav.addObject("responsePage", "/customer/list");
        return mav;
    }

    /**
     * 下载
     *
     * @param res
     * @param tel
     * @param carNum
     * @param user
     * @throws IOException
     */
    @RequestMapping(value = "/erpuser/excel/export", method = RequestMethod.GET)
    public void download(HttpServletResponse res, String tel, String carNum, @AuthenticationPrincipal ERPUser user) throws IOException {

        List<Object[]> vehicleInfoObjs;

        if (org.jadira.usertype.spi.utils.lang.StringUtils.isEmpty(carNum)) {
            if (org.jadira.usertype.spi.utils.lang.StringUtils.isEmpty(tel)) {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByOrganization(user.organization);
            } else {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByTelAndOrganization(tel, user.organization);
            }
        } else {
            if (org.jadira.usertype.spi.utils.lang.StringUtils.isEmpty(tel)) {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByPlateNumberAndOrganization(carNum, user.organization);
            } else {
                vehicleInfoObjs = vehicleInfoRepository.findVehicleByPlateNumberAndTelAndOrganization(carNum, tel, user.organization);
            }
        }


        System.out.println(vehicleInfoObjs);

        List<ExcelExportUserModel> list = new ArrayList<>();
        for (Object[] objs : vehicleInfoObjs) {

            VehicleInfo vehicleInfo = (VehicleInfo) objs[0];
            String realName = (String) objs[1];
            String gender = (Integer) objs[2] == 0 ? "男" : "女";
            String mobile = (String) objs[3];

            ExcelExportUserModel excelExportUserModel = new ExcelExportUserModel();
            excelExportUserModel.realname = realName;
            excelExportUserModel.mobile = mobile;
            excelExportUserModel.carNum = vehicleInfo.plateNumber;
            excelExportUserModel.gender = gender;
            excelExportUserModel.model = vehicleInfo.model.version;

            list.add(excelExportUserModel);

        }

        OutputStream os = res.getOutputStream();

        try {
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=dict.xls");
            res.setContentType("application/octet-stream; charset=utf-8");

            ExcelUtil<ExcelExportUserModel> util = new ExcelUtil<ExcelExportUserModel>(ExcelExportUserModel.class);// 创建工具类.
            util.exportExcel(list, "顾客信息", 65536, os);// 导出

        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

}
