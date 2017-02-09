package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Agency;
import com.daqula.carmore.model.admin.SkuItem;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.shop.*;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.repository.specification.CustomStockItemRepositorySpecifications;
import com.daqula.carmore.repository.specification.SaleShelfSpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxin on 2015/11/2 0002.
 */
@Controller
public class SaleShelfController {

    @Autowired
    private SaleShelfRepository saleShelfRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CustomStockItemRepository customStockItemRepository;

    @Autowired
    private CustomSuiteRepository customSuiteRepository;

    @Autowired
    private SkuItemRepository skuItemRepository;


    @Autowired
    private AgencyRepository agencyRepository;

    @Autowired
    private ShopRepository shopRepository;

    /**
     * TO APP上架管理查询
     * @return
     */
    @RequestMapping("/saleshelf/list")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav= new ModelAndView("/saleshelf/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_EXPENSE)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        mav.addObject("orgId", user.organization.id);
        return mav;
    }

    /**
     *获取APP上架管理数据
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param saleCategory 上架分类
     * @param orgId 组织id
     * @return
     */
    @RequestMapping(value = "/saleshelf/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, long orgId, Integer saleCategory) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(orgId);

        Specification<SaleShelf> specification = Specifications.where(SaleShelfSpecifications.filterBySaleCategory(saleCategory))
                .and(SaleShelfSpecifications.filterByOrganization(organization));
        Page pageData = saleShelfRepository.findAll(specification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 进入APP上架信息页面
     * @param id 信息的id
     * @param doType 类型，新增=0，修改=1，查看=2
     * @return
     */
    @RequestMapping(value = "/saleshelf/form", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView form(String id, @AuthenticationPrincipal ERPUser user, Integer doType){
        ModelAndView mav = new ModelAndView("/saleshelf/form");
        Agency agency = agencyRepository.findByErpUser(user);   //获取代理商信息；
        List<Organization> organizations = organizationRepository.findByAgency(agency);  //获取组织信息
        SaleShelf saleShelf = new SaleShelf();
        mav.addObject("doType", doType);
        mav.addObject("saleShelf", saleShelf);
        mav.addObject("organizations", organizations);
        return mav;
    }

    /**
     * 获取商品信息列表
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param orgId
     * @return
     */
    @RequestMapping(value = "/saleshelf/list/form/skuitem", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listSkuData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, long orgId,String skuName,Integer rootCategory) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(orgId);
        Specification<CustomStockItem> specification = Specifications.where(CustomStockItemRepositorySpecifications.filterByName(skuName))
                .and(CustomStockItemRepositorySpecifications.filterByOrganization(organization))
                .and(CustomStockItemRepositorySpecifications.filterByRootCategory(rootCategory))
                .and(CustomStockItemRepositorySpecifications.filterDeleted(false));
        Page pageData = customStockItemRepository.findAll(specification, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    @RequestMapping(value = "/saleshelf/list/form/shops", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, List<Shop>> listShops(long orgId) {
        Map<String,List<Shop>> shops = new HashMap<String,List<Shop>>();
        Organization organization = organizationRepository.findOne(orgId);
        List<Shop> shopList =  shopRepository.findByOrganization(organization);
        shops.put("shopList",shopList);
        return shops;
    }

    /**
     * 保存APP上架信息
     * @param saleShelf
     * @return
     */
    @RequestMapping(value = "/saleshelf/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save( @ModelAttribute SaleShelf saleShelf, String skuItemId, String suiteId){
        ModelAndView mav = new ModelAndView("/message");
        String message="保存成功";
        SkuItem skuItem = null;
        Suite suite = null;
        if (skuItemId != null) {
            skuItem = customStockItemRepository.findOne(Long.parseLong(skuItemId));
        }
        if (suiteId != null) {
            suite = customSuiteRepository.findOne(Long.parseLong(suiteId));
        }

        if (saleShelf.id != 0){
            SaleShelf updateStaff = saleShelfRepository.findOne(saleShelf.id);
            Organization organization = organizationRepository.findOne(saleShelf.organization.id);
            updateStaff.saleCategory = saleShelf.saleCategory;
            updateStaff.organization = saleShelf.organization;
            updateStaff.price = saleShelf.price;
            updateStaff.skuItem = skuItem;
            updateStaff.suite = suite;

            saleShelfRepository.save(updateStaff);
        } else {
            saleShelf.skuItem = skuItem;
            saleShelf.suite = suite;
            saleShelfRepository.save(saleShelf);
        }

        mav.addObject("message",message);
        mav.addObject("responsePage","/saleshelf/list");
        return mav;
    }

    /**
     * 删除APP上架信息
     * @param id saleshelf的id
     * @return
     */
    @RequestMapping(value = "/saleshelf/delete", method = RequestMethod.GET)
    @Transactional
    public ModelAndView delete(String id) {
        ModelAndView mav = new ModelAndView();

        SaleShelf saleShelf = saleShelfRepository.findOne(Long.parseLong(id));
        saleShelf.deleted = !saleShelf.deleted;

        mav.setViewName("/message");
        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/saleshelf/list");
        return mav;
    }


}
