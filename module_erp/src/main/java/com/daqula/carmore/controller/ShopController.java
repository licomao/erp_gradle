package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.template.City;
import com.daqula.carmore.repository.CityRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.specification.ShopRepositorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.StringUtil;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * Created by mdc on 2015/7/28.
 */
@Controller
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CityRepository cityRepository;

    /**
     * Register binding validators
     * @param binder
     */
    @InitBinder("shop")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new ShopValidator());
    }
    /**
     * 返回门店列表
     * @return
     */
    @RequestMapping(value = "/shop/list",method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/shop/list");
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_SHOPMANAGE)) {
            mav.setViewName("/noauthority");
            return mav;
        }
        Organization organization = organizationRepository.findOne(user.organization.id);
        mav.addObject("shop",new Shop());
        mav.addObject("organization",organization);

        return mav;
    }

    /**
     * toShopCreate
     * @return
     */
    @RequestMapping(value = "/shop/new", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView create(@AuthenticationPrincipal ERPUser erpUser,@RequestParam(required = false) Long id) {
        ModelAndView mav = new ModelAndView("/shop/form");
        if(id != null && id >= 0) {
            Shop shop = shopRepository.findOne(id);
            mav.addObject("pageContent", "更新");
            mav.addObject("shop", shop);
        } else {
            Shop shop =  new Shop();
            shop.organization = erpUser.organization;
            mav.addObject("shop", shop);
            mav.addObject("pageContent", "新增");
        }

        List<City> citys = (List<City>) cityRepository.findAll();
        mav.addObject("citys", cityRepository.findAll());
        return mav;
    }

    /**
     * toShopDelete
     * @return
     */
    @RequestMapping(value = "/shop/delete", method = RequestMethod.GET)
    @Transactional
    public ModelAndView delete(@RequestParam(required = false) Long id) {
        ModelAndView mav = new ModelAndView();

        if(id != null && id >= 0) {
            shopRepository.delete(id);
        }

        mav.setViewName("/message");
        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/shop/list");
        return mav;
    }

    /**
     * 保存门店信息
     * @param shop
     * @return
     */
    @RequestMapping(value = "/shop/new",method = RequestMethod.POST)
    @Transactional
    public ModelAndView saveShop(@Valid @ModelAttribute Shop shop, BindingResult bindingResult) {
        ModelAndView mav =  new ModelAndView("/message");
        if (bindingResult.hasErrors()) {
            mav.setViewName("/shop/form");
            List<City> citys = (List<City>) cityRepository.findAll();
            mav.addObject("citys", cityRepository.findAll());
            return mav;
        }
        boolean toSave = true;
        shop.organization = organizationRepository.findOne(shop.organization.id);
        if (shop.id == 0){
            List<Shop> shops = shopRepository.findByOrganization(shop.organization);
            if (shops.size() >= shop.organization.shopQuota) {
                toSave = false;
                mav.addObject("message","已到达添加门店的上线数量");
            }
        }

        if (toSave) {
            Shop resultShop = shopRepository.save(shop);
            if (resultShop == null) {
                mav.addObject("message","保存失败");
            } else {
                mav.addObject("message","保存成功");
            }
        }
        mav.addObject("responsePage","/shop/list");
        return mav;
    }



    /**
     * 判断门店代码是否重复
     *  @param shopcode 门店代码
     * @return
     */
    @RequestMapping("/shop/isCodeRepeat")
    public @ResponseBody Map<String, Object>  isCodeRepeat(@RequestParam String shopcode , @RequestParam(required = false) long id , long orgId) {
        return  map(entry("data", shopRepository.isCodeCanUse(shopcode, id, orgId)));
    }

    /**
     * 门店信息查询异步加载
     *  @param orgId 所属组织
     *  @param name
     *  @return
     */
    @RequestMapping(value = "/shop/list/data", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public @ResponseBody Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                      @RequestParam String sord, @RequestParam String sidx,long orgId,
                                                      @RequestParam String name) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(orgId);
        Specification<Shop> shopSpecification = Specifications.where(ShopRepositorySpecifications.filteredByOrganization(organization))
                .and(ShopRepositorySpecifications.filteredByName(name));
        Page pageData = shopRepository.findAll(shopSpecification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }


    /**
     * 参数合法性验证
     */
    static class ShopValidator implements Validator{
        /**
         * This Validator validates *just* Shop instances
         */
        @Override
        public boolean supports(Class<?> clazz) {
            return Shop.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            Shop shop = (Shop)obj;
            String name = shop.name;
            // name validation
            if (!StringUtils.hasLength(name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
            String openingHours = shop.openingHours;
            if (!StringUtils.hasLength(openingHours)) {
                errors.rejectValue("openingHours", "required", "不能为空");
            }
            String address = shop.address;
            if (!StringUtils.hasLength(address)) {
                errors.rejectValue("address", "required", "不能为空");
            }
            String phone = shop.phone;
            if (!StringUtils.hasLength(phone)) {
                errors.rejectValue("phone", "required", "不能为空");
            }
            String shopCode = shop.shopCode;
            if (!StringUtils.hasLength(shopCode)) {
                errors.rejectValue("shopCode", "required", "不能为空");
            }
        }
    }

}
