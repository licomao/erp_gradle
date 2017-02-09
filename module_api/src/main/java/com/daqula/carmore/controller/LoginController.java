package com.daqula.carmore.controller;

import com.daqula.carmore.CarmoreProperties;
import com.daqula.carmore.ErrorCode;
import com.daqula.carmore.model.customer.Customer;
import com.daqula.carmore.model.customer.CustomerAppProfile;
import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.pojo.CustomerData;
import com.daqula.carmore.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.daqula.carmore.utils.JsonResultBuilder.buildResult;
import static com.daqula.carmore.utils.JsonResultBuilder.buildSuccessResult;

@RestController
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
	CustomerRepository customerRepository;

	@Autowired
	CustomerProfileRepository customerProfileRepository;

	@Autowired
	CustomerPurchasedSuiteRepository customerPurchasedSuiteRepository;

    @Autowired
    CarmoreProperties carmoreProperties;

    @Autowired
    CampaignRepository campaignRepository;

    @Autowired
    private PresaleOrderRepository presaleOrderRepository;

    @Autowired
    private SettleOrderRepository settleOrderRepository;

    /**
     * 用户登陆 /api/login
	 *
     * @param mobile 手机号码
     * @param code 验证码
     */
	@RequestMapping(value = "/api/login", method = RequestMethod.POST)
    @Transactional
	public Map<String, Object> login(@RequestParam String mobile, @RequestParam int code,
                                     HttpServletRequest request) {
		List<Customer> customers = customerRepository.findByMobile(mobile);
		CustomerAppProfile profile;
        if (!carmoreProperties.isDebug()
                && (request.getSession().getAttribute("code") == null
                    || Integer.parseInt((String)request.getSession().getAttribute("code")) != code)) {
            return buildResult(ErrorCode.INCORRECT_VERIFY_CODE, "Incorrect verify code");
        }
        Customer customer;
		if (customers.size() == 0) {
            // 该顾客从未登录过，创建帐号和Profile
			customer = new Customer();
			customer.token = UUID.randomUUID().toString();
			customer.mobile = mobile;
			customer = customerRepository.save(customer);

			profile = new CustomerAppProfile();
			profile.customer = customer;
			profile.nickName = mobile;
            profile.vehicles = new ArrayList<>();
            profile = customerProfileRepository.save(profile);

			SecurityContextHolder.getContext().setAuthentication(new RunAsUserToken(
					customer.mobile, customer, null, null, null));

		} else {
            customer = customers.get(0);
			profile = customerProfileRepository.findAppProfileByCustomer(customer);

            // ERP端有顾客的Profile，但App端没有登录过没有Profile，从ERP端复制车型信息过来。
            if (profile == null) {
                profile = new CustomerAppProfile();
                profile.customer = customer;
                profile.nickName = mobile;
                profile.vehicles = new ArrayList<>();

                // 如果用户在ERP端创建过资料，复制到App端
                CustomerERPProfile erpProfile = customerProfileRepository.findUsableERPProfileByMobile(mobile);
                if (erpProfile != null) {
                    final CustomerAppProfile finalProfile = profile;
                    erpProfile.vehicles.stream().forEach(vehicleInfo -> {
                        finalProfile.vehicles.add(vehicleInfo.clone());
                    });
                }
                profile = customerProfileRepository.save(profile);
            }
		}

		CustomerData data = CustomerData.build(customer, profile,
                customerPurchasedSuiteRepository,
                presaleOrderRepository,
                settleOrderRepository);
		return buildSuccessResult(data);
	}

	/**
	 * 验证手机号码 /api/verify_mobile/{mobileNumber}
	 * @param mobileNumber
	 * @return
	 */
	@RequestMapping(value = "/api/verify_mobile/{mobileNumber}", method = RequestMethod.GET)
	public Map<String, Object> verifyMobile(@PathVariable String mobileNumber, HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval((int)TimeUnit.SECONDS.convert(
                carmoreProperties.getVerifyCodeExpireTimeInMinutes(), TimeUnit.MINUTES));
        StringBuilder verifyCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            verifyCode.append(random.nextInt(10));
        }
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(String.format(carmoreProperties.getSMSUri(),
                carmoreProperties.getSMSUser(), carmoreProperties.getSMSPass(),
                mobileNumber, "您的验证码是:" + verifyCode.toString() + "," +
                        carmoreProperties.getVerifyCodeExpireTimeInMinutes() + "分钟内有效."), String.class);
        String smsStatus = result.split("\n")[0].split(",")[1];
        if (!"0".equals(smsStatus)) {
            String errorMessage = String.format("Fail to send message to mobile number %s, error code: %s",
                    mobileNumber, smsStatus);
            log.error(errorMessage);
            return buildResult(ErrorCode.SMS_FAIL,  errorMessage);
        }

        session.setAttribute("code", verifyCode.toString());
        return buildSuccessResult(
            carmoreProperties.isDebug() ? verifyCode.toString() : null);
	}

    /**
     * 活动列表 /api/campaigns
     * @param page
     * @param rows
     * @return
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/campaigns", method = RequestMethod.GET)
    public Map<String, Object> findCampaigns(@RequestParam int page, @RequestParam int rows) {
        PageRequest pageRequest = new PageRequest(page-1, rows, Sort.Direction.DESC, "publishDate");
        return buildSuccessResult(campaignRepository.findAll(pageRequest).getContent());
    }

    /**
     * 首页广告 /api/promotions
     * @param page
     * @param rows
     * @return
     */
    @Transactional(readOnly = true)
    @RequestMapping(value = "/api/promotions", method = RequestMethod.GET)
    public Map<String, Object> findPromotions(@RequestParam int page, @RequestParam int rows) {
        PageRequest pageRequest = new PageRequest(page-1, rows, Sort.Direction.DESC, "publishDate");
        return buildSuccessResult(campaignRepository.findCampaignByOnBanner(true, pageRequest).getContent());
    }
}
