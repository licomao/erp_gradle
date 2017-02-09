package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.template.VehicleModel;

import java.util.List;
import java.util.Optional;

public interface VehicleModelRepositoryInterface {
    List<VehicleModel> getVersions(String brandName, String line, Optional<String> year);
}
