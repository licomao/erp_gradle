package com.daqula.carmore.model.shop;

import com.daqula.carmore.annotation.ApiJsonIgnore;
import com.daqula.carmore.model.admin.Suite;
import com.daqula.carmore.model.admin.SuiteItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.ArrayList;

/**
 * 门店自定义套餐
 */
@Entity
@DiscriminatorValue(value="1")
public class CustomSuite extends Suite {

    /** 是哪个组织自定义的 */
    @ManyToOne(fetch = FetchType.LAZY)
    @ApiJsonIgnore
    @JsonIgnore
    public Organization organization;

    /** 是否启用 作废 */
    public boolean enabled;

}
