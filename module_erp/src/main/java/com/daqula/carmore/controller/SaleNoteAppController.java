package com.daqula.carmore.controller;


import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.CarmoreProperties;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.customer.*;
import com.daqula.carmore.model.order.*;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.shop.Staff;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.util.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
public class SaleNoteAppController {

    private static final Logger log = LoggerFactory.getLogger(SaleNoteAppController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private VehicleInfoRepository vehicleInfoRepository;

    @Autowired
    private OperationItemRepository operationItemRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private BaseSetRepository baseSetRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StaffAttendanceRepository staffAttendanceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private PresaleOrderRepository presaleOrderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    CarmoreProperties carmoreProperties;

    @Autowired
    CampaignRepository campaignRepository;

    /**
     * 生成app销售开单
     * @param plateNumber 车牌号
     * @param preSaleId 预约id
     * @return
     */
    @RequestMapping("/salenote/salenoteapp")
    @Transactional
    public ModelAndView toSave(@RequestParam(required = false) String plateNumber, String preSaleId) {
        ModelAndView mav = new ModelAndView("/salenote/salenoteapp");
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

        if (settleOrder.orderDetails == null)
            settleOrder.orderDetails = new ArrayList<>();

        mav.addObject("settleOrder", settleOrder);
        mav.addObject("customerERPProfile", customerERPProfile);

        PresaleOrder presaleOrder = presaleOrderRepository.findOne(Long.parseLong(preSaleId));
        settleOrder.presaleOrder = presaleOrder;
        if (settleOrder.payment == null) {
            settleOrder.payment = presaleOrder.payment;
        }

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
        return mav;
    }

    /**
     * 保存app结算订单
     * @param nextOrSettle 保存 or 直接结算
     * @param salenotedata orderdetails字符串
     * @param receiver 接车人员
     * @param customerERPProfile custom信息
     * @param settleOrder 结算订单
     * @return
     */
    @RequestMapping(value = "/salenote/saveapp", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ModelAndView saveSettleOrder(String nextOrSettle, String salenotedata,String workhoursdata, Long receiver,
                                        @ModelAttribute CustomerERPProfile customerERPProfile, @ModelAttribute SettleOrder settleOrder) {
        ModelAndView mav;
        String saleNoView = "";

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
                orderDetail.discountPrice = Double.parseDouble(row[4]);
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
            List<Customer> customers = customerRepository.findByMobile(customerERPProfile.customer.mobile);
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
            }
            settleOrderRepository.save(settleOrder);
            saleNoView = settleOrder.saleNoView;

            PresaleOrder presaleOrder = presaleOrderRepository.findOne(settleOrder.presaleOrder.id);
            presaleOrder.settleOrder = settleOrder;
            presaleOrderRepository.save(presaleOrder);

        }else {//修改
            SettleOrder settleOrderUpdate = settleOrderRepository.findOne(settleOrder.id);
            //详细订单
            settleOrderUpdate.orderDetails.clear();
            settleOrderUpdate.orderDetails.addAll(orderDetails);

            Shop shop = (Shop)request.getSession().getAttribute("SHOP");
            settleOrderUpdate.shop = shopRepository.findOne(shop.id);

            //顾客信息
            List<Customer> customers = customerRepository.findByMobile(customerERPProfile.customer.mobile);
            settleOrderUpdate.customer = customers.get(0);
            settleOrderUpdate.payment.customer = settleOrderUpdate.customer;

            //收款机
            settleOrderUpdate.payment.cashAmount = settleOrder.payment.cashAmount;
            settleOrderUpdate.payment.posAmount = settleOrder.payment.posAmount;
            settleOrderUpdate.payment.appAmount = settleOrder.payment.appAmount;
            settleOrderUpdate.remark = settleOrder.remark;
            //工时
            settleOrderUpdate.operationItemDetails.clear();
            settleOrderUpdate.operationItemDetails.addAll(operationItemDetails);

            //接车人员
            Staff staff = staffRepository.findOne(receiver);
            settleOrderUpdate.receiver = staff;

            if(nextOrSettle.equals("0")) {//结算
                settleOrderUpdate.isFinish = true;
            }

            saleNoView = settleOrderUpdate.saleNoView;
        }
        VehicleInfo vehicleInfo = vehicleInfoRepository.findOne(settleOrder.vehicleInfo.id);
        vehicleInfo.lastMaintenanceMileage = settleOrder.vehicleInfo.lastMaintenanceMileage;
        vehicleInfoRepository.save(vehicleInfo);

        if(nextOrSettle.equals("0")) {//结算
            mav = new ModelAndView("redirect:/salenote/salenoteprint?saleNoView=" + saleNoView );
//            String mobileNumber = customerERPProfile.customer.mobile;
////            String mobileNumber = "13917224164";
//            RestTemplate restTemplate = new RestTemplate();
//            settleOrder = settleOrderRepository.findOne(settleOrder.id);
//            String plateNumber = vehicleInfoRepository.findOne( settleOrder.vehicleInfo.id).plateNumber;
//            plateNumber = plateNumber.substring(plateNumber.length()-3,plateNumber.length());
//            String date = (settleOrder.createdDate == null? new Date():settleOrder.createdDate).toString().substring(0, 10);
//            String shopName = settleOrder.shop.name;
//            String amount = settleOrder.payment.appAmount + "";
//
//            String result = restTemplate.getForObject(String.format(carmoreProperties.getSMSUri(),
//                    carmoreProperties.getSMSUser(), carmoreProperties.getSMSPass(), mobileNumber,
//                    "您的牌照尾号"+ plateNumber +"的车辆，"+ date +"在"+ shopName +"消费"+ amount +"元。下载“修车么“APP，车辆状况时时提醒，更多商品优惠活动等待您。（下载链接）"), String.class);
//            String smsStatus = result.split("\n")[0].split(",")[1];
//            if (!"0".equals(smsStatus)) {
//                String errorMessage = String.format("Fail to send message to mobile number %s, error code: %s",
//                        mobileNumber, smsStatus);
//    			log.error(errorMessage);
//            }
        } else {//保存
            mav = new ModelAndView("redirect:/salenote/shownotsettleinfoapp?saleNoView=" + saleNoView );
        }

        return mav;
    }

    /**
     * 获取未结算销售单
     * @param saleNoView 显示用销售单号
     * @return
     */
    @RequestMapping("/salenote/shownotsettleinfoapp")
    @Transactional(readOnly = true)
    public ModelAndView showNotSettleInfo(@RequestParam String saleNoView,@AuthenticationPrincipal ERPUser user) {
        SettleOrder settleOrder = settleOrderRepository.findBySaleNoView(saleNoView).get(0);

        ModelAndView mav = new ModelAndView("/salenote/salenoteapp");
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
        //组织及门店信息
        mav.addObject("organizationInfo",organization.name + "(" + shop.name + ")");
        mav.addObject("shopInfo",shop);
        return mav;
    }

    /**
     * app预约保存车牌号和Vin码
     *
     * @param plateNumber 车牌号
     * @param vinCode vin码
     * @return
     */
    @RequestMapping(value = "/salenote/updateappvehicleinfo", method = RequestMethod.POST)
    @Transactional
    @ResponseBody
    public Map<String, Object> checkAuthority(String plateNumber, String vinCode, String id) {
        Map<String, Object> map = new HashMap<>();
        boolean result = true;
        PresaleOrder presaleOrder = presaleOrderRepository.findOne(Long.parseLong(id));
        presaleOrder.vehicleInfo.plateNumber = plateNumber;
        presaleOrder.vehicleInfo.vinCode = vinCode;
        presaleOrderRepository.save(presaleOrder);
        map.put("result",result);
        return map;
    }

    /**
     * 获取销售开单明细
     * @return
     */
    @RequestMapping("/salenote/searchorderdeatial")
    public ModelAndView SearchSettleInfo(@AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/salenote/searchorderdeatial");
        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);
        String workDate = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date());

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_SEARCHORDERDEATIAL)) {
            mav.setViewName("/noauthority");
        }

        mav.addObject("createdDateEnd",workDate);
        mav.addObject("createdDateStart",workDate);
        mav.addObject("shops",shops);
        return mav;
    }

    /**
     * 获取订单明细列表
     * @param shopid 门店id
     * @param createdDateStart 起始日期
     * @param createdDateEnd 结束日期
     * @return
     */
    @RequestMapping(value = "/salenote/searchorderdeatiallist", method = RequestMethod.POST)
    @Transactional(readOnly = true)
    public @ResponseBody Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                      @RequestParam String sord, @RequestParam String sidx,
                                                      String shopid, String createdDateStart, String createdDateEnd,String skuItemName) {
        PageRequest pageRequest = new PageRequest(page-1, rows);

        DateTime startDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(createdDateStart + " 00:00:00");
        DateTime endDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(createdDateEnd + " 23:59:59");
        Page<Object> pageData;
        if(StringUtils.isEmpty(skuItemName)) {
            pageData  = orderDetailRepository.searchOrderDetail(Long.parseLong(shopid), startDate, endDate,pageRequest);
        } else {
            skuItemName = "%" + skuItemName + "%";
            pageData  = orderDetailRepository.searchOrderDetailAndItem(Long.parseLong(shopid), startDate, endDate, skuItemName, pageRequest);
        }

        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 下载
     *
     * @param res
     * @param shopid 门店id
     * @param createdDateStart 起始日期
     * @param createdDateEnd 结束日期
     * @throws IOException
     */
    @RequestMapping(value = "/erpuser/exportorderdeatiallist", method = RequestMethod.GET)
    public void download(HttpServletResponse res,String shopid, String createdDateStart, String createdDateEnd, @AuthenticationPrincipal ERPUser user) throws IOException {
        DateTime startDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(createdDateStart + " 00:00:00");
        DateTime endDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(createdDateEnd + " 23:59:59");
        DecimalFormat df = new DecimalFormat("#.00");

        List<OrderDetail> orderDetails = orderDetailRepository.searchOrderDetail(Long.parseLong(shopid), startDate, endDate);

        List<ExcelExportOrderDeatailModel> list = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            ExcelExportOrderDeatailModel excelExportOrderDeatailModel = new ExcelExportOrderDeatailModel();
            excelExportOrderDeatailModel.realname = orderDetail.orderedItem.name;
            excelExportOrderDeatailModel.brandName = orderDetail.orderedItem.brandName;
            excelExportOrderDeatailModel.cost = orderDetail.cost;
            excelExportOrderDeatailModel.count = orderDetail.count;
            excelExportOrderDeatailModel.receivable = Double.parseDouble(df.format(orderDetail.receivable));
            excelExportOrderDeatailModel.staffName = orderDetail.merchandier.name;
            list.add(excelExportOrderDeatailModel);

        }

        OutputStream os = res.getOutputStream();

        try {
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=dict.xls");
            res.setContentType("application/octet-stream; charset=utf-8");

            ExcelUtil<ExcelExportOrderDeatailModel> util = new ExcelUtil<ExcelExportOrderDeatailModel>(ExcelExportOrderDeatailModel.class);// 创建工具类.
            util.exportExcel(list, "销售开单明细", 65536, os);// 导出

        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

}
