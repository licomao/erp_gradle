package com.daqula.carmore.repository;

import com.daqula.carmore.model.admin.Campaign;
import com.daqula.carmore.repository.impl.CampaignRepositoryInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CampaignRepository extends PagingAndSortingRepository<Campaign, Long>, CampaignRepositoryInterface, JpaSpecificationExecutor {

    Page<Campaign> findCampaignByOnBanner(boolean onBanner, Pageable pageable);

    @Query("SELECT c FROM Campaign c left join fetch c.shop WHERE c.id = ?1")
    Campaign findByIdNotlazy (Long id);
}
