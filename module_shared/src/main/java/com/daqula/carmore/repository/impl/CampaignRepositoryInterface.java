package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.Campaign;

import java.util.List;

public interface CampaignRepositoryInterface {

    List<Campaign> findCampaignNearBy(double lat, double lng, int page, int pageSize, int radiusInKilometer);

}
