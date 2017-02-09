package com.daqula.carmore.controller;

import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.announcement.ErpAnnouncement;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.model.shop.Shop;
import com.daqula.carmore.repository.*;
import com.daqula.carmore.util.AuthorizatorUtil;
import com.daqula.carmore.util.SessionUtil;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
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

import static com.daqula.carmore.util.CollectionUtil.*;
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

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private StockingOrderRepository stockingOrderRepository;

	@Autowired
	private BaseSetRepository baseSetRepository;

	private final static String PT_NAME = "上海达丘拉网络科技有限公司";



	@RequestMapping(value = "/", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView index(HttpServletRequest request, @AuthenticationPrincipal ERPUser user) {
		//由于lazy加载，没有shoplist数据，重新获取全部数据。
//		ERPUser loginUser = erpUserRepository.findOne(user.id);
		//添加用户组织下所有的shops到session
		List<Shop> shops = shopRepository.findByUserId(user.id);
		user.shops = shops;
		request.getSession().setAttribute("SHOPS", user.shops);
		request.getSession().setAttribute("AUTHORITYSTR", AuthorizatorUtil.GetAuthorityCheck(user.role.authorityMask));
		if(user.shops.size() == 1) {
			Shop userShop = user.shops.get(0);
			request.getSession().setAttribute("SHOP", userShop);
			request.getSession().setAttribute("ORGANIZATIONS", userShop.organization);
			request.getSession().setAttribute("AUTHORITY",getAuthorityModel());
			request.getSession().setAttribute("user",user);
			SessionUtil.checkPd(request.getSession(), userShop, baseSetRepository,stockingOrderRepository);
			return new ModelAndView("/index", map(
					entry("AUTHORITY", getAuthorityModel()),
					entry("user", user),
					entry("ptAnnouncement",getAnnouncement()),
					entry("erpAnnouncement",getAnnouncement(user.organization))
			));
		}else {
			return new ModelAndView("/login",map(entry("shops",user.shops),entry("userid",user.id)));
		}
	}

	/**
	 * 选择门店后跳转到index
	 * @param shopid
	 * @return
	 */
	@RequestMapping(value = "/shoplogin", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView shopLogin(Long shopid,HttpServletRequest request, @AuthenticationPrincipal ERPUser user) {
//		ERPUser loginUser = erpUserRepository.findOne(userid);
		Shop userShop = shopRepository.findOne(shopid);
		if (userShop == null) {
			new ModelAndView("redirect:/logout");
		}
		request.getSession().setAttribute("SHOP",userShop);
		request.getSession().setAttribute("ORGANIZATIONS",userShop.organization);
		request.getSession().setAttribute("AUTHORITYSTR", AuthorizatorUtil.GetAuthorityCheck(user.role.authorityMask));
		request.getSession().setAttribute("AUTHORITY",getAuthorityModel());
		request.getSession().setAttribute("user",user);
		SessionUtil.checkPd(request.getSession(), userShop, baseSetRepository,stockingOrderRepository);
		return new ModelAndView("/index", map(
				entry("AUTHORITY", getAuthorityModel()),
				entry("user", user),
				entry("ptAnnouncement",getAnnouncement()),
				entry("erpAnnouncement",getAnnouncement(user.organization))
		));
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView index(@AuthenticationPrincipal ERPUser user,HttpServletRequest request) {

		return new ModelAndView("/index", map(
				entry("AUTHORITY", getAuthorityModel()),
				entry("user", user),
				entry("ptAnnouncement",getAnnouncement()),
				entry("erpAnnouncement",getAnnouncement(user.organization))
		));
	}

	/**
	 * 获取Erp组织公告行公告
	 * @return
	 */
	private ErpAnnouncement getAnnouncement(Organization organization) {
		List<ErpAnnouncement> announcements = erpAnnouncementRepository.findByOrganizationOrderByPublishDateDesc(organization);
		if(announcements.size() > 0) {
			ErpAnnouncement erpAnnouncement = announcements.get(0);
			DateTime now = new DateTime();
			Period period = new Period(erpAnnouncement.publishDate, now,PeriodType.days());
			if (period.getDays() <= 3){
				erpAnnouncement.isNewInfo = true;
			}
			return erpAnnouncement;
		} else {
			ErpAnnouncement announcement = new ErpAnnouncement();
			announcement.title = "暂无通知";
			announcement.content = "尽情期待";
			announcement.publisher = " ";
			return  announcement;
		}
	}

	@Transactional(readOnly = true)
	private ErpAnnouncement getAnnouncement() {
		Organization orgPt = organizationRepository.findByName(PT_NAME);
		List<ErpAnnouncement> announcements = erpAnnouncementRepository.findByOrganizationOrderByPublishDateDesc(orgPt);
		if(announcements.size() > 0) {
			return announcements.get(0);
		} else {
			ErpAnnouncement announcement = new ErpAnnouncement();
			announcement.title = "暂无通知";
			announcement.content = "尽情期待";
			announcement.publisher = " ";
			return  announcement;
		}
	}


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam Optional<String> error) {

		return new ModelAndView("/login", map(
				entry("error", error)
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
	 * @return
	 */
	@RequestMapping(value = "/noauthority" ,method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView noauthority () {
		ModelAndView mav = new ModelAndView("/noauthority");
		return mav;
	}


	/**
	 * WEB跳转至此登录路径
	 * @return
	 */
	@RequestMapping(value = "/logintest", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView loginTest(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("/logintest");
		return mav;
	}


}


