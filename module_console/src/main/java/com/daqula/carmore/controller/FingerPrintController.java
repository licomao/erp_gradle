package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.model.template.FingerPrintScanner;
import com.daqula.carmore.repository.ERPUserRepository;
import com.daqula.carmore.repository.FingerPrintScannerRepository;
import com.daqula.carmore.repository.OrganizationRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.repository.specification.FingerPrintScannerRepositorySpecifications;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指纹仪相关方法
 * Created by swj on 2015/11/6.
 */
@Controller
public class FingerPrintController {

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private FingerPrintScannerRepository fingerPrintScannerRepository;

    @Autowired
    private OrganizationRepository organizationRepository;


    /**
     * 门店
     */
    @Autowired
    private ShopRepository shopRepository;

    /**
     * 指纹机硬件信息管理  首页
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/fingerprint/list", method = RequestMethod.GET)
    public ModelAndView fingerPrintList(@AuthenticationPrincipal ERPUser user) {
        ModelAndView mav = new ModelAndView("/fingerprint/list");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_STOCKINGORDER)) {
            mav.setViewName("/noauthority");
            return mav;
        }

        Organization organization = new Organization();
        organization.id = user.organization.id;
        List<Shop> shopList = shopRepository.findByOrganization(organization);
        mav.addObject("shopList", shopList);
        return mav;
    }

    @RequestMapping(value = "/fingerprint/list/data", method = RequestMethod.GET)
    @ResponseBody
    @Transactional(readOnly = true)
    public Map<String, Object> listData(@RequestParam String page, @RequestParam int rows,
                                        @RequestParam String sord, @RequestParam String sidx, FingerPrintScanner fingerPrintScanner) {
        PageRequest pageRequest = new PageRequest(Integer.valueOf(page) - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        Specification<FingerPrintScanner> specification = Specifications.where(FingerPrintScannerRepositorySpecifications.filterByOrganization(fingerPrintScanner.organization))
                .and(FingerPrintScannerRepositorySpecifications.filterByVid(fingerPrintScanner.vid))
                .and(FingerPrintScannerRepositorySpecifications.filterByPid(fingerPrintScanner.pid))
                .and(FingerPrintScannerRepositorySpecifications.filterByUsbSn(fingerPrintScanner.usbSn))
                .and(FingerPrintScannerRepositorySpecifications.filterBySensorSN(fingerPrintScanner.sensorSN))
                .and(FingerPrintScannerRepositorySpecifications.filterByShop(fingerPrintScanner.shop))
                .and(FingerPrintScannerRepositorySpecifications.filterDeleteStatus(false));
        Page pageData = fingerPrintScannerRepository.findAll(specification, pageRequest);
        return JqGridDataGenerator.getDataJson(pageData);
    }

    @RequestMapping(value = "/fingerprint/tosave", method = RequestMethod.GET)
    public ModelAndView tosave() {
        ModelAndView mav = new ModelAndView("/fingerprint/form");


        return mav;
    }


   /* *//**
     * @param fingerprint
     * @param name
     * @return
     *//*
    @RequestMapping(value = "/baseset/saveFingerprint", method = RequestMethod.POST)
    public ModelAndView saveFingerprint(String fingerprint, String name) {
        ModelAndView mav = new ModelAndView("/baseset/test");

        ERPUser erpUser = erpUserRepository.findByUsername(name);
        if (erpUser != null) {
//            erpUser.fingerprint = fingerprint;

        }

        return mav;
    }*/

    /**
     * 录入指纹仪硬件信息
     *
     * @param zkInfoArr
     * @return
     */
    @RequestMapping(value = "/baseset/insettest", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> insetTest(@RequestParam String zkInfoArr) {
        Map<String, Object> result = new HashMap<>();


        Gson gson = new Gson();
        List<FingerPrintScanner> list = gson.fromJson(zkInfoArr, new TypeToken<List<FingerPrintScanner>>() {
        }.getType());


        for (FingerPrintScanner fingerPrintScanner : list) {
            FingerPrintScanner isExist = fingerPrintScannerRepository.findByPidAndVidAndUsbSn(fingerPrintScanner.pid, fingerPrintScanner.vid, fingerPrintScanner.usbSn);
            if (isExist == null) fingerPrintScannerRepository.save(fingerPrintScanner);
        }

        result.put("result", true);


//        mav.addObject("message", message);
//        mav.addObject("responsePage", "/baseset/tosave");
        return result;
    }


    /**
     * 录入指纹仪硬件信息
     *
     * @param sensorSN
     * @return
     */
    @RequestMapping(value = "/fingerprint/save", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> save(@RequestParam String sensorSN) {
        Map<String, Object> result = new HashMap<>();

        FingerPrintScanner fingerPrintScanner = fingerPrintScannerRepository.findBySensorSN(sensorSN);

        if (fingerPrintScanner != null) {
//            result.put("result", false);
        } else {
            FingerPrintScanner printScanner = new FingerPrintScanner();
            printScanner.sensorSN = sensorSN;
            fingerPrintScannerRepository.save(printScanner);
            result.put("result", true);
        }
        return result;
    }

    @RequestMapping(value = "/fingerprint/erpuser/list", method = RequestMethod.GET)
    @ResponseBody
    public Map<Long, String> getFingerPrintList(@AuthenticationPrincipal ERPUser user) {
        Map<Long, String> result = new HashMap<>();
        Organization organization = organizationRepository.findOne(user.organization.id);
//        List<ERPUser> erpUserList = erpUserRepository.findByOrganizationAndFingerPrintNotNull(organization);
        List<ERPUser> erpUserList = erpUserRepository.findByOrganizationAndFingerPrintNotNullWithNoCache(organization);
        for (ERPUser erpUser : erpUserList) {
            result.put(erpUser.id, erpUser.fingerPrint);
        }

        return result;

    }

    /**
     * 校验指纹机是否在库中有记录
     * @param sensorSN
     * @return
     */
    @RequestMapping(value = "/fingerprint/checkauthority", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkAuthority(String sensorSN) {
        return fingerPrintScannerRepository.findBySensorSN(sensorSN) == null ? false : true;
    }
}
