package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.order.MaterialOrder;
import com.daqula.carmore.model.order.MaterialOrderDetail;
import com.daqula.carmore.model.order.PurchaseOrderDetail;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.CustomStockItemRepositorySpecifications;
import com.daqula.carmore.repository.specification.MaterialOrderSpecifications;
import com.daqula.carmore.util.DateUtil;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.OrderUtil;
import com.daqula.carmore.util.SessionUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
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
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * 耗材领用Controller
 * Created by mdc on 2015/9/16.
 */
@Controller
public class MaterialOrderController {

    @Autowired
    public MaterialOrderRepository materialOrderRepository;

    @Autowired
    public StockingOrderRepository stockingOrderRepository;


    @Autowired
    public CustomStockItemRepository customStockItemRepository;

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.addValidators(new MaterialOrderValidator());
//    }

    /**
     * 领用单列表
     * @param user
     * @return
     */
    @RequestMapping(value = "/material/list",method = RequestMethod.GET)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user ,HttpSession session) {
        ModelAndView mav = new ModelAndView("/material/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_MATERIALORDER)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shops == null) {
            mav.setViewName("/logout");
            return mav;
        }
        String workDate = (DateTime.now().toString("yyyy/MM/dd"));
        mav.addObject("useDateStart",workDate);
        mav.addObject("useDateEnd", workDate);
        mav.addObject("shops",shops);
        return mav;
    }

    /**
     *获取领用单列表数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param materialOrder
     * @return
     */
    @RequestMapping(value = "/material/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,@ModelAttribute MaterialOrder materialOrder ,String useDateStart, String useDateEnd) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        DateTime startDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(useDateStart + " 00:00:00");
        DateTime endDate = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").parseDateTime(useDateEnd + " 23:59:59");
        Specification<MaterialOrder> specification = Specifications.where(MaterialOrderSpecifications.filterByCreatedDateGreater(startDate))
                .and(MaterialOrderSpecifications.filterByCreatedDateLess(endDate))
                .and(MaterialOrderSpecifications.filterByShop(materialOrder.shop))
                .and(MaterialOrderSpecifications.filterByDeleted(materialOrder.deleted))
                .and(MaterialOrderSpecifications.filterByOrderNumView(materialOrder.orderNumView));
        Page pageData = materialOrderRepository.findAll(specification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 进入领用单据页面
     * @param id
     * @param user
     * @return
     */
    @RequestMapping(value = "/material/tosave", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView toSave (String id, @AuthenticationPrincipal ERPUser user,HttpSession session) {
        ModelAndView mav = new ModelAndView("/material/form");
        String responseOption = "create";
        MaterialOrder materialOrder = new MaterialOrder();
        Shop shop = (Shop)session.getAttribute("SHOP");
        if(shop == null) {
            return new ModelAndView("/login?error");
        }
        materialOrder.shop = shop;
        if (StringUtils.hasLength(id)) {
            responseOption = "look";
            materialOrder = materialOrderRepository.findOne(Long.parseLong(id));
            mav.setViewName("/material/view");
            if (materialOrder == null) {
                mav.setViewName("/message");
                mav.addObject("message","进入页面失败");
                mav.addObject("responsePage","/material/list");
                return mav;
            }
        } else {
            materialOrder.useDate = DateUtil.formatDate(new Date());
            materialOrder.erpUser = user;
            Long newOrderNum = materialOrderRepository.findMaxOrderNum(shop);
            materialOrder.orderNumView = OrderUtil.getViewOrderNumber(user.organization, shop, OrderUtil.ORDER_TYPE_MATERIAL, newOrderNum);
            materialOrder.orderNum = newOrderNum;
        }
        mav.addObject("pageContent", responseOption);
        mav.addObject("materialOrder", materialOrder);
        return mav;
    }


    /**
     * 显示库存信息
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param customStockItem
     * @return
     */
    @RequestMapping(value = "/material/list/shopdata", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listShopData(@RequestParam int page, @RequestParam int rows,@AuthenticationPrincipal ERPUser user,
                                        @RequestParam String sord, @RequestParam String sidx,@ModelAttribute CustomStockItem customStockItem,Long shopId) {
        Shop shop = new Shop();
        shop.id = shopId;
        Date checkDate = stockingOrderRepository.findLastStockingDate(shop);
        int totalCount = customStockItemRepository.calCustomStockInfoCounts(shop, user.organization, customStockItem, checkDate);
        List<CustomStockItem> customStockItems = customStockItemRepository.calCustomStockInfo(page, rows, shop, user.organization, customStockItem, checkDate);
        return JqGridDataGenerator.getNativeDataJson(customStockItems, totalCount, rows, page);
    }

    /**
     * 保存耗材领用
     * @param rowDatas
     * @param materialOrder
     * @param user
     * @return
     */
    @RequestMapping(value = "/material/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(String rowDatas, @ModelAttribute MaterialOrder materialOrder, @AuthenticationPrincipal ERPUser user,HttpSession session){
        ModelAndView mav = new ModelAndView("/message");
        if (StringUtils.hasLength(rowDatas)) {
            materialOrder.materialOrderDetails = this.getDetailInfo(rowDatas);
        }
        materialOrder.erpUser = user;
        Shop shop = (Shop)session.getAttribute("SHOP");
        materialOrder.shop = shop;
        materialOrder.useDate = DateUtil.formatDate(new Date());
        materialOrderRepository.save(materialOrder);
        mav.addObject("message","申领成功");
        mav.addObject("responsePage","/material/list");
        return mav;
    }

    @RequestMapping(value = "/material/savedelete", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Map<String, Object> saveDelete(Long id, boolean deleted){
        MaterialOrder materialOrder = materialOrderRepository.findOne(id);
        materialOrder.deleted = deleted;
        materialOrderRepository.save(materialOrder);
        return  map(entry("msg", true));
    }



    /**
     * formatList
     * @param rowDatas
     * @return
     */
    public ArrayList<MaterialOrderDetail> getDetailInfo(String rowDatas) {
        ArrayList<MaterialOrderDetail> materialOrderDetails = new ArrayList<MaterialOrderDetail>();
        String[] rowData = rowDatas.split(";");
        MaterialOrderDetail materialOrderDetail;
        CustomStockItem customStockItem;
        for (String data : rowData) {
            String[] datas = data.split(",");
            if (datas.length > 1) {
                materialOrderDetail = new MaterialOrderDetail();
                customStockItem = new CustomStockItem();
                customStockItem.id = Long.parseLong(datas[0]);
                materialOrderDetail.customStockItem = customStockItem;
                materialOrderDetail.number = Integer.parseInt(datas[1]);
                materialOrderDetail.cost = Double.parseDouble(datas[2]);
                materialOrderDetails.add(materialOrderDetail);
            }
        }
        return  materialOrderDetails;
    }



    /**
     * valid
     */
    static class MaterialOrderValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return MaterialOrder.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            MaterialOrder materialOrder = (MaterialOrder) obj;
//            String name = fixedAsset.name;
//            if (!StringUtils.hasLength(name)) {
//                errors.rejectValue("name", "required", "不能为空");
//            }
//            String model = fixedAsset.model;
//            if (!StringUtils.hasLength(model)) {
//                errors.rejectValue("model", "required", "不能为空");
//            }
//            if (fixedAsset.price <= 0) {
//                errors.rejectValue("price", "required", "必须大于0");
//            }
        }
    }

}
