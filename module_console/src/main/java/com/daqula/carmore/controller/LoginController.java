package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.announcement.ErpAnnouncement;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.ERPUserRepository;
import com.daqula.carmore.repository.ErpAnnouncementRepository;
import com.daqula.carmore.repository.ShopRepository;
import com.daqula.carmore.util.AuthorizatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.daqula.carmore.AuthorityConst.MANAGE_AGENTED_ORGANIZATION;
import static com.daqula.carmore.AuthorityConst.MANAGE_SYSTEM_ADMIN;
import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;
import static com.daqula.carmore.util.FreeMarkerUtil.getAuthorityModel;

@Controller
public class LoginController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ERPUserRepository erpUserRepository;

    @Autowired
    private ErpAnnouncementRepository erpAnnouncementRepository;

    @Autowired
    private ShopRepository shopRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request, @AuthenticationPrincipal ERPUser user) {

        //由于lazy加载，没有shoplist数据，重新获取全部数据。
        List<Shop> shops = shopRepository.findByUserId(user.id);
        user.shops = shops;
        //添加用户组织下所有的shops到session
        request.getSession().setAttribute("SHOPS", user.shops);

        if(user.organization.name.equals("上海达丘拉网络科技有限公司")) {
            if (user.shops.size() == 1) {
                Shop userShop = user.shops.get(0);
                request.getSession().setAttribute("SHOP", userShop);
                request.getSession().setAttribute("ORGANIZATIONS", userShop.organization);
                request.getSession().setAttribute("AUTHORITYSTR", AuthorizatorUtil.GetAuthorityCheck(user.role.authorityMask));
                request.getSession().setAttribute("AUTHORITY", getAuthorityModel());
                request.getSession().setAttribute("user", user);
                return new ModelAndView("/index", map(
                        entry("debugMessage", String.format("MANAGE_SYSTEM_ADMIN:%s, MANAGE_AGENTED_ORGANIZATION:%s",
                                user.checkAuthority(MANAGE_SYSTEM_ADMIN), user.checkAuthority(MANAGE_AGENTED_ORGANIZATION))),
                        entry("AUTHORITY", getAuthorityModel()),
                        entry("user", user),
                        entry("announcementList", getAnnouncement())
                ));
            } else {
                return new ModelAndView("/login", map(entry("shops", user.shops), entry("userid", user.id)));
            }
        } else {
            request.getSession().invalidate();
            Optional<String> error = Optional.of("");
            return new ModelAndView("/login", map(entry("error", error)));
        }
    }

    /**
     * 选择门店后跳转到index
     *
     * @param shopid
     * @param userid
     * @return
     */
    @RequestMapping(value = "/shoplogin", method = RequestMethod.GET)
    public ModelAndView shopLogin(Long shopid, Long userid,HttpServletRequest request, @AuthenticationPrincipal ERPUser user) {

        Shop userShop = shopRepository.findOne(shopid);
        request.getSession().setAttribute("SHOP", userShop);
        request.getSession().setAttribute("ORGANIZATIONS", userShop.organization);
        request.getSession().setAttribute("AUTHORITYSTR", AuthorizatorUtil.GetAuthorityCheck(user.role.authorityMask));
        request.getSession().setAttribute("AUTHORITY", getAuthorityModel());
        request.getSession().setAttribute("user", user);
        return new ModelAndView("/index", map(
                entry("debugMessage", String.format("MANAGE_SYSTEM_ADMIN:%s, MANAGE_AGENTED_ORGANIZATION:%s",
                        user.checkAuthority(MANAGE_SYSTEM_ADMIN), user.checkAuthority(MANAGE_AGENTED_ORGANIZATION))),
                entry("AUTHORITY", getAuthorityModel()),
                entry("user", user),
                entry("announcementList", getAnnouncement())
        ));
    }

    /**
     * 获取前5行公告
     *
     * @return
     */
    private List<ErpAnnouncement> getAnnouncement() {
        List<ErpAnnouncement> announcements = (List<ErpAnnouncement>) erpAnnouncementRepository.findAll();
        return announcements.subList(0, announcements.size() > 5 ? 5 : announcements.size());
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam Optional<String> error) {

        return new ModelAndView("/login", map(
                entry("error", error)
        ));
    }

    /**
     * 修改用户信息
     *
     * @param error
     * @return
     */
    @RequestMapping(value = "/login/useredit", method = RequestMethod.GET)
    public ModelAndView changePwdRedit(@RequestParam Optional<String> error) {
        return new ModelAndView("/erpuser/useredit", map(entry("notify", ""), entry("pwd", request.getSession().getAttribute("USER_PWD"))));
    }

    /**
     * 用户密码修改(不使用)
     *
     * @param oldPwd
     * @param newPwd
     * @return
     * @Author tianhongyu
     */
    @RequestMapping(value = "/login/changepwd", method = RequestMethod.POST)
    public ModelAndView changePwdAction(@RequestParam String oldPwd, String newPwd) {

        String name = "";
        if (request.getSession().getAttribute("USER_NAME") != null) {
            name = request.getSession().getAttribute("USER_NAME").toString();
        }
        ERPUser newuser = erpUserRepository.findByUsername(name);
        newuser.password = newPwd;
        erpUserRepository.save(newuser);
        request.getSession().setAttribute("USER_PWD", newPwd);
        return new ModelAndView("/erpuser/useredit", map(entry("notify", "保存成功"), entry("pwd", newPwd)));
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    public ModelAndView index(@AuthenticationPrincipal ERPUser user) {
        return new ModelAndView("/index", map(
                entry("AUTHORITY", getAuthorityModel()),
                entry("user", user),
                entry("ptAnnouncement",getAnnouncement())
        ));
    }

    /**
     * 密码修改
     * @param oldPwd
     * @param newPwd
     * @param user
     * @return
     */
    @RequestMapping(value = "/login/index/changepwd",method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public boolean changePwd(String oldPwd, String newPwd,@AuthenticationPrincipal ERPUser user){
        try {

            ERPUser erpUser = erpUserRepository.findByUsernameAndPasswordUseBCrypt(user.username, oldPwd);
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//			ERPUser erpUser = erpUserRepository.findByUsername(user.username);
//			if(erpUser != null && bCryptPasswordEncoder.matches(oldPwd, erpUser.password)){
            if(erpUser != null){
                erpUser.password = bCryptPasswordEncoder.encode(newPwd);
            }else{
                return false;
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 跳转到没有权限的页面
     *
     * @return
     */
    @RequestMapping(value = "/noauthority", method = RequestMethod.GET)
    public ModelAndView noauthority() {
        ModelAndView mav = new ModelAndView("/noauthority");
        return mav;
    }


    @RequestMapping(value = "/erpuser/username/check", method = RequestMethod.GET)
    @ResponseBody
    public boolean isUserNameExist(@RequestParam String username) {
        return erpUserRepository.findByUsername(username) == null ? false : true;
    }
}


