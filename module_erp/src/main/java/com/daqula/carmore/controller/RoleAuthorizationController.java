package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPRole;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.ERPRoleRepository;
import com.daqula.carmore.repository.ERPUserRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.specification.ERPRoleRepositorySpecifications;
import com.daqula.carmore.util.AuthorizatorUtil;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
@Controller
public class RoleAuthorizationController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private ERPRoleRepository erpRoleRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

//    @InitBinder("erpRole")
//    public void initBinder(WebDataBinder binder) {
//        ErpRoleValidator erpRoleValidator = new ErpRoleValidator();
//        binder.addValidators(erpRoleValidator);
//    }

    /**
     * 获取角色权限列表
     * @return
     */
    @RequestMapping("/roleauthorization/list")
    @Transactional(readOnly = true)
    public ModelAndView GetAuthorizationList(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/roleauthorization/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_ROLEAUTHORIZATION)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        Organization organization = organizationRepository.findOne(user.organization.id);
        List<ERPRole> erpRoleList = erpRoleRepository.findByOrganization(organization);
        mav.addObject("erpRoleList", erpRoleList);
        mav.addObject("organization",organization);
        return mav;
    }

    /**
     * 创建或者更新角色信息
     * @param rowid 角色id
     * @return
     */
    @RequestMapping("/roleauthorization/tosave")
    @Transactional(readOnly = true)
    public ModelAndView createOrUpdate(@RequestParam(required = false) Long rowid) {
        ModelAndView mav = new ModelAndView("/roleauthorization/form");
//        Map<String,Object> map = new HashMap<>();
        List<ERPRole> roleList = (List<ERPRole>) erpRoleRepository.findAll();
        mav.addObject("roleList", roleList);
        if(rowid != null && rowid > 0) {
            ERPRole erpRole = erpRoleRepository.findOne(rowid);

            //去掉拼接的组织serialNum
//            int index = erpRole.role.lastIndexOf(erpRole.organization.serialNum);
//            if (index > 0) {
//                erpRole.role = erpRole.role.substring(0,index);
//            }
            mav.addObject("erpRole", erpRole);
            mav.addObject("organization", erpRole.organization);
            mav.addObject("authorityCheckbox", AuthorizatorUtil.GetAuthorityCheck(erpRole.authorityMask));
        } else {
            ERPRole erpRole = new ERPRole();
            mav.addObject("erpRole", erpRole);

            String name = "";
            if(request.getSession().getAttribute("USER_NAME") != null) {
                name = request.getSession().getAttribute("USER_NAME").toString();
            }
            ERPUser userInfo = erpUserRepository.findByUsername(name);
            mav.addObject("organization", userInfo.organization);
            mav.addObject("authorityCheckbox", "");
        }
        return mav;
    }

    /**
     * 删除角色权限
     * @param rowid 角色id
     * @return
     */
    @RequestMapping(value = "/roleauthorization/delete", method = RequestMethod.POST)
    @Transactional
    public ModelAndView deleteRow(String rowid) {
        ModelAndView mav = new ModelAndView();

        ERPRole erpRole = erpRoleRepository.findOne(Long.parseLong(rowid));
        erpRole.deleted = true;
        erpRoleRepository.save(erpRole);

        mav.setViewName("/message");
        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/roleauthorization/list");
        return mav;
    }

    /**
     * 保存角色权限信息
     * @param erpRole 界面获取的账户数据
     * @param
     * @return
     */
    @RequestMapping(value = "/roleauthorization/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView saveRole( @ModelAttribute ERPRole erpRole,String function ,@AuthenticationPrincipal ERPUser erpUser) {
        ModelAndView mav = new ModelAndView("/message");
        String message="保存成功,相关用户重新登录后将使用新权限";
        boolean hasSame = false;
//        if (bindingResult.hasErrors()) {
//            mav.setViewName("/roleauthorization/form");
//            return mav;
//        }
        Long mask = 0b0L;

        if (function != null) {
            for(String func : function.split(",")) {
                mask = mask | GetAuthorityConst(func);
            }
        }

        erpRole.organization = erpUser.organization;
        ERPRole erpRoleFind = erpRoleRepository.findByRole(erpRole.role + erpUser.organization.serialNum);
        ERPRole erpRoleUpdate = erpRoleRepository.findOne(erpRole.id);
        erpRole.authorityMask = mask;

        if (erpRoleFind != null && erpRole.id ==0) {
            hasSame = true;
        } else if (erpRoleFind != null && erpRole.id != 0 && (erpRoleFind.id != erpRoleUpdate.id)) {
            hasSame = true;
        }

        if (hasSame) {
            message = "角色名称重复";
            mav.setViewName("/roleauthorization/form");
            mav.addObject("message", message);
            mav.addObject("organization", erpRole.organization);
            mav.addObject("authorityCheckbox", AuthorizatorUtil.GetAuthorityCheck(erpRole.authorityMask));
            mav.addObject("erpRole", erpRole);
            return mav;
        }

        if(erpRole.role.indexOf(erpUser.organization.serialNum) < 0 ){
            erpRole.role += erpUser.organization.serialNum;
        }

        if (erpRole.id != 0) {
            erpRoleUpdate.role = erpRole.role;
            erpRoleUpdate.authorityMask = mask;
            erpRoleRepository.save(erpRoleUpdate);
        } else {

            erpRoleRepository.save(erpRole);
        }

        mav.addObject("message",message);
        mav.addObject("responsePage","/roleauthorization/list");
        return mav;
    }




    /**
     * 显示角色列表
     * @param roleName 角色名
     * @param organizationid 组织id
     * @return
     */
    @RequestMapping(value = "/roleauthorization/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> getRoleAuthorizationList(@RequestParam int page, @RequestParam int rows,
                                                     @RequestParam String sord, @RequestParam String sidx,
                                                     @RequestParam String roleName, @RequestParam Long organizationid) {
        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(organizationid);
        Specification<ERPRole> specification = Specifications.where(ERPRoleRepositorySpecifications.filterByOrganization(organization))
                .and(ERPRoleRepositorySpecifications.filterByDeleted(false))
                .and(ERPRoleRepositorySpecifications.filterByRoleName(roleName));
        Page pageData = erpRoleRepository.findAll(specification, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }


//    /**
//     * valid
//     */
//    static class ErpRoleValidator implements Validator {
//
//        @Override
//        public boolean supports(Class<?> clazz) {
//            return ERPRole.class.equals(clazz);
//        }
//
//        @Override
//        public void validate(Object obj, Errors errors) {
//            ERPRole erpRole = (ERPRole) obj;
//
//            String role = erpRole.role;
//            if (!StringUtils.hasLength(role)) {
//                errors.rejectValue("role", "required", "不能为空");
//            }
//        }
//    }

    /**
     * 根据checkbox名，获取对应的权限二进制码
     * @param functionName
     * @return
     */
    private Long GetAuthorityConst(String functionName) {
        Long result = 0L;

        switch (functionName) {
            case "fu91":
                result = AuthorityConst.MANAGE_ORG_ROLEAUTHORIZATION;
                break;
            case "fu1":
                result = AuthorityConst.MANAGE_ORG_FIXEDASSET;
                break;
            case "fu2":
                result = AuthorityConst.MANAGE_ORG_MATERIALORDER;
                break;
            case "fu92":
                result = AuthorityConst.MANAGE_ORG_ACCOUNT;
                break;
            case "fu4":
                result = AuthorityConst.MANAGE_ORG_CUSTOMSTOCKITEM;
                break;
            case "fu94":
                result = AuthorityConst.MANAGE_ORG_SECONDARYITEM;
                break;
            case "fu93":
                result = AuthorityConst.MANAGE_ORG_SHOPMANAGE;
                break;
            case "fu3":
                result = AuthorityConst.MANAGE_ORG_SUPPLIER;
                break;
            case "fu95":
                result = AuthorityConst.MANAGE_ORG_NOTICE;
                break;
            case "fu5":
                result = AuthorityConst.MANAGE_ORG_CUSTOMER;
                break;
            case "fu6":
                result = AuthorityConst.MANAGE_ORG_EXPENSE;
                break;
            case "fu11":
                result = AuthorityConst.MANAGE_ORG_STOCK;
                break;
            case "fu12":
                result = AuthorityConst.MANAGE_ORG_STOCKINGORDER;
                break;
            case "fu13":
                result = AuthorityConst.MANAGE_ORG_STOCKTRANSFERORDER;
                break;
            case "fu21":
                result = AuthorityConst.MANAGE_ORG_BASESET;
                break;
            case "fu22":
                result = AuthorityConst.MANAGE_ORG_BASESETMONTHLY;
                break;
            case "fu31":
                result = AuthorityConst.MANAGE_ORG_PURCHASEORDER;
                break;
            case "fu32":
                result = AuthorityConst.MANAGE_ORG_PURCHASEAPPROVE;
                break;
            case "fu33":
                result = AuthorityConst.MANAGE_ORG_ADDSTORAGE;
                break;
            case "fu34":
                result = AuthorityConst.MANAGE_ORG_PURCHASEPAYMENT;
                break;
            case "fu35":
                result = AuthorityConst.MANAGE_ORG_PAYMENTQUERY;
                break;
            case "fu36":
                result = AuthorityConst.MANAGE_ORG_PAYMENTQUERY_EDIT;
                break;
            case "fu41":
                result = AuthorityConst.MANAGE_ORG_REFUNDORDER;
                break;
            case "fu42":
                result = AuthorityConst.MANAGE_ORG_REFUNDAPPROVE;
                break;
            case "fu51":
                result = AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITE;
                break;
            case "fu52":
                result = AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITEREMOTE;
                break;
            case "fu53":
                result = AuthorityConst.MANAGE_ORG_CUSTOMERPURCHASESUITEVIPCARD;
                break;
            case "fu61":
                result = AuthorityConst.MANAGE_ORG_STAFF;
                break;
            case "fu62":
                result = AuthorityConst.MANAGE_ORG_JOB;
                break;
            case "fu63":
                result = AuthorityConst.MANAGE_ORG_STAFFATTENDANCE;
                break;
            case "fu64":
                result = AuthorityConst.MANAGE_ORG_DOSTAFFATTENDANCE;
                break;
            case "fu65":
                result = AuthorityConst.MANAGE_ORG_FINGERATTENDANCE;
                break;
            case "fu71":
                result = AuthorityConst.MANAGE_ORG_DISCOUNTAPPROVE;
                break;
            case "fu72":
                result = AuthorityConst.MANAGER_ORG_STAFF_DELETE;
                break;
            case "fu81":
                result = AuthorityConst.MANAGE_ORG_PAYMENTPRE;
                break;
            case "fu82":
                result = AuthorityConst.MANAGE_ORG_SEARCHCUSTOMINFO;
                break;
            case "fu83":
                result = AuthorityConst.MANAGE_ORG_SEARCHSETTLEINFO;
                break;
            case "fu84":
                result = AuthorityConst.MANAGE_ORG_CALDAYSALEPRICE;
                break;
            case "fu85":
                result = AuthorityConst.MANAGE_ORG_SEARCHORDERDEATIAL;
                break;
        }
        return result;
    }

}