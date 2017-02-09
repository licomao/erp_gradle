package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.admin.Campaign;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.template.City;
import com.daqula.carmore.repository.CampaignRepository;
import com.daqula.carmore.repository.CityRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.specification.CampaignSpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import org.hibernate.Hibernate;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chenxin on 2015/7/28.
 */
@Controller
public class CampaignController {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    /**
     * TO APP公告查询
     * @return
     */
    @RequestMapping(value = "/campaign/list" ,method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView list (@AuthenticationPrincipal ERPUser user, HttpSession session) {
        ModelAndView mav = new ModelAndView("/campaign/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_SUPPLIER)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        String publishDate = (new SimpleDateFormat("yyyy/MM/dd")).format(new Date());

        mav.addObject("publishDate",publishDate);
        return mav;
    }

    /**
     * 获取APP公告信息列表数据
     *  @param campaign APP公告查询条件
     *  @return
     */
    @RequestMapping(value = "/campaign/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public  Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                                      @RequestParam String sord, @RequestParam String sidx, @ModelAttribute Campaign campaign) {
        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        Specification<Campaign> specification = Specifications.where(CampaignSpecifications.filterByCompaignType(campaign.compaignType))
                .and(CampaignSpecifications.filterByPublishDateStart(campaign.publishDate))
                .and(CampaignSpecifications.filterByPublishDateEnd(campaign.publishDate));
        Page pageData = campaignRepository.findAll(specification,pageRequest);

        return JqGridDataGenerator.getDataJson(pageData);
    }

    /**
     * 进入APP公告信息页面
     * @param id APP公告的id
     * @return
     */
    @RequestMapping(value = "/campaign/form", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView form(String id, HttpSession session){
        ModelAndView mav = new ModelAndView("/campaign/form");
        Campaign campaign = new Campaign();
        if (!id.equals("0")) {
            campaign = campaignRepository.findByIdNotlazy (Long.parseLong(id));
            if (campaign == null) {
                mav.setViewName("/message");
                mav.addObject("message","未找到相关的APP公告信息");
                mav.addObject("responsePage","/campaign/list");
                return mav;
            }
        }

        mav.addObject("campaign", campaign);
        return mav;
    }


    /**
     * 保存APP公告信息
     * @param campaign
     * @return
     */
    @RequestMapping(value = "/campaign/save", method = RequestMethod.POST)
    @Transactional
    public ModelAndView save(@ModelAttribute Campaign campaign){
        ModelAndView mav = new ModelAndView("/message");
        String message="保存成功";
        if (campaign.id != 0){
            Campaign updateCampaign = campaignRepository.findOne(campaign.id);
            updateCampaign.compaignType = campaign.compaignType;
            updateCampaign.onBanner = campaign.onBanner;
            updateCampaign.url = campaign.url;
            updateCampaign.bannerImageUrl = campaign.bannerImageUrl;
            updateCampaign.summary = campaign.summary;
            updateCampaign.latitude = campaign.latitude;
            updateCampaign.longitude = campaign.longitude;
            if (campaign.shop == null) {
                updateCampaign.shop = null;
            } else {
                updateCampaign.shop = shopRepository.findOne(campaign.shop.id);
            }
            if (campaign.city == null) {
                updateCampaign.city = null;
            } else {
                updateCampaign.city = cityRepository.findOne(campaign.city.id);
            }
            campaignRepository.save(updateCampaign);
        } else {
            campaign.publishDate =  new DateTime();
            campaignRepository.save(campaign);
        }

        mav.addObject("message",message);
        mav.addObject("responsePage","/campaign/list");
        return mav;
    }


    /**
     * 获取所有城市信息
     * @return
     */
    @RequestMapping(value = "/campaign/getcity", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public List<City> getCity() {
        List<City> cityList = new ArrayList<City>();
        Iterator<City> cityIterable = cityRepository.findAll().iterator();

        while (cityIterable.hasNext())
            cityList.add(cityIterable.next());
        return cityList;
    }

    /**
     * 获取所有组织信息
     * @return
     */
    @RequestMapping(value = "/campaign/getorganization", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Organization> getOrganization() {
        List<Organization> organizationList = new ArrayList<Organization>();
        Iterator<Organization> organizationIterable = organizationRepository.findAll().iterator();

        while (organizationIterable.hasNext())
            organizationList.add(organizationIterable.next());
        return organizationList;
    }

    /**
     * 获取组织下门店信息
     * @return
     */
    @RequestMapping(value = "/campaign/getshop", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Shop> getOrganization(Long id) {
        Organization organization = organizationRepository.findOne(id);
        return shopRepository.findByOrganization(organization);
    }

    /**
     * 保存作废，启用
     * @param id
     * @return
     */
    @RequestMapping(value = "/campaign/enable", method = RequestMethod.GET)
    @Transactional
    public ModelAndView enable(String id){
        ModelAndView mav = new ModelAndView("/message");
        String message="修改成功";

        Campaign campaign = campaignRepository.findOne(Long.parseLong(id));
        campaign.deleted = !campaign.deleted;
        campaignRepository.save(campaign);

        mav.addObject("message",message);
        mav.addObject("responsePage","/campaign/list");
        return mav;
    }

}
