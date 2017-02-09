package com.daqula.carmore.repository;

import com.daqula.carmore.model.template.FingerPrintScanner;
import com.daqula.carmore.repository.impl.FingerPrintScannerRepositoryInterface;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by swj on 2015/11/6.
 */
public interface FingerPrintScannerRepository extends PagingAndSortingRepository<FingerPrintScanner, Long>, JpaSpecificationExecutor, FingerPrintScannerRepositoryInterface {

    FingerPrintScanner findByPidAndVidAndUsbSn(String pid, String vid, String usbSn);


    FingerPrintScanner findBySensorSN(String sensorSN);
}
