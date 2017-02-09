package com.daqula.carmore.repository;

import com.daqula.carmore.model.template.VehicleModel;
import com.daqula.carmore.repository.impl.VehicleModelRepositoryInterface;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface VehicleModelRepository extends
        PagingAndSortingRepository<VehicleModel, Long>, VehicleModelRepositoryInterface {

    @Query("select distinct v.brand from VehicleModel v ")
    List<String> getBrandNames();

    @Query("select distinct v.line from VehicleModel v where v.brand=?1")
    List<String> getBrandLines(String brandName);

    @Query("select distinct v.producedYear from VehicleModel v where v.brand=?1 and v.line=?2")
    List<String> getYears(String brandName, String brandLine);

//    @Query("select distinct v.version from VehicleModel v where v.brand=?1 and v.line=?2")
//    List<String> getBrandVersions(String brandName, String brandLine);

//    VehicleModel findByVersionAndForSale(String version, String forSale);

    @Query("select distinct v.version,v.id from VehicleModel v where v.brand=?1 and v.line=?2")
    List<VehicleModel> getBrandVersions(String brandName, String brandLine);

}
