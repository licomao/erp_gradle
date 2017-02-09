package com.daqula.carmore.model.admin;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.BaseEntity;

import javax.persistence.*;
import java.util.List;

/**
 * 保养套餐
 */
@Entity
public class CareSuite extends BaseEntity {

    /** 上路后多少个月需要做的保养 */
    @ApiJsonIgnore
    public int month;

    /** 上路经过多少里程后需要做的保养 */
    @ApiJsonIgnore
    public int mileage;

    /**
     * 保养套餐名
     */
    public String name;
    /**
     * 套餐项目组，组内项目可替换
     */
    @OneToMany(cascade = CascadeType.ALL,
                orphanRemoval = true)
    @JoinColumn(name = "care_suite_id")
    public List<CareSuiteGroup> careSuiteGroups;

}
