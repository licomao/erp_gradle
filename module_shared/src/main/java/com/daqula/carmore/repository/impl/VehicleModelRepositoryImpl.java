package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.template.VehicleModel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleModelRepositoryImpl implements VehicleModelRepositoryInterface {

    @Autowired
    private EntityManager em;

    @Override
    public List<VehicleModel> getVersions(String brandName, String line, Optional<String> year) {
        String jpql = "SELECT v.id, v.version, v.price FROM VehicleModel v WHERE v.brand=?1 AND v.line=?2";
        if (year.isPresent()) jpql += " AND v.producedYear=?3";

        Query query = em.createQuery(jpql)
                .setParameter(1, brandName)
                .setParameter(2, line);
        if (year.isPresent()) query.setParameter(3, year.get());

        List<Object[]> result = query.getResultList();

        List<VehicleModel> models = new ArrayList<>();
        for(Object[] obj : result) {
            VehicleModel model = new VehicleModel();
            model.uid = null;
            model.id = (Long)obj[0];
            model.version = (String)obj[1];
            model.price = (String)obj[2];
            model.clazz = model.getVehicleClazz();
            model.price = null;
            models.add(model);
        }
        return models;
    }

}
