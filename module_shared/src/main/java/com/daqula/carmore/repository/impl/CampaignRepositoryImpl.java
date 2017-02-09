package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.admin.Campaign;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class CampaignRepositoryImpl implements CampaignRepositoryInterface {

    @Autowired
    private EntityManager em;

    @Override
    public List<Campaign> findCampaignNearBy(double lat, double lng, int page,
                                             int pageSize, int radiusInKilometer) {
        // 参数1和3为lat,2为lng
        // 因为使用了NativeQuery，查询的SELECT后面要带上所有的fields
        Query q = em.createNativeQuery(
                "SELECT id,banner_image_url,campaign_type,on_banner,summary," +
                        "url,city_id,latitude,longitude,shop_id,deleted,uid," +
                        "created_by,created_date,updated_by,updated_date,ver," +
                        "6371*acos(" +
                            "cos(radians(?1))*cos(radians(latitude))*" +
                            "cos(radians(longitude)-radians(?2))+" +
                            "sin(radians(?3))*sin(radians(latitude))" +
                        ") AS distance " +
                "FROM campaign HAVING distance <= ?4 " +
                "ORDER BY distance ", Campaign.class)
                .setMaxResults(pageSize)
                .setFirstResult((page-1)*pageSize);
        q.setParameter(1, lat);
        q.setParameter(2, lng);
        q.setParameter(3, lat);
        q.setParameter(4, radiusInKilometer);
        List<Campaign> r = q.getResultList();
        return r;
    }
}
