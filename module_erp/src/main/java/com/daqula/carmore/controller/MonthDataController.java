package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.order.*;
import com.daqula.carmore.model.shop.Expense;
import com.daqula.carmore.model.shop.FixedAsset;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 月度数据统计
 * Created by mdc on 2015/10/22.
 */
@Controller
public class MonthDataController {


    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private StockingOrderRepository stockingOrderRepository;

    @Autowired
    private FixedAssetRepository fixedAssetRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private MaterialOrderRepository materialOrderRepository;

    @Autowired
    private BaseSetRepository baseSetRepository;

    /**
     * 统计月度数据
     * @param year
     * @param month
     * @param shopId
     * @param erpUser
     * @return
     */
    @RequestMapping(value = "/monthdata/dataview", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView viewMonthData(String year, String month, Long shopId, @AuthenticationPrincipal ERPUser erpUser) {
        ModelAndView mav = new ModelAndView("/monthdata/dataview");
        DateTime nowDate = DateTime.now();
        Organization organization = erpUser.organization;
        List<Shop> shopList = shopRepository.findByOrganization(organization);
        if (!StringUtils.hasLength(year)){
            year = String.valueOf(nowDate.getYear());
            month = String.valueOf(nowDate.getMonthOfYear());
            if (shopList.size() > 0) shopId = shopList.get(0).id;
        }
        ArrayList years = new ArrayList();
        for (int i = 0;i < 3; i++) {
            int y = nowDate.getYear();
            String ys = String.valueOf((y - i));
            years.add(ys);
        }
        String queryDate = year + "-" + month + "-1";
        DateTime queryDateStart = new DateTime(queryDate);
        DateTime queryDateEnd = queryDateStart.plusMonths(1);
        Shop shop = shopRepository.findOne(shopId);
        // 获得营业额汇总
        Payment payment = paymentRepository.calSaleInfo(queryDateStart,queryDateEnd,shop);
        mav.addObject("payment",payment);
        double sumCost = 0;
        List<OrderDetail> orderDetails = orderDetailRepository.getOrderDetailInfo(shop.id,queryDateStart,queryDateEnd);
        List<MaterialOrder> materialOrders = materialOrderRepository.getMaterials(shop,queryDateStart,queryDateEnd);
        for (MaterialOrder materialOrder : materialOrders){
            for (MaterialOrderDetail materialOrderDetail : materialOrder.materialOrderDetails){
                sumCost += materialOrderDetail.cost * materialOrderDetail.number;
            }
        }
        for (OrderDetail orderDetail : orderDetails) {
            sumCost += orderDetail.cost * orderDetail.count;
        }
        mav.addObject("sumCost",sumCost);
        if (payment.amount != 0){
            mav.addObject("maoli",new DecimalFormat("#.00").format(((payment.amount - sumCost) / payment.amount) * 100));
        } else {
            mav.addObject("maoli","0");
        }

        //获得库存总价值
        //*******************盘点数据********************//
        BaseSet baseSet = baseSetRepository.findByShop(shop);
        int checkDay = 1;
        if (baseSet != null && baseSet.checkDay != 0 ) checkDay = baseSet.checkDay;
        String panDianDate = year + "-" + month + "-" + checkDay;
        DateTime panDianDateStart = new DateTime(panDianDate);
        DateTime panDianDateEnd = panDianDateStart.plusMonths(1);
        //期初总值
        List<StockingOrder> lastStockingOrders = stockingOrderRepository.findOrderCal(panDianDateStart.minusMonths(1),panDianDateEnd.minusMonths(1),shop);
        double startStockingSum = 0;
        if (lastStockingOrders.size() > 0){
            List<StockingOrderDetail> stockingOrderDetails = lastStockingOrders.get(0).stockingOrderDetails;
            for (StockingOrderDetail stockingOrderDetail : stockingOrderDetails) {
                startStockingSum += stockingOrderDetail.stockCost * stockingOrderDetail.calculateNumber;
            }
        }
        mav.addObject("startStockingSum",startStockingSum);
        List<StockingOrder> stockingOrders = stockingOrderRepository.findOrderCal(panDianDateStart,panDianDateEnd,shop);
        double stockingCostSum = 0;//当月期末值
        double beforeStockingCostSum = 0;//盘前总价
        if (stockingOrders.size() > 0){
            List<StockingOrderDetail> stockingOrderDetails = stockingOrders.get(0).stockingOrderDetails;
            for (StockingOrderDetail stockingOrderDetail : stockingOrderDetails) {
                stockingCostSum += stockingOrderDetail.stockCost * stockingOrderDetail.calculateNumber;
                beforeStockingCostSum += stockingOrderDetail.stockCost * stockingOrderDetail.oldNumber;
            }
        }

        double panYK = stockingCostSum - beforeStockingCostSum;
        mav.addObject("panYK",panYK);
        mav.addObject("stockingCostSum",stockingCostSum);
        //******************END********************//
        //耗材成本
        double ret = paymentRepository.getMaterialCal(queryDateStart,queryDateEnd,shop);
        mav.addObject("materialSum",ret);
        //日常费用
        Expense expense = expenseRepository.findByYearAndMonthAndShopAndDeleted(Integer.parseInt(year), Integer.parseInt(month), shop, false);
//        Expense expense = expenseRepository.findByYearAndMonthAndShop(Integer.parseInt(year), Integer.parseInt(month),shop);
        if (expense == null) expense = new Expense();
        double expenseSum = expense.rentExpense + expense.propertyExpense + expense.waterExpense + expense.electricExpense + expense.netPhoneExpense
                + expense.equipRepairsExpense + expense.otherExpense;
        mav.addObject("expenseSum",expenseSum);
        double staffExpenseSum = expense.staffBaseExpense + expense.staffCommissionExpense + expense.staffPerformanceExpense;
        mav.addObject("staffExpenseSum",staffExpenseSum);
        //固定资产
        List<FixedAsset> fixedAssets = fixedAssetRepository.findByShopAndAssetStatus(shop,0);
        double fixAssetSum = 0;
        for (FixedAsset fixedAsset : fixedAssets) {
            fixAssetSum += fixedAsset.number * fixedAsset.price;
        }
        mav.addObject("fixAssetSum", fixAssetSum);
        mav.addObject("shopList", shopList);
        mav.addObject("year",year);
        mav.addObject("shopId",shopId);
        mav.addObject("month",month);
        mav.addObject("years",years);
        return mav;
    }

}
