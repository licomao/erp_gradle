package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.order.StockingOrder;
import com.daqula.carmore.model.order.StockingOrderDetail;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.StockingOrderSpecifications;
import com.daqula.carmore.util.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

import static com.daqula.carmore.util.CollectionUtil.*;

/**
 * 库存盘点控制器
 * Created by mdc on 2015/9/23.
 */
@Controller
public class StockingOrderController {

    @Autowired
    public StockingOrderRepository stockingOrderRepository;

    @Autowired
    public CustomStockItemRepository customStockItemRepository;

    @Autowired
    public StockingOrderDetailRepository stockingOrderDetailRepository;

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    /**
     * 盘点单查询页面
     * @return
     */
    @RequestMapping(value = "/stockingorder/list",  method = RequestMethod.GET)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user,HttpSession session) {
        ModelAndView mav = new ModelAndView("/stockingorder/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_STOCKINGORDER)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shops == null) {
            mav.setViewName("/logout");
            return mav;
        }
        mav.addObject("shops",shops);
        return mav;
    }

    /**
     * 获取盘点单数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param stockingOrder
     * @return
     */
    @RequestMapping(value = "/stockingorder/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, @ModelAttribute StockingOrder stockingOrder) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("DESC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Specifications specifications = Specifications.where(StockingOrderSpecifications.filterByOrderNumberView(stockingOrder.orderNumberView))
                .and(StockingOrderSpecifications.filterByShop(stockingOrder.shop))
                .and(StockingOrderSpecifications.filterByStockingDate(stockingOrder.stockingDate))
                .and(StockingOrderSpecifications.filterByStockingDateEnd(stockingOrder.stockingDate))
                .and(StockingOrderSpecifications.filterByDeleted(false));
        Page pageData = stockingOrderRepository.findAll(specifications,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }


    /**
     * 前往开始盘点页面
     * @param erpUser
     * @return
     */
    @RequestMapping(value = "/stockingorder/tostocking",method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toStocking(@AuthenticationPrincipal ERPUser erpUser,HttpSession session){
        ModelAndView mav = new ModelAndView("/stockingorder/form");
        Shop shop = (Shop)session.getAttribute("SHOP");
        StockingOrder stockingOrder = new StockingOrder();
        Long orderNum = stockingOrderRepository.findMaxOrderNum(shop);
        stockingOrder.orderNumber = orderNum;
        stockingOrder.shop = shop;
        stockingOrder.erpUser = erpUser;
        stockingOrder.orderNumberView = OrderUtil.getViewOrderNumber(erpUser.organization,shop,OrderUtil.ORDER_TYPE_STOCK,orderNum);
        stockingOrder.stockingDate = DateTime.now();
        mav.addObject("stockingOrder",stockingOrder);
        return mav;
    }

    /**
     * 显示库存查询
     * @param shopId
     * @param user
     * @return
     */
    @RequestMapping(value = "/stockingorder/list/stock",method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String,Object> stockingList(long shopId, @AuthenticationPrincipal ERPUser user){
        Shop shop = new Shop();
        shop.id = shopId;
        Date checkDate = stockingOrderRepository.findLastStockingDate(shop);
        List<CustomStockItem> customStockItems = customStockItemRepository.calForStockingOrder(shop, user.organization, checkDate);
        return JqGridDataGenerator.getNativeDataJson(customStockItems,customStockItems.size(),customStockItems.size(),1);
    }


    @RequestMapping(value = "/stockingorder/cancel", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Map<String, Object> saveDelete(Long id){
        StockingOrder stockingOrder = stockingOrderRepository.findOne(id);
        stockingOrder.deleted = true;
        stockingOrderRepository.save(stockingOrder);
        return  map(entry("msg", true));
    }



    /**
     * 创建盘点单
     * @param stockingOrder
     * @param listData
     * @param user
     * @return
     */
    @RequestMapping(value = "/stockingorder/create",method = RequestMethod.POST)
    @Transactional
    public ModelAndView create (@ModelAttribute StockingOrder stockingOrder,String listData,@AuthenticationPrincipal ERPUser user,int stockingStatus ) {
        ModelAndView mav = new ModelAndView("/message");
        if (StringUtils.hasLength(listData)) {
            ArrayList<StockingOrderDetail> stockingOrderDetails = this.getDetail(listData, "create");
            stockingOrder.stockingOrderDetails = stockingOrderDetails;
        }
        stockingOrder.stockingDate = DateTime.now();
        stockingOrder.stockingStatus = stockingStatus;
        stockingOrder = stockingOrderRepository.save(stockingOrder);
        mav.addObject("message","保存成功");
        mav.addObject("responsePage", "/stockingorder/edit?id=" + stockingOrder.id + "&doType=0");
        return mav;
    }

    /**
     * 盘点单编辑页面
     * @param stockingOrder
     * @return
     */
    @RequestMapping(value = "/stockingorder/edit",method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toEdit(@ModelAttribute StockingOrder stockingOrder,int doType){
        ModelAndView mav = new ModelAndView("/stockingorder/edit");
        stockingOrder = stockingOrderRepository.findOne(stockingOrder.id);
        if (doType == 1) mav.setViewName("/stockingorder/view");
        mav.addObject("stockingOrder",stockingOrder);
        return mav;
    }

    /**
     * 盘点信息editSave
     * @param listData
     * @param stockingOrder
     * @param stockingStatus
     * @return
     */
    @RequestMapping(value = "/stockingorder/edit/save",method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(String listData,@ModelAttribute StockingOrder stockingOrder,int stockingStatus){
        ModelAndView mav = new ModelAndView("/message");
        stockingOrder = stockingOrderRepository.findOne(stockingOrder.id);
        stockingOrder.stockingOrderDetails.clear();
        stockingOrder.stockingStatus = stockingStatus;
        if (StringUtils.hasLength(listData)) {
            ArrayList<StockingOrderDetail> stockingOrderDetails = this.getDetail(listData, "update");
            StockingOrderDetail upDetail = null;
            for (StockingOrderDetail stockingOrderDetail:stockingOrderDetails){
                upDetail = stockingOrderDetailRepository.findOne(stockingOrderDetail.id);
                upDetail.calculateNumber = stockingOrderDetail.calculateNumber;
//                upDetail = stockingOrderDetailRepository.save(upDetail);
                stockingOrder.stockingOrderDetails.add(upDetail);
            }
        }

        stockingOrder = stockingOrderRepository.save(stockingOrder);

        mav.addObject("message","保存成功");
        mav.addObject("responsePage", "/stockingorder/edit?id=" + stockingOrder.id + "&doType=0");
        return mav;
    }
    /**
     * 编辑页面Details显示
     * @param orderId
     * @return
     */

    @RequestMapping(value = "/stockingorder/details",method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String,Object> getStockingOrderDetails(long orderId){
        StockingOrder stockingOrder = stockingOrderRepository.findOne(orderId);
        List<StockingOrderDetail> stockingOrderDetails = stockingOrder.stockingOrderDetails;
        if (stockingOrderDetails != null) {
            return JqGridDataGenerator.getNativeDataJson(stockingOrderDetails,stockingOrderDetails.size(),stockingOrderDetails.size(),1);
        } else {
            return null;
        }
    }

    private ArrayList<StockingOrderDetail> getDetail(String listData,String doType) {
        ArrayList<StockingOrderDetail> result = new ArrayList<StockingOrderDetail>();
        String[] listDatas = listData.split(";");
        StockingOrderDetail stockingOrderDetail;
        CustomStockItem customStockItem;
        for (String data : listDatas) {
            stockingOrderDetail = new StockingOrderDetail();
            customStockItem = new CustomStockItem();
            String [] d = data.split(",");
            customStockItem = new CustomStockItem();
            if (doType.equals("create")) {
                customStockItem.id = Long.parseLong(d[0]);
                stockingOrderDetail.customStockItem = customStockItem;
                stockingOrderDetail.stockCost = Double.parseDouble(d[1]);
                stockingOrderDetail.calculateNumber = Integer.parseInt(d[2]);
                stockingOrderDetail.oldNumber = Integer.parseInt(d[3]);
            }
            if (doType.equals("update")){
                stockingOrderDetail.id = Integer.parseInt(d[0]);
                stockingOrderDetail.calculateNumber = Integer.parseInt(d[1]);
            }
            result.add(stockingOrderDetail);
        }
        return  result;
    }


    /**
     * 下载
     *
     * @param res
     * @param orderId
     * @para m user
     * @throws IOException
     */
    @RequestMapping(value = "/stockingorder/excel/export", method = RequestMethod.GET)
    public void download(HttpServletResponse res, long orderId, @AuthenticationPrincipal ERPUser user, String type, Long shopId) throws IOException {
        List<ExcelExportStockModel> list = new ArrayList<>();
        if (orderId != 0) {
            StockingOrder stockingOrder = stockingOrderRepository.findOne(orderId);
            List<StockingOrderDetail> stockingOrderDetails = stockingOrder.stockingOrderDetails;

            BigDecimal sum = new BigDecimal("0");
            BigDecimal beforeSum = new BigDecimal("0");

            for (StockingOrderDetail stockingOrderDetail : stockingOrderDetails) {
                ExcelExportStockModel excelExportUserModel = new ExcelExportStockModel();
                excelExportUserModel.name = stockingOrderDetail.customStockItem.name;
                excelExportUserModel.brandName = stockingOrderDetail.customStockItem.brandName;
                excelExportUserModel.barCode = stockingOrderDetail.customStockItem.barCode;
                int rootCategoryNum = stockingOrderDetail.customStockItem.rootCategory;
                excelExportUserModel.rootCategory = getRootCategoryName(rootCategoryNum);
                excelExportUserModel.stockCost = stockingOrderDetail.stockCost;
                excelExportUserModel.oldNumber = stockingOrderDetail.oldNumber;

                if ( type.equals("view")) {
                    excelExportUserModel.calculateNumber = stockingOrderDetail.calculateNumber;
                    excelExportUserModel.afterNumber = (new BigDecimal(stockingOrderDetail.stockCost + "").multiply(new BigDecimal(stockingOrderDetail.calculateNumber + ""))).doubleValue();

                    beforeSum = beforeSum.add(new BigDecimal(stockingOrderDetail.stockCost + "").multiply(new BigDecimal(stockingOrderDetail.oldNumber + "")));
                    sum = sum.add(new BigDecimal(stockingOrderDetail.stockCost + "").multiply(new BigDecimal(stockingOrderDetail.calculateNumber + "")));
                }
                list.add(excelExportUserModel);
            }

            if ( type.equals("view")) {
                ExcelExportStockModel excelExportStockModel = new ExcelExportStockModel();
                excelExportStockModel.name = "盘前总价：";
                excelExportStockModel.brandName = beforeSum.toString() + "元";
                excelExportStockModel.barCode = "盘后总价：";
                excelExportStockModel.rootCategory = sum.toString() + "元";
                excelExportStockModel.stockCost = null;
                excelExportStockModel.oldNumber = null;
                excelExportStockModel.calculateNumber = null;
                list.add(excelExportStockModel);
            }
        } else {
            Shop shop = new Shop();
            shop.id = shopId;
            Date checkDate = stockingOrderRepository.findLastStockingDate(shop);
            List<CustomStockItem> customStockItems = customStockItemRepository.calForStockingOrder(shop, user.organization, checkDate);

            for (CustomStockItem customStockItem : customStockItems) {
                ExcelExportStockModel excelExportUserModel = new ExcelExportStockModel();
                excelExportUserModel.name = customStockItem.name;
                excelExportUserModel.brandName = customStockItem.brandName;
                excelExportUserModel.barCode = customStockItem.barCode;
                int rootCategoryNum = customStockItem.rootCategory;
                excelExportUserModel.rootCategory = getRootCategoryName(rootCategoryNum);
                excelExportUserModel.stockCost = customStockItem.cost;
                excelExportUserModel.oldNumber = customStockItem.number;

                list.add(excelExportUserModel);
            }
        }

        OutputStream os = res.getOutputStream();

        Calendar cal = Calendar.getInstance();

        try {
            res.reset();
            res.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode("盘点数据信息", "UTF8")
                    + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + ".xls");
            res.setContentType("application/octet-stream; charset=utf-8");

            ExcelUtil<ExcelExportStockModel> util = new ExcelUtil<ExcelExportStockModel>(ExcelExportStockModel.class);// 创建工具类.
            util.exportExcel(list, "盘点信息", 65536, os);// 导出

        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    private String getRootCategoryName (int rootCategoryNum) {
        String rootCategoryName = "";
        switch (rootCategoryNum) {
            case 0:
                rootCategoryName = "临时分类";
                break;
            case 1:
                rootCategoryName = "机油";
                break;
            case 2:
                rootCategoryName = "机滤";
                break;
            case 3:
                rootCategoryName = "轮胎";
                break;
            case 4:
                rootCategoryName = "电瓶";
                break;
            case 5:
                rootCategoryName = "电子类产品";
                break;
            case 6:
                rootCategoryName = "美容类产品";
                break;
            case 7:
                rootCategoryName = "汽车用品";
                break;
            case 8:
                rootCategoryName = "养护产品";
                break;
            case 9:
                rootCategoryName = "耗材类产品";
                break;
            case 10:
                rootCategoryName = "灯具类产品";
                break;
            case 11:
                rootCategoryName = "雨刮类产品";
                break;
            case 12:
                rootCategoryName = "发动机配件类";
                break;
            case 13:
                rootCategoryName = "底盘配件类";
                break;
            case 14:
                rootCategoryName = "变速箱类";
                break;
            case 15:
                rootCategoryName = "电气类";
                break;
            case 16:
                rootCategoryName = "车身覆盖类";
                break;
            case 17:
                rootCategoryName = "服务类";
                break;
        }
        return  rootCategoryName;
    }

}
