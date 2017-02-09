package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.order.StockTransferOrder;
import com.daqula.carmore.model.order.StockTransferOrderDetail;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.StockTransferOrderSpecifications;
import com.daqula.carmore.util.*;
import org.joda.time.DateTime;
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
import java.util.*;

/**
 * 库存调拨
 * Created by mdc on 2015/10/2.
 */
@Controller
public class StockTransferOrderController {

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    @Autowired
    public StockTransferOrderRepository stockTransferOrderRepository;

    @Autowired
    public StockingOrderRepository stockingOrderRepository;

    @Autowired
    public CustomStockItemRepository customStockItemRepository;

    /**
     * 查询库存调拨List页面
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "/stocktransferorder/list",method = RequestMethod.GET)
    public ModelAndView list (@AuthenticationPrincipal ERPUser user,HttpSession session) {
        ModelAndView mav = new ModelAndView("/stocktransferorder/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_STOCKTRANSFERORDER)) {
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
     * 获取调拨单据数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param stockTransferOrder
     * @return
     */
    @RequestMapping(value = "/stocktransferorder/list/data" ,method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String,Object> listDate(@RequestParam int page, @RequestParam int rows,
                                       @RequestParam String sord, @RequestParam String sidx,@ModelAttribute StockTransferOrder stockTransferOrder){
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Specification<StockTransferOrder> specification = Specifications.where(StockTransferOrderSpecifications.filterByShop(stockTransferOrder.inShop))
                .and(StockTransferOrderSpecifications.filterByOrderNumberView(stockTransferOrder.orderNumberView))
                .and(StockTransferOrderSpecifications.filterByDeleted(stockTransferOrder.deleted))
                .and(StockTransferOrderSpecifications.filterByTransferDateStart(stockTransferOrder.transferDate))
                .and(StockTransferOrderSpecifications.filterByTransferDateEnd(stockTransferOrder.transferDate));
        Page pageData = stockTransferOrderRepository.findAll(specification, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }


    @RequestMapping(value = "/stocktransferorder/form" ,method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toTransfer(@AuthenticationPrincipal ERPUser user,HttpSession session) {
        ModelAndView mav = new ModelAndView("/stocktransferorder/form");
        Shop shop = (Shop) session.getAttribute("SHOP");
        StockTransferOrder stockTransferOrder = new StockTransferOrder();
        stockTransferOrder.inShop = shop;
        Organization organization = organizationRepository.findOne(user.organization.id);
        List<Shop> shopList = shopRepository.findByOrganization(organization);
        Long newOrderNumber = stockTransferOrderRepository.findMaxOrderNum(shop);
        stockTransferOrder.orderNumber = newOrderNumber;
        stockTransferOrder.orderNumberView = OrderUtil.getViewOrderNumber(organization, shop ,OrderUtil.ORDER_TYPE_TRANSFER,newOrderNumber);
        stockTransferOrder.erpUser = user;
        mav.addObject("outShops", shopList);
        mav.addObject("stockTransferOrder",stockTransferOrder);
        return mav;
    }

    /**
     * insertTransferOrder
     * @param stockTransferOrder
     * @param listData
     * @param erpUser
     * @return
     */
    @RequestMapping(value = "/stocktransferorder/save" ,method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@ModelAttribute StockTransferOrder stockTransferOrder, String listData,@AuthenticationPrincipal ERPUser erpUser) {
        ModelAndView mav = new ModelAndView("/message");
        if (StringUtils.hasLength(listData)) {
            stockTransferOrder.stockTransferOrderDetails = this.getDetailInfo(listData);
        }
        stockTransferOrder.transferDate = DateTime.now();
        stockTransferOrder.transferStatus = 0;
        stockTransferOrder.erpUser = erpUser;
        stockTransferOrderRepository.save(stockTransferOrder);
        mav.addObject("message","保存成功");
        mav.addObject("responsePage", "/stocktransferorder/list");
        return mav;
    }


    /**
     * 前往调拨单编辑页面
     * @param id
     * @param doType
     * @return
     */
    @RequestMapping(value = "/stocktransferorder/edit", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView edit(long id,int doType, @AuthenticationPrincipal ERPUser user){
        ModelAndView mav = new ModelAndView("/stocktransferorder/edit");
        if(id > 0) {
            StockTransferOrder stockTransferOrder = stockTransferOrderRepository.findOne(id);
            mav.addObject("stockTransferOrder",stockTransferOrder);
        }
        if (doType > 0){
            mav.setViewName("/stocktransferorder/view");
            mav.addObject("doType",doType);
        }
        Organization organization = organizationRepository.findOne(user.organization.id);
        List<Shop> shopList = shopRepository.findByOrganization(organization);
        mav.addObject("outShops",shopList);
        return  mav;
    }

    /**
     * 编辑
     * @param stockTransferOrder
     * @param listData
     * @param erpUser
     * @return
     */
    @RequestMapping(value = "/stocktransferorder/editsave", method = RequestMethod.POST)
    @Transactional
    public  ModelAndView editSave(@ModelAttribute StockTransferOrder stockTransferOrder,String listData,@AuthenticationPrincipal ERPUser erpUser){
        ModelAndView mav = new ModelAndView("/message");
        StockTransferOrder stockTransferOrderUpdate = stockTransferOrderRepository.findOne(stockTransferOrder.id);
        if (StringUtils.hasLength(listData)) {
            stockTransferOrderUpdate.stockTransferOrderDetails.clear();
            stockTransferOrderUpdate.stockTransferOrderDetails.addAll(this.getDetailInfo(listData));
        }
        stockTransferOrderUpdate.outShop = stockTransferOrder.outShop;
        stockTransferOrderUpdate.remark = stockTransferOrder.remark;
        stockTransferOrderUpdate.transferStatus = 0;
        stockTransferOrderUpdate.erpUser = erpUser;
        stockTransferOrderRepository.save(stockTransferOrderUpdate);
        mav.addObject("message","保存成功");
        mav.addObject("responsePage", "/stocktransferorder/list");
        return mav;
    }

    /**
     * 调拨单作废
     * @param id
     * @param type
     * @return
     */
    @RequestMapping(value = "/stocktransferorder/deleted", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public String deletedOrder(long id,int type) {
        StockTransferOrder stockTransferOrder = stockTransferOrderRepository.findOne(id);
        boolean flag = type == 1? true : false;
        stockTransferOrder.deleted = flag;
        stockTransferOrderRepository.save(stockTransferOrder);
        return "success";
    }

    /***
     * 调拨单审批及入库
     * @param stockTransferOrder
     * @param doType
     * @return
     */
    @RequestMapping(value = "/stocktransferorder/approve",method = RequestMethod.POST)
    @Transactional
    public ModelAndView approve(@ModelAttribute StockTransferOrder stockTransferOrder,int doType){
        ModelAndView mav = new ModelAndView("message");
        StockTransferOrder stockTransferOrderApprove = stockTransferOrderRepository.findOne(stockTransferOrder.id);
        stockTransferOrderApprove.transferStatus = doType;
        if(doType == 3) {
            stockTransferOrderApprove.stockDate = DateTime.now();
        }
        stockTransferOrderRepository.save(stockTransferOrderApprove);
        mav.addObject("message","操作成功");
        mav.addObject("responsePage", "/stocktransferorder/list");
        return mav;
    }



    /**
     * format details
     * @param rowDatas
     * @return
     */
    public ArrayList<StockTransferOrderDetail> getDetailInfo(String rowDatas) {
        ArrayList<StockTransferOrderDetail> stockTransferOrderDetails = new ArrayList<StockTransferOrderDetail>();
        String[] rowData = rowDatas.split(";");
        StockTransferOrderDetail stockTransferOrderDetail;
        CustomStockItem customStockItem;
        for (String data : rowData) {
            String[] datas = data.split(",");
            if (datas.length > 1) {
                stockTransferOrderDetail = new StockTransferOrderDetail();
                customStockItem = new CustomStockItem();
                customStockItem.id = Long.parseLong(datas[0]);
                stockTransferOrderDetail.customStockItem = customStockItem;
                stockTransferOrderDetail.number = Integer.parseInt(datas[1]);
                stockTransferOrderDetail.cost = Double.parseDouble(datas[2]);
                stockTransferOrderDetail.beforeNumber = Integer.parseInt(datas[3]);
                stockTransferOrderDetails.add(stockTransferOrderDetail);
            }
        }
        return  stockTransferOrderDetails;
    }

}
