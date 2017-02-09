package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.template.FingerPrintScanner;

/**
 * Created by mdc on 2016/6/6.
 */
public interface FingerPrintScannerRepositoryInterface {

    FingerPrintScanner findBySensorSnWithNoCache(String sensorSN);
}
