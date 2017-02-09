package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.template.FingerPrintScanner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;

/**
 * Created by mdc on 2016/6/6.
 */
public class FingerPrintScannerRepositoryImpl implements FingerPrintScannerRepositoryInterface{

    @Autowired
    private EntityManager em;

    @Override
    public FingerPrintScanner findBySensorSnWithNoCache(String sensorSN) {

        FingerPrintScanner singleResult = null;
        Random random = new Random();
        int ran = random.nextInt(10000);

        List list = em.createQuery(
                String.format("SELECT p from FingerPrintScanner p where p.sensorSN = ?1 and %s = %s", ran, ran))
                .setParameter(1, sensorSN)
                .getResultList();

        if (list.size() > 0 ){
            singleResult = (FingerPrintScanner) list.get(0);
        }
        return singleResult;
    }
}
