package com.daqula.carmore.repository;

import com.daqula.carmore.model.customer.CustomerERPProfile;
import com.daqula.carmore.model.customer.VehicleInfo;
import com.daqula.carmore.model.shop.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface VehicleInfoRepository extends PagingAndSortingRepository<VehicleInfo, Long>{
    VehicleInfo findByPlateNumber(String plateNumber);

    @Query("select v from VehicleInfo v, CustomerERPProfile p where v MEMBER OF p.vehicles and  v.plateNumber =?1 and p.organization = ?2 and p.deleted = false ORDER BY v.createdDate DESC ")
    List<VehicleInfo> findByPlateNumberInCustomerERPProfile(String plateNumber, Organization organization);

    @Query("select v from VehicleInfo v, CustomerAppProfile p where v MEMBER OF p.vehicles and  v.plateNumber =?1 and p.deleted = false")
    VehicleInfo findByPlateNumberInCustomerAppProfile(String plateNumber);

    Page<VehicleInfo> findByPlateNumber(String plateNumber, Pageable pageable);


    @Query("select v,p.realName,p.gender,p.customer.mobile from VehicleInfo v, CustomerERPProfile p where v MEMBER OF p.vehicles and v.plateNumber like %?1% and p.organization = ?2 and p.deleted = false")
    Page<Object[]> findVehicleByPlateNumberAndOrganization(String plateNumber, Organization organization, Pageable pageable);

    @Query("select v,p.realName,p.gender,p.customer.mobile from VehicleInfo v, CustomerERPProfile p where v MEMBER OF p.vehicles and v.plateNumber like %?1% and p.customer.mobile like %?2% and p.organization = ?3 and p.deleted = false")
    Page<Object[]> findVehicleByPlateNumberAndTelAndOrganization(String plateNumber, String tel, Organization organization, Pageable pageable);

    @Query("select v,p.realName,p.gender,p.customer.mobile from CustomerERPProfile p left join p.vehicles v  where p.customer.mobile like %?1% and p.organization = ?2 and p.deleted = false")
    Page<Object[]> findVehicleByTelAndOrganization(String tel,Organization organization, Pageable pageable);

    @Query("select v,p.realName,p.gender,p.customer.mobile from CustomerERPProfile p left join p.vehicles v  where p.organization = ?1 and p.deleted = false")
    Page<Object[]> findVehicleByOrganization(Organization organization, Pageable pageable);

    @Query("select v,p.realName,p.gender,p.customer.mobile from VehicleInfo v, CustomerERPProfile p where v MEMBER OF p.vehicles and v.plateNumber like %?1% and p.organization = ?2 and p.deleted = false")
    List<Object[]> findVehicleByPlateNumberAndOrganization(String plateNumber, Organization organization);

    @Query("select v,p.realName,p.gender,p.customer.mobile from VehicleInfo v, CustomerERPProfile p where v MEMBER OF p.vehicles and v.plateNumber like %?1% and p.customer.mobile like %?2% and p.organization = ?3 and p.deleted = false")
    List<Object[]> findVehicleByPlateNumberAndTelAndOrganization(String plateNumber, String tel, Organization organization);

    @Query("select v,p.realName,p.gender,p.customer.mobile from CustomerERPProfile p left join p.vehicles v  where p.customer.mobile like %?1% and p.organization = ?2 and p.deleted = false")
    List<Object[]> findVehicleByTelAndOrganization(String tel,Organization organization);

    @Query("select v,p.realName,p.gender,p.customer.mobile from CustomerERPProfile p left join p.vehicles v  where p.organization = ?1 and p.deleted = false")
    List<Object[]> findVehicleByOrganization(Organization organization);

    VehicleInfo findByUid(UUID uid);

}