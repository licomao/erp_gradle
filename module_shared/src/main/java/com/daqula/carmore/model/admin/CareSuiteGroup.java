package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保养套餐项目组，组内项目可替换
 */
@Entity
public class CareSuiteGroup extends BaseEntity {

    /** 套餐项目 */
    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "care_suite_group_id")
    public List<CareSuiteGroupItem> careSuiteGroupItems;

    public CareSuiteGroup clone() {
        CareSuiteGroup careSuiteGroup = new CareSuiteGroup();
        careSuiteGroup.careSuiteGroupItems = new ArrayList<>();
        careSuiteGroup.careSuiteGroupItems.addAll(
                careSuiteGroupItems.stream().map(CareSuiteGroupItem::clone).collect(Collectors.toList()));
        return careSuiteGroup;
    }

}
