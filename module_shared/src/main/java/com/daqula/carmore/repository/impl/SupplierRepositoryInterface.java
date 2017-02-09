package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.Supplier;

import java.util.List;
/**
 * Created by thy on 2015/9/11.
 */
public interface SupplierRepositoryInterface {

    List<Supplier>  findSupplierByNameAndOrgid(String name, Long orgid ,int page, int row, String sord);

}
