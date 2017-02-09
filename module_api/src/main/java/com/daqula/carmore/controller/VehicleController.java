package com.daqula.carmore.controller;

import com.daqula.carmore.model.template.VehicleModel;
import com.daqula.carmore.repository.VehicleModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.daqula.carmore.utils.JsonResultBuilder.buildSuccessResult;

@RestController
public class VehicleController {

    @Autowired
    private VehicleModelRepository vehicleModelRepository;

    /**
     * 获取品牌 /api/vehicle/brand
     * @return {"result":["DS","Jeep","MG","MINI","Smart","奥迪"],"retCode":0,"msg":"","svrTime":1435208772626}
     */
    @RequestMapping(value = "/api/vehicle/brand", method = RequestMethod.GET)
	public Map<String, Object> getVehicleBrands() {
		return buildSuccessResult(vehicleModelRepository.getBrandNames());
	}

    /** 获取品牌下面的车系列表 /api/vehicle/line
      *
      * @param brandName 品牌名称
      * @return {"result":["大切诺基","指南者","指挥官","牧马人","自由光","自由客"],"retCode":0,"msg":"","svrTime":1435209589730}
      */
    @RequestMapping(value = "/api/vehicle/line", method = RequestMethod.GET)
    public Map<String, Object> getBrandLines(String brandName) {
        return buildSuccessResult(vehicleModelRepository.getBrandLines(brandName));
    }

    @RequestMapping(value = "/api/vehicle/year", method = RequestMethod.GET)
    public Map<String, Object> getYears(String brandName, String line) {
        return buildSuccessResult(vehicleModelRepository.getYears(brandName, line));
    }

    /** 获取车系版本列表  /api/vehicle/version
      *
      * @param brandName 品牌名称
      * @param line 车系
      * @return {"result":[{"id":12,"version":"1.6T 手自一体 风尚版 2012款"},{"id":13,"version":"1.6T 手自一体 雅致版 2012款"}],"msg":"","svrTime":1437322235506,"retCode":0}
      */
    @RequestMapping(value = "/api/vehicle/version", method = RequestMethod.GET)
    public Map<String, Object> getVersions(@RequestParam String brandName,
                                           @RequestParam String line,
                                           @RequestParam Optional<String> year) {
        List<VehicleModel> vm = vehicleModelRepository.getVersions(brandName, line, year);
        return buildSuccessResult(vm);
    }
}