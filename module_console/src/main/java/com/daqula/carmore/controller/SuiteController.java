package com.daqula.carmore.controller;

import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.repository.SuiteRepository;
import com.daqula.carmore.util.JqGridDataGenerator;
import com.daqula.carmore.util.StringUtil;
import org.jadira.usertype.spi.utils.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static com.daqula.carmore.util.CollectionUtil.entry;
import static com.daqula.carmore.util.CollectionUtil.map;

/**
 * Created by thy on 2015/9/14.
 */

@Controller
public class SuiteController {

    @Autowired
    private SuiteRepository suiteRepository;

    /**
     * 供应商信息列表
     * @return
     */
    @RequestMapping(value = "/suite/list")
    public ModelAndView list() {
        return new ModelAndView("/suite/list");
    }


    @RequestMapping("/suite/list/data")
    public @ResponseBody
    Map<String, Object> listData(@RequestParam int page, @RequestParam int rows,
                                 @RequestParam String sord, @RequestParam String sidx,
                                 @RequestParam(required = false) String name) {

        PageRequest pageRequest = new PageRequest(page - 1, rows,
                sord.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                !StringUtils.isEmpty(sidx) ? sidx : "id");

        if (StringUtil.IsNullOrEmpty(name) ) {
            Page pageData = suiteRepository.findAll(pageRequest);

            return JqGridDataGenerator.getDataJson(pageData);
        }else {
            name = "%" + name + "%";
            Page<Suite> pageData = suiteRepository.findByNameLike(name , pageRequest);

            return JqGridDataGenerator.getDataJson(pageData);
        }
    }

    /**
     * 供应商信息列表
     * @return
     */
    @RequestMapping(value = "/suite/form")
    public ModelAndView enw() {
        return new ModelAndView("/suite/form");
    }

}
