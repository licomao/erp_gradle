package com.daqula.carmore.util;

import com.daqula.carmore.model.admin.BaseSet;
import com.daqula.carmore.model.order.StockingOrder;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.BaseSetRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.StockingOrderRepository;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxin on 2015/10/10 0010.
 */
public class SessionUtil {

    /**
     * 取出session中的shop,如果是分店就返回，是总店就取出组织下的所有shop
     * @param session
     * @param organizationRepository
     * @param shopRepository
     * @return List<Shop>
     */
    public static List<Shop> getShopList (HttpSession session , OrganizationRepository organizationRepository , ShopRepository shopRepository) {
        List<Shop> shops = new ArrayList<Shop>();
        Shop shop = (Shop) session.getAttribute("SHOP");
        Organization organization = (Organization) session.getAttribute("ORGANIZATIONS");

        if (shop == null || organization == null) {
            return null;
        }
        Long organizationId = organization.id;
        if (shop.shopType == 0){
            shops = shopRepository.findByOrganization(organizationRepository.findOne(organizationId));
        }else {
            shops.add(shop);
        }
        return shops;
    }

    /***
     * 盘点验证
     * @param session
     * @param baseSetRepository
     * @param stockingOrderRepository
     * @return
     */
    public static void checkPd(HttpSession session,Shop shop, BaseSetRepository baseSetRepository, StockingOrderRepository stockingOrderRepository) {
        boolean checkPass = false;
        if (shop != null) {
            BaseSet baseSet = baseSetRepository.findByShop(shop);
            if (baseSet != null) {
                if(baseSet.isCheckPd) {
                    DateTime now = DateTime.now();
                    String startDateStr = now.getYear() + "-" + now.getMonthOfYear() + "-" + baseSet.checkDay;

                    DateTime startDate = new DateTime(startDateStr);
                    startDate = startDate.minusMonths(1);
                    Period p = new Period(startDate,now, PeriodType.days());
                    int day = p.getDays();
                    if(day < 30){
                        startDate = startDate.minusMonths(1);
                    }
                    List<StockingOrder> stockingOrders = stockingOrderRepository.findOrderCal(startDate.minusMonths(1),now,shop);
                    if (stockingOrders.size() == 0){
                        checkPass = true;
                    }
                }
            }
        }
        session.setAttribute("pdCheck",checkPass);
    }

}
