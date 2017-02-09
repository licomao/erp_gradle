package com.daqula.carmore.controller;

import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
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

import static com.daqula.carmore.util.CollectionUtil.*;

/**
 * Created by mdc on 2015/7/28.
 */
@Controller
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * Register binding validators
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new ShopValidator());
    }

    /**
     * toShopCreate
     * @return
     */
    @RequestMapping(value = "/shop/new", method = RequestMethod.GET)
    public ModelAndView create(@RequestParam(required = false) Long id) {

        if(id != null && id >= 0) {
            Shop shop = shopRepository.findOne(id);
            ModelAndView model = new ModelAndView("/shop/form", map(entry("shop", shop)));
            model.addObject("pageContent", "更新");
            return model;
        }
        ModelAndView model = new ModelAndView("/shop/form", map(entry("shop", new Shop())));
        model.addObject("pageContent", "新增");

        return model;
    }

    /**
     * toShopDelete
     * @return
     */
    @RequestMapping(value = "/shop/delete", method = RequestMethod.GET)
    public String delete(@RequestParam(required = false) Long id) {

        if(id != null && id >= 0) {
            shopRepository.delete(id);
        }

        return "success";
    }

    /**
     * 保存门店信息
     * @param shop
     * @return
     */
    @RequestMapping(value = "/shop/new",method = RequestMethod.POST)
    public ModelAndView saveShop(@Valid @ModelAttribute Shop shop, BindingResult bindingResult) {

        ModelAndView mav =  new ModelAndView("redirect:/shop/list");
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/shop/form");
        }
        shop.organization = organizationRepository.findOne(shop.organization.id);
        Shop resultShop = shopRepository.save(shop);

        return mav;
    }

    /**
     * 返回门店列表
     * @return
     */
    @RequestMapping("/shop/list")
    public String list() {
        return "/shop/list";
    }


    /**
     * 门店信息查询异步加载
     *  @param orgName 所属组织
     *  @param orgName 所属组织
     *  @return
     */
    @RequestMapping("/shop/list/data")
    public @ResponseBody Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                      @RequestParam String sord, @RequestParam String sidx,
                                                      @RequestParam(required = false) String orgName,
                                                      @RequestParam(required = false) String shopName) {
        if(StringUtil.IsNullOrEmpty(orgName) && StringUtil.IsNullOrEmpty(shopName)) {
            PageRequest pageRequest = new PageRequest(page-1, rows,
                    sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                    !StringUtils.isEmpty(sidx) ? sidx : "id");
            Page pageData = shopRepository.findAll(pageRequest);
            return JqGridDataGenerator.getDataJson(pageData);
        } else {
            if(StringUtil.IsNullOrEmpty(orgName)) {
                orgName = "";
            }
            if(StringUtil.IsNullOrEmpty(shopName))  shopName = "";
            List<Shop> rst = shopRepository.findShopByNameAndOrgLike(orgName,shopName,page,rows, sord);
            int total = rst.size()/rows ;
            if(rst.size() < rows || total * rows < rst.size()) {
                total = total + 1;
            }

            return map(
                    entry("total", total),
                    entry("page", page),
                    entry("records", rst.size()),
                    entry("rows", rst)
            );
        }
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
