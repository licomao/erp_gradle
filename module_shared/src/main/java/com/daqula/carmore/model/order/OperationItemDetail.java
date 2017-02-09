package com.daqula.carmore.model.order;

import com.daqula.carmore.model.BaseEntity;
import com.daqula.carmore.model.admin.OperationItem;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by mdc on 2015/10/13.
 */
@Entity
public class OperationItemDetail extends BaseEntity {

    @OneToOne
    public OperationItem operationItem;

    public double sum;

}
