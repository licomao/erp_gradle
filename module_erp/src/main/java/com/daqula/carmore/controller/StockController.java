package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.order.PurchaseOrderDetail;
import com.daqula.carmore.model.shop.CustomStockItem;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.CustomStockItemRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.StockingOrderRepository;
import com.daqula.carmore.util.DateUtil;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.SessionUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.JsonResultBuilder.buildSuccessResult;

/**
 * 库存查询
 * Created by mdc on 2015/9/21.
 */
@Controller
public class StockController {

    @Autowired
    public StockingOrderRepository stockingOrderRepository;

    @Autowired
    public CustomStockItemRepository customStockItemRepository;

    @Autowired
    public ShopRepository shopRepository;

    @Autowired
    public OrganizationRepository organizationRepository;

    /**
     * TO库存查询
     * @return
     */
    @RequestMapping(value = "/stock/list" ,method = RequestMethod.GET)
    public ModelAndView list (HttpSession session, @AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/stock/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_STOCK)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        mav.addObject("secondaryCategory",new SecondaryCategory());
        List<Shop> shops = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shops == null) {
            mav.setViewName("/logout");
            return mav;
        }
        mav.addObject("orgId",user.organization.id);
        mav.addObject("shops",shops);
        return mav;
    }


    /**
     * 库存查询
     * @param page
     * @param rows
     * @param customStockItem
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/stock/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows ,
                                        CustomStockItem customStockItem,long shopId, @AuthenticationPrincipal ERPUser user) {
        Shop shop = new Shop();
        shop.id = shopId;
        Date orderDate = stockingOrderRepository.findLastStockingDate(shop);
        int totalCount = customStockItemRepository.calCustomStockInfoCounts(shop, user.organization, customStockItem, orderDate);
        List<CustomStockItem> customStockItems = customStockItemRepository.calCustomStockInfo(page, rows, shop, user.organization, customStockItem, orderDate);
        return JqGridDataGenerator.getNativeDataJson(customStockItems,totalCount,rows,page);
    }
}
