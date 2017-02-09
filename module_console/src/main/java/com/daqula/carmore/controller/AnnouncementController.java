package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.announcement.ErpAnnouncement;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.ErpAnnouncementRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.specification.ErpAnnouncementRepositorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import org.joda.time.DateTime;
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

/**
 * Created by mdc on 2015/10/15.
 */

@Controller
public class AnnouncementController {

    @Autowired
    private ErpAnnouncementRepository erpAnnouncementRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @InitBinder("erpAnnouncement")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new ErpAnnounceValidator());
    }

    /**
     * 进入ERP公告查询
     * @return
     */
    @RequestMapping(value = "/erpannouncement/list", method = RequestMethod.GET)
    public ModelAndView list(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav =  new ModelAndView("/erpannouncement/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_FIXEDASSET)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        mav.addObject("organization", user.organization);
        return mav;
    }

    /**
     * ERP公告获取数据列表
     * @param page
     * @param rows
     * @param sord
     * @param sidx
     * @param erpAnnouncement
     * @return
     */
    @RequestMapping(value = "/erpannouncement/list/data",method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx,@ModelAttribute ErpAnnouncement erpAnnouncement,long orgId) {

        PageRequest pageRequest = new PageRequest(page-1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");
        Organization organization = organizationRepository.findOne(orgId);
        Specification specification = Specifications.where(ErpAnnouncementRepositorySpecifications.filterByOrganization(organization))
                .and(ErpAnnouncementRepositorySpecifications.filterByTitle(erpAnnouncement.title))
                .and(ErpAnnouncementRepositorySpecifications.filterByPublisher(erpAnnouncement.publisher));
        Page pageData = erpAnnouncementRepository.findAll(specification,pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 新建更新公告信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/erpannouncement/new",method = RequestMethod.GET)
    public ModelAndView erpnew(@RequestParam(required = false) Long id) {
        ModelAndView model = new ModelAndView("/erpannouncement/form");

        if(id != null && id >= 0) {
            ErpAnnouncement erpAnnouncement = erpAnnouncementRepository.findOne(id);
            model.addObject("erpAnnouncement", erpAnnouncement);
            model.addObject("pageContent", "更新");
            return model;
        }
        model.addObject("erpAnnouncement", new ErpAnnouncement());
        model.addObject("pageContent", "新增");
        return model;
    }

    /**
     * 新建公告信息
     * @param erpAnnouncement 公告实体
     * @return
     */
    @RequestMapping(value = "/erpannouncement/new" ,method = RequestMethod.POST)
    @Transactional
    public ModelAndView saveAnnounce(@Valid @ModelAttribute ErpAnnouncement erpAnnouncement, BindingResult bindingResult, @AuthenticationPrincipal ERPUser erpUser) {
        ModelAndView mav =  new ModelAndView("/message");
        if (bindingResult.hasErrors()) {
            return new ModelAndView("/erpannouncement/form");
        }
        ErpAnnouncement updateBean = erpAnnouncementRepository.findOne(erpAnnouncement.id);
        erpAnnouncement.publishDate = DateTime.now();
        erpAnnouncement.organization = erpUser.organization;
        if (updateBean == null) {
            erpAnnouncementRepository.save(erpAnnouncement);
        } else {
            updateBean.publishDate = erpAnnouncement.publishDate;
            updateBean.publisher = erpAnnouncement.publisher;
            updateBean.title = erpAnnouncement.title;
            updateBean.content = erpAnnouncement.content;
            erpAnnouncementRepository.save(updateBean);
        }
        mav.addObject("message", "发布成功");
        mav.addObject("responsePage", "/erpannouncement/list");
        return mav;
    }

    /**
     * 公告信息删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/erpannouncement/delete", method = RequestMethod.GET)
    @Transactional
    public ModelAndView erpdel(@RequestParam(required = false) Long id) {
        ModelAndView mav =  new ModelAndView("/message");
        if(id > 0) {
            erpAnnouncementRepository.delete(id);
        }

        mav.addObject("message", "删除成功");
        mav.addObject("responsePage", "/erpannouncement/list");
        return mav;
    }

    /**
     * 参数合法性验证
     */
    static class  ErpAnnounceValidator implements Validator {
        @Override
        public boolean supports(Class<?> clazz)
        {return ErpAnnouncement.class.equals(clazz);}

        @Override
        public void validate(Object obj , Errors errors) {

            ErpAnnouncement announce = (ErpAnnouncement)obj;
            String tittle = announce.title;
            if (!StringUtils.hasLength(tittle)) {
                errors.rejectValue("title", "required", "不能为空");
            }
            String content = announce.content;
            if (!StringUtils.hasLength(content)) {
                errors.rejectValue("content", "required", "不能为空");
            }
            String publisher = announce.publisher;
            if (!StringUtils.hasLength(publisher)) {
                errors.rejectValue("publisher", "required", "不能为空");
            }
        }
    }

}
