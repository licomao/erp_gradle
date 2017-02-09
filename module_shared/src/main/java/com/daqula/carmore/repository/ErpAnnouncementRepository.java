package com.daqula.carmore.repository;

import com.daqula.carmore.model.announcement.ErpAnnouncement;
import com.daqula.carmore.model.shop.Organization;
import com.daqula.carmore.repository.impl.ErpAnnouncementRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by mdc on 2015/9/6.
 */
public interface ErpAnnouncementRepository extends CrudRepository<ErpAnnouncement, Long> ,JpaSpecificationExecutor, PagingAndSortingRepository<ErpAnnouncement, Long> ,ErpAnnouncementRepositoryInterface {

    List<ErpAnnouncement> findByOrganizationOrderByPublishDateDesc(Organization organization);
}
