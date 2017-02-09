package com.daqula.carmore.controller;


import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.order.*;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.PresaleOrderRepositorySpecifications;
import com.daqula.carmore.repository.specification.SettleAccountsRepositorySpecifications;
import com.daqula.carmore.repository.specification.SettleOrderSpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.SaleForm;
import com.daqula.carmore.util.SessionUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.*;


/**
 * Created by thy on 2015/8/14.
 */
@Controller
public class PaymentController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private PresaleOrderRepository  presaleOrderRepository;

    @Autowired
    private SettleAccountsRepository settleAccountsRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    public MaterialOrderRepository materialOrderRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    /**
     * 预约列表
     * @return
     */
    @RequestMapping(value = "/payment/list",method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView preSale(@AuthenticationPrincipal ERPUser user,HttpSession session) {
        Shop shop = (Shop) session.getAttribute("SHOP");

        ModelAndView mav = new ModelAndView("/payment/presale");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_PAYMENTPRE)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shops == null) {
            mav.setViewName("/logout");
            return mav;
        }
        mav.addObject("shops", shops);
        return mav;
    }

    /**
     * 预约信息分页
     * @return
     */
    @RequestMapping(value = "/payment/list/data", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody
    Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                 @RequestParam String sord, @RequestParam String sidx,@ModelAttribute PresaleOrder presaleOrder) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Shop shop = null;
        if (presaleOrder.shop != null) {
            shop = shopRepository.findOne(presaleOrder.shop.id);
        }
        Specification<PresaleOrder> specification = Specifications.where(PresaleOrderRepositorySpecifications.filterByCustomerMobile(presaleOrder.customer.mobile))
                .and(PresaleOrderRepositorySpecifications.filterByShop(shop))
//                .and(PresaleOrderRepositorySpecifications.filterBySettleOrder(null))
                .and(PresaleOrderRepositorySpecifications.filterByCancelled(false));
        Page<PresaleOrder> pageData = presaleOrderRepository.findAll(specification, pageRequest);
        for (PresaleOrder po : pageData){
            CustomerAppProfile customerAppProfile =  customerProfileRepository.findAppProfileByCustomer(po.customer);
            if(customerAppProfile != null) po.customerName = customerAppProfile.nickName;
        }
        return JqGridDataGenerator.getDataJson(pageData);
    }


    /**
     * 预约信息查询
     * @param shopname 门店名
     * @return
     */
    @RequestMapping("/payment/list/query")
    @Transactional(readOnly = true)
    public @ResponseBody
    Map<String, Object> queryListData(@RequestParam int page, @RequestParam int rows,
                                 @RequestParam String sord, @RequestParam String sidx, @RequestParam String shopname) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        Page<PresaleOrder> pageData = presaleOrderRepository.findByCancelled(false ,pageRequest);

        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 付款信息
     * @return
     */
    @RequestMapping(value = "/payment/query", method = RequestMethod.POST)
    public ModelAndView queryList(@RequestParam String mobile, String id ,String shops) {
        return new ModelAndView("/payment/presale", map(entry("shops", shopRepository.findAll())));
    }

    /**
     * 预约明细
     * @return
     */
    @RequestMapping(value = "/payment/presaledtal")
    public ModelAndView presaledetal(@RequestParam String id) {

        PresaleOrder presaleOrder = presaleOrderRepository.findOne(Long.parseLong(id));

        return new ModelAndView("/payment/presaledtal", map(entry("orders", presaleOrder.orderDetails)) );
    }

    /**
     * 预约信息删除
     * @return
     */
    @RequestMapping(value = "/payment/presaledelete")
    @Transactional
    public ModelAndView presaledelete(@RequestParam String id) {
        ModelAndView mav = new ModelAndView();

        PresaleOrder presaleOrder = presaleOrderRepository.findOne(Long.parseLong(id));
        presaleOrder.cancelled = true;
        presaleOrderRepository.save(presaleOrder);
        mav.setViewName("/message");
        mav.addObject("message", "作废成功");
        mav.addObject("responsePage", "/payment/list");

        return mav;
    }


    /**
     * 进入汇总及结算页面
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "/payment/daysettlecal", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView daySettleCal(@AuthenticationPrincipal ERPUser user,HttpSession session, String calDateStart,String calDateEnd,Long shopId) {
        ModelAndView mav = new ModelAndView("/payment/daysettlecal");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_CALDAYSALEPRICE)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);
        if (shopId == null){
            Shop tempShop = (Shop) session.getAttribute("SHOP");
            shopId = tempShop.id;
        }
        Shop shop = shopRepository.findOne(shopId);

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
        //--处理日期

//        Specification specificationAccount = Specifications.where(SettleAccountsRepositorySpecifications.filterByShop(shop))
//                .and(SettleAccountsRepositorySpecifications.filterByCalDate(filterTimeStart.toDate()));
//        SettleAccounts settleAccounts = (SettleAccounts)settleAccountsRepository.findOne(specificationAccount);
//        if (settleAccounts != null) {
//            isCal = true;
//        }
//        mav.addObject("isCal",isCal);
        Specification<SettleOrder> specification = Specifications.where(SettleOrderSpecifications.filterByShop(shop))
                .and(SettleOrderSpecifications.createDayGreater(filterTimeStart.toDate()))
                .and(SettleOrderSpecifications.filteredByDeleted(false))
                .and(SettleOrderSpecifications.createDayLess(filterTimeEnd.toDate()));
        List<SettleOrder> settleOrders = settleOrderRepository.findAll(specification);
        //--计算营业额 start
        double cash = 0;
        double pos = 0;
        double app = 0;
        double other = 0;
        int zeroOrders = 0;
        double allAmount = 0;
        double allPurchasedAmount = 0;
        int notFinished = 0;
        double tempAllAmount = 0;
        double sumCost = 0;
        for(SettleOrder settleOrder : settleOrders) {
            if (!settleOrder.isFinish){
                notFinished ++;
                continue;
            }
            Payment payment = settleOrder.payment;
            if (payment != null) {
                cash += payment.cashAmount;
                pos += payment.posAmount;
                app += payment.appAmount;
                other += payment.otherAmount;
            }
            tempAllAmount = payment.cashAmount + payment.posAmount + payment.appAmount + payment.otherAmount;
            if (settleOrder.customerPurchasedSuite == null){
                allAmount += tempAllAmount;
                if (tempAllAmount == 0 ) {
                    zeroOrders ++; //0收入普通施工单
                }
            } else {
                allPurchasedAmount += tempAllAmount;
            }



        }
        Payment paymentToPage = new Payment();
        paymentToPage.amount = cash + pos + app + other;
        paymentToPage.cashAmount = cash;
        paymentToPage.posAmount = pos;
        paymentToPage.appAmount = app;
        paymentToPage.otherAmount = other;

        //---计算营业额 end
        //饼图数据 start
        List<OrderDetail> orderDetails = orderDetailRepository.getOrderDetailInfo(shop.id,filterTimeStart,filterTimeEnd);
        SaleForm saleForm = new SaleForm();
        sumCost = 0;
        List<MaterialOrder> materialOrders = materialOrderRepository.getMaterials(shop,filterTimeStart,filterTimeEnd);
        for (MaterialOrder materialOrder : materialOrders){
            for (MaterialOrderDetail materialOrderDetail : materialOrder.materialOrderDetails){
                sumCost += materialOrderDetail.cost * materialOrderDetail.number;
            }
        }
        for (OrderDetail orderDetail : orderDetails){
            sumCost += orderDetail.cost * orderDetail.count;
            saleForm.sum += orderDetail.count;
            int rootCategory = orderDetail.orderedItem.rootCategory;
            switch (rootCategory){
                case 0:
                    saleForm.temp += orderDetail.count;
                    break;
                case 1:
                    saleForm.jiyou += orderDetail.count;
                    break;
                case 2:
                    saleForm.jilv += orderDetail.count;
                    break;
                case 3:
                    saleForm.luntai += orderDetail.count;
                    break;
                case 4:
                    saleForm.dianpin += orderDetail.count;
                    break;
                case 5:
                    saleForm.dianzi += orderDetail.count;
                    break;
                case 6:
                    saleForm.meirong += orderDetail.count;
                    break;
                case 7:
                    saleForm.qicheyongpin += orderDetail.count;
                    break;
                case 8:
                    saleForm.yanghu += orderDetail.count;
                    break;
                case 9:
                    saleForm.haocai += orderDetail.count;
                    break;
                case 10:
                    saleForm.dengju += orderDetail.count;
                    break;
                case 11:
                    saleForm.yugua += orderDetail.count;
                    break;
                case 12:
                    saleForm.fadongji += orderDetail.count;
                    break;
                case 13:
                    saleForm.dipanpeijian += orderDetail.count;
                    break;
                case 14:
                    saleForm.biansuxiang += orderDetail.count;
                    break;
                case 15:
                    saleForm.dianqi += orderDetail.count;
                    break;
                case 16:
                    saleForm.fugai += orderDetail.count;
                    break;
                case 17:
                    saleForm.fuwu += orderDetail.count;
                    break;
                default:
                    System.out.println(orderDetail.count);
                    break;
            }

        }
        double sumProfit = 0;
        if (paymentToPage.amount != 0)sumProfit = (paymentToPage.amount - sumCost)/paymentToPage.amount;
//        saleForm.setPercent();
        //饼图数据end
        mav.addObject("sumCost",sumCost);
        mav.addObject("saleForm",saleForm);
        mav.addObject("sumProfit",sumProfit);
        mav.addObject("notFinishedNum",notFinished);
        mav.addObject("calDateStart",calDateStart);
        mav.addObject("calDateEnd",calDateEnd);
        mav.addObject("zeroOrders",zeroOrders);
        mav.addObject("paymentToPage",paymentToPage);
        mav.addObject("allAmount",allAmount);
        mav.addObject("allPurchasedAmount",allPurchasedAmount);
        mav.addObject("shops",shops);
        mav.addObject("chooseShopId",shopId);
        return mav;
    }

    /**
     * 关店结算
     * @param settleAccounts
     * @param session
     * @return
     */
    @RequestMapping(value = "/payment/settletoday",method = RequestMethod.POST)
    @Transactional
    public ModelAndView settleToday(@ModelAttribute SettleAccounts settleAccounts,HttpSession session) {
        ModelAndView mav = new ModelAndView("/message");
        Shop shop = (Shop) session.getAttribute("SHOP");
        shop = shopRepository.findOne(shop.id);
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd");
        settleAccounts.calDate = DateTime.parse(DateTime.now().toString("yyyy/MM/dd"), format);
        settleAccounts.shop = shop;
        try {
            settleAccountsRepository.save(settleAccounts);
        } catch (Exception ex) {
            ex.printStackTrace();
            mav.addObject("message", "操作失败");
        }
        mav.addObject("message", "操作成功");
        mav.addObject("responsePage", "/payment/daysettlecal");
        return mav;
    }


}
