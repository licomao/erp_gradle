package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.shop.Shop;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ShopRepositoryImpl implements ShopRepositoryInterface {

    @Autowired
    private EntityManager em;

        // 113.328572,23.143682 体育中心
		// 113.372697,23.134377 天河公园
		// 113.31348,23.137568 东峻广场
		// 113.3096,23.392413 白云机场
/*        The SQL statement that will find the closest 20 locations that are within a radius
of 30 miles to the 78.3232, 65.3234 coordinate. It calculates
the distance based on the latitude/longitude of that row and the
 target latitude/longitude, and then asks for only rows where the distance
 value is less than 30 miles, orders the whole query by distance,
 and limits it to 20 results. To search by kilometers instead of miles, replace 3959 with 6371(地球半径/km).

SELECT id, (3959 * acos (cos ( radians(78.3232) )* cos( radians( lat ) )* cos( radians( lng )
- radians(65.3234) )+ sin ( radians(78.3232) )* sin( radians( lat ) ))) AS distance
FROM markers
HAVING distance < 30
ORDER BY distance
LIMIT 0 , 20;*/

    @Override
    public List<Shop> findShopNearBy(double lat, double lng, int page, int pageSize, int radiusInKilometer) {
        // 参数1和3为lat,2为lng
        // 因为使用了NativeQuery，查询的SELECT后面要带上所有的fields
        Query q = em.createNativeQuery(
                "SELECT id,organization_id,deleted,name,address,opening_hours," +
                        "phone,description,latitude,longitude,promotion_tag," +
                        "created_by,created_date,updated_by,updated_date,ver,uid," +
                        "image_url,rating,rating_count,shop_code,shop_type," +
                        "6371*acos(" +
                            "cos(radians(?1))*cos(radians(latitude))*" +
                            "cos(radians(longitude)-radians(?2))+" +
                            "sin(radians(?3))*sin(radians(latitude))" +
                        ") AS distance,city_id " +
                "FROM shop HAVING distance <= ?4 " +
                "ORDER BY distance ", Shop.class)
                .setMaxResults(pageSize)
                .setFirstResult((page-1)*pageSize);
        q.setParameter(1, lat);
        q.setParameter(2, lng);
        q.setParameter(3, lat);
        q.setParameter(4, radiusInKilometer);
        List<Shop> r = q.getResultList();
        return r;
    }

    @Override
    public List<Shop> findShopByNameAndOrgLike(String orgName, String shopName ,int page, int row, String sord) {

        int max = page*row;
        StringBuilder sql = new StringBuilder("SELECT * from shop s ");
        sql.append("WHERE s.organization_id in (SELECT a.id FROM organization a WHERE a.`name`  LIKE '%"+orgName+"%') AND s.`name` LIKE '%"+shopName+"%' ");
        sql.append("order by id ").append(sord);

        Query q = em.createNativeQuery(sql.toString(),Shop.class);
        q.setFirstResult(max - row);
        q.setMaxResults(max -1);
        List ret = q.getResultList();

        return ret;
    }

    @Override
    public int isCodeCanUse(String code,long id,long orgId) {
        StringBuilder sql = new StringBuilder("SELECT * FROM shop WHERE shop_code LIKE '%"+ code +"%'");
        sql.append(" and organization_id = " + orgId);
        Query q = em.createNativeQuery(sql.toString(),Shop.class);
        List ret = q.getResultList();
        if(ret.size() > 0) {
            return 0;
        }
        return 1;
    }
}
