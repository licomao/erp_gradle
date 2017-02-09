package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.CareSuite;

public interface CareSuiteRepositoryInterface {

    CareSuite recommendCareSuite(int mileage, int month, long orgId);

}
