package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.util.JqGridDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.*;

/**
 * 账号Controller
 */
@Controller
public class AccountController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private ERPRoleRepository erpRoleRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ShopRepository shopRepository;

    /**
     * Register binding validators
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        AccountValidator accountValidator = new AccountValidator();
        binder.addValidators(accountValidator);
    }

    /**
     * 获取账号列表
     * @return
     */
    @RequestMapping("/account/list")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView list() {

        String name  = request.getSession().getAttribute("USER_NAME").toString();
        ERPUser userInfo = erpUserRepository.findByUsername(name);
        List<Shop> shopList =  (List<Shop>)request.getSession().getAttribute("SHOPS");
        return  new ModelAndView("/account/list", map(entry("user", userInfo),entry("shopList", shopList)));
    }

    /**
     * 创建或者更新账户信息
     * @param id 账户id
     * @return
     */
    @RequestMapping("/account/tosave")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView toSave(@RequestParam(required = false) Long id, @AuthenticationPrincipal ERPUser sessionUser) {
        ModelAndView mav = new ModelAndView("/account/form");
        List<ERPRole> roleList = (List<ERPRole>) erpRoleRepository.findByOrganization(sessionUser.organization);

        mav.addObject("roleList", roleList);

        if(id != null && id > 0) {
            ERPUser erpUser = erpUserRepository.findOne(id);
            mav.addObject("erpUser", erpUser);
        } else {
            ERPUser user = new ERPUser();
            Organization organization = (Organization) request.getSession().getAttribute("ORGANIZATIONS");
            user.organization = organizationRepository.findOne(organization.id);
            user.shops = new ArrayList<>();
            mav.addObject("erpUser", user);
        }
        return mav;
    }

    /**
     * 获取门店信息
     * @param organization 组织id
     * @return
     */
    @RequestMapping("/account/getshops")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> getShopList(Long organization, Long userid) {
        List<Shop> shopList = shopRepository.findByOrganization(organizationRepository.findOne(organization));
        List<Integer> checkBoxStatus = new ArrayList<>();

        List<Shop> shopsList= new ArrayList<>();

        if(!StringUtils.isEmpty(userid) && userid > 0) {
            shopsList= erpUserRepository.findOne(userid).shops;
        }

        for(int i = 0; i < shopList.size(); i++) {
            Shop shop = shopList.get(i);
            if (shopsList.contains(shop)) {
                checkBoxStatus.add(1);
            } else {
                checkBoxStatus.add(0);
            }
        }
        return map(entry("shopList",shopList),entry("checkBoxStatus",checkBoxStatus));
    }

    /**
     * 删除制定账户
     * @param id 账户id
     * @return
     */
    @RequestMapping("/account/delete")
    @Transactional
    public String delete(@RequestParam Long id) {
        ERPUser erpUser = erpUserRepository.findOne(id);
        erpUser.deleted = true;
        erpUserRepository.save(erpUser);
        return "redirect:/account/list";
    }

    /**
     * 保存账户信息
     * @param user 界面获取的账户数据
     * @param shopchk 门店checkbox的值
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/account/save", method = RequestMethod.POST)
    @Transactional
    public String save(@Valid @ModelAttribute ERPUser user,String shopchk, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/account/form";
        }
        user.organization = organizationRepository.findOne(user.organization.id);

        List<Long> shopsId = new ArrayList<>();
        for(String shop :shopchk.split(",")) {
            shopsId.add(Long.parseLong(shop));
        }
        user.shops = (List<Shop>) shopRepository.findAll(shopsId);

        erpUserRepository.save(user);
        return "redirect:/account/list";
    }

    /**
     * 显示account list
     * @param accountName 查询的账户名
     * @param organization 组织
     * @param shop 门店
     * @return
     */
    @RequestMapping(value = "/account/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> getAccountManagerList(@RequestParam int page, @RequestParam int rows,
                                                     @RequestParam String sord, @RequestParam String sidx,
                                                     @RequestParam String accountName, @RequestParam Long organization, @RequestParam Long shop) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Shop> shopList = new ArrayList<>();

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        shopList.add(shopRepository.findOne(shop));

        if(StringUtils.isEmpty(accountName)) {
            accountName = "%%";
        }
        Page<ERPUser> userList = accountRepository.findByUsernameLikeAndOrganizationAndShopsAndDeleted(accountName, organizationRepository.findOne(organization), shopList, false, pageRequest);

        for(ERPUser user : userList) {
            user.showedDate = user.createdDate == null ? "" : user.createdDate.toString();
        }

        return JqGridDataGenerator.getDataJson(userList);
    }

    static class AccountValidator implements Validator {
        /**
         * This Validator validates *just* StockItem instances
         */
        @Override
        public boolean supports(Class<?> clazz) {
            return ERPUser.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            ERPUser erpUser = (ERPUser)obj;

            if (!StringUtils.hasLength(erpUser.username)) {
                errors.rejectValue("username", "required", "不能为空");
            }

            if (!StringUtils.hasLength(erpUser.password)) {
                errors.rejectValue("password", "required", "不能为空");
            }
        }
    }
}
