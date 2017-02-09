package com.daqula.carmore.controller;

import com.daqula.carmore.AuthorityConst;
import com.daqula.carmore.model.acl.ERPUser;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static com.daqula.carmore.util.FreeMarkerUtil.getAuthorityModel;

/**
 * 报表Controller
 * Created by mdc on 2016/1/11.
 */
@Controller
public class ReportController {

    /**
     * 采购统计页面
     * @param user
     * @return
     */
    @RequestMapping(value = "/report/purchaselist" ,method = RequestMethod.GET)
    public ModelAndView purchaseList(@AuthenticationPrincipal ERPUser user){
        ModelAndView mav = new ModelAndView("/report/purchaselist");

        if (!user.checkAuthority(AuthorityConst.MANAGE_ORG_JOB)) { //TODO
            mav.setViewName("/noauthority");
            return mav;
        }

        mav.addObject("AUTHORITY", getAuthorityModel());
        return mav;
    }
}
