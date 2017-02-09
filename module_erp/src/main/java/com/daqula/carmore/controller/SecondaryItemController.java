package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.SecondaryCategory;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.SecondaryCategoryRepository;
import com.daqula.carmore.repository.specification.SecondaryCategorySpecifications;
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

import javax.validation.Valid;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * 二级分类Controller
 * Created by shd
 */
@Controller
public class SecondaryItemController {

    @Autowired
    private SecondaryCategoryRepository secondaryCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    /**
     * Register binding validators
     *
     * @param binder
     */
    @InitBinder("secondaryCategory")
    public void initBinder(WebDataBinder binder) {
        SecondaryItemValidator secondaryItemValidator= new SecondaryItemValidator();
        binder.addValidators(secondaryItemValidator);
    }

    /**
     * 获取商品列表
     * @return
     */
    @RequestMapping("/secondaryitem/list")
    @Transactional(readOnly = true)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav=new ModelAndView("/secondaryitem/list");
        mav.addObject("orgId",user.organization.id);
        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_SECONDARYITEM)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        return mav;
    }

    /**
     * 获取添加或更新分类商品的信息
     * @param id 商品id
     * @return
     */
    @RequestMapping("/secondaryitem/tosave")
    @ResponseBody
    @Transactional(readOnly = true)
    public ModelAndView toSave(@RequestParam(required = false) Long id) {
        ModelAndView mav;
        if(id != null && id >= 0) {
            SecondaryCategory secondaryItem = secondaryCategoryRepository.findOne(id);
            mav = new ModelAndView("/secondaryitem/form", map(entry("secondaryCategory", secondaryItem)));
        } else {
            mav = new ModelAndView("/secondaryitem/form", map(entry("secondaryCategory", new SecondaryCategory())));
        }
        return mav;
    }

    /**
     * 删除分类商品
     * @param id 商品id
     * @return
     */
    @RequestMapping("/secondaryitem/delete")
    @Transactional
    public ModelAndView delete(@RequestParam Long id) {
        ModelAndView mav =  new ModelAndView("/message");
        SecondaryCategory secondaryCategory = secondaryCategoryRepository.findOne(id);
        secondaryCategory.deleted = true;
        secondaryCategoryRepository.save(secondaryCategory);

        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/secondaryitem/list");
        return mav;
    }

    /**
     * 保存分类商品
     * @param secondaryCategory 分类商品信息
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/secondaryitem/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@Valid @ModelAttribute SecondaryCategory secondaryCategory, BindingResult bindingResult, @AuthenticationPrincipal ERPUser user) {
        ModelAndView mav =  new ModelAndView("/message");
        if (bindingResult.hasErrors()) {
            mav.setViewName("/secondaryitem/form");
            return mav;
        }
        secondaryCategory.organization = user.organization;
        if (secondaryCategory.id != 0) {
            SecondaryCategory secondaryCategoryUpdate = secondaryCategoryRepository.findOne(secondaryCategory.id);
            secondaryCategoryUpdate.name = secondaryCategory.name;
            secondaryCategoryUpdate.additionRate = secondaryCategory.additionRate;
            secondaryCategoryUpdate.rootCategory = secondaryCategory.rootCategory;
            secondaryCategoryRepository.save(secondaryCategoryUpdate);
        } else {
            secondaryCategoryRepository.save(secondaryCategory);
        }

        mav.addObject("message", "保存成功");
        mav.addObject("responsePage", "/secondaryitem/list");

        return mav;
    }

    /**
     * 根据条件获取分类商品
     * @param rootCategory 顶级商品id
     * @return
     */
    @RequestMapping(value = "/secondaryitem/list/data")
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,
                                        @RequestParam Integer rootCategory,long orgId) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        if (rootCategory == 99) {
            rootCategory = null;
        }
        Organization organization = organizationRepository.findOne(orgId);
        Specification<SecondaryCategory> specification = Specifications.where(SecondaryCategorySpecifications.filterByRootCategory(rootCategory))
                .and(SecondaryCategorySpecifications.filterByOrganization(organization))
                .and(SecondaryCategorySpecifications.filterDeleted(false));
        Page pageData = secondaryCategoryRepository.findAll(specification,pageRequest);

//        Page<SecondaryCategory> secondaryCategoryList = secondaryCategoryRepository.findByRootCategoryAndDeleted(rootCategory, false, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    static class SecondaryItemValidator implements Validator {

        @Override
        public boolean supports(Class<?> clazz) {
            return SecondaryCategory.class.equals(clazz);
        }

        @Override
        public void validate(Object obj, Errors errors) {
            SecondaryCategory secondaryCategory = (SecondaryCategory)obj;

            if (!StringUtils.hasLength(secondaryCategory.name)) {
                errors.rejectValue("name", "required", "不能为空");
            }
        }
    }
}