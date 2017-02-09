package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

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

//    /**
//     * Register binding validators
//     *
//     * @param binder
//     */
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        AccountValidator accountValidator = new AccountValidator();
//        binder.addValidators(accountValidator);
//    }

    /**
     * 获取账号列表
     * @return
     */
    @RequestMapping("/account/list")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user, HttpSession session) {

        ModelAndView mav = new ModelAndView("/account/list");
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_ACCOUNT)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        List<Shop> shopList = SessionUtil.getShopList(session, organizationRepository, shopRepository);

        if (shopList == null) {
            mav.setViewName("/logout");
            return mav;
        }

        mav.addObject("user", user);
        mav.addObject("shopList", shopList);
        return  mav;
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
    public ModelAndView delete(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView();

        ERPUser erpUser = erpUserRepository.findOne(id);
        erpUser.deleted = true;
        erpUserRepository.save(erpUser);

        mav.setViewName("/message");
        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/account/list");
        return mav;
    }

    /**
     * 保存账户信息
     * @param erpUser 界面获取的账户数据
     * @param shopchk 门店checkbox的值
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/account/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save( @ModelAttribute ERPUser erpUser, BindingResult bindingResult, String shopchk) {
        ModelAndView mav =  new ModelAndView("/message");
        if (bindingResult.hasErrors()) {
            List<ERPRole> roleList = (List<ERPRole>) erpRoleRepository.findAll();
            mav.setViewName("/account/form");
            Organization organization = (Organization) request.getSession().getAttribute("ORGANIZATIONS");
            erpUser.organization = organizationRepository.findOne(organization.id);
            mav.addObject("erpUser", erpUser);
            mav.addObject("roleList", roleList);
            return mav;
        }
        List<Long> shopsId = new ArrayList<>();
        for(String shop :shopchk.split(",")) {
            shopsId.add(Long.parseLong(shop));
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (erpUser.id != 0){  //update
            ERPUser updateUser;
            updateUser = erpUserRepository.findOne(erpUser.id);
            updateUser.role = erpUser.role;
            updateUser.username = erpUser.username;
            updateUser.realName = erpUser.username;
            updateUser.phone = erpUser.phone;
            if(!erpUser.password.equals(updateUser.password)){
                if(!bCryptPasswordEncoder.matches(erpUser.password,updateUser.password)) updateUser.password = bCryptPasswordEncoder.encode(erpUser.password);
            }
            updateUser.shops.clear();
            updateUser.shops = (List<Shop>) shopRepository.findAll(shopsId);
            updateUser.fingerPrint = erpUser.fingerPrint;
            erpUserRepository.save(updateUser);
        } else { //insert
            ERPUser valiUser = erpUserRepository.findByUsernameAndDeleted(erpUser.username, false);
            if (valiUser != null){
                mav.addObject("message","添加失败!该用户已存在");
                mav.addObject("responsePage", "/account/tosave");
                return mav;
            }
            erpUser.organization = organizationRepository.findOne(erpUser.organization.id);
            erpUser.shops = (List<Shop>) shopRepository.findAll(shopsId);
            erpUser.enable = true;
            erpUser.realName = erpUser.username;
            erpUser.password = bCryptPasswordEncoder.encode(erpUser.password);
            erpUserRepository.save(erpUser);
        }
        mav.addObject("message","保存成功");
        mav.addObject("responsePage", "/account/list");
        return mav;
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
        Page<ERPUser> userList = null;
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        Organization organizationFind = organizationRepository.findOne(organization);
        if(StringUtils.isEmpty(accountName)) {
            accountName = "";
        }

        if (shop != null) {
            shopList.add(shopRepository.findOne(shop));
            userList = accountRepository.findByUsernameLikeAndOrganizationAndShopsAndDeleted("%" + accountName + "%", organizationFind, shopList, false, pageRequest);
        } else {
            userList = accountRepository.findByUsernameLikeAndOrganizationAndDeleted("%" + accountName + "%", organizationFind, false, pageRequest);
        }

        for(ERPUser user : userList) {
            user.showedDate = user.createdDate == null ? "" : user.createdDate.toString();
        }

        return JqGridDataGenerator.getDataJson(userList);
    }

//    static class AccountValidator implements Validator {
//        /**
//         * This Validator validates *just* StockItem instances
//         */
//        @Override
//        public boolean supports(Class<?> clazz) {
//            return ERPUser.class.equals(clazz);
//        }
//
//        @Override
//        public void validate(Object obj, Errors errors) {
//            ERPUser erpUser = (ERPUser)obj;
//
//            if (!StringUtils.hasLength(erpUser.username)) {
//                errors.rejectValue("username", "required", "不能为空");
//            }
//
//            if (!StringUtils.hasLength(erpUser.password)) {
//                errors.rejectValue("password", "required", "不能为空");
//            }
//
//            if (!StringUtils.hasLength(erpUser.phone)) {
//                errors.rejectValue("phone", "required", "不能为空");
//            }
//        }
//    }
}
