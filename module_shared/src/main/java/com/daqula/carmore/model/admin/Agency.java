package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.acl.ERPUser;
import com.daqula.carmore.model.shop.Organization;

import javax.persistence.*;
import java.util.List;

/**代理商
 * Created by mdc on 2015/10/10.
 */
@Entity
public class Agency extends BaseEntity {

    @OneToOne
    public ERPUser erpUser;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "agency_id")
    public List<Organization> organizations;
}
