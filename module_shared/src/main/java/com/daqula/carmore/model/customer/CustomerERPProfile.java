package com.daqula.carmore.model.customer;

import com.daqula.carmore.model.shop.Organization;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/** ERP顾客信息，组织与组织之间是隔离的 */
@Entity
@DiscriminatorValue(value=CustomerProfile.PROFILE_TYPE_ERP)
public class CustomerERPProfile extends CustomerProfile {

    /** 真实姓名 */
    public String realName;

    /** 性别 男：0；女：1*/
    public int gender;

    /** 会员等级 */
    public Integer level = 0;

    /** 积分结余 */
    public Integer bonus = 0;

    /** 所属组织 */
    @ManyToOne
    @JsonSerialize(using = IDSerializer.class)
    public Organization organization;

}
