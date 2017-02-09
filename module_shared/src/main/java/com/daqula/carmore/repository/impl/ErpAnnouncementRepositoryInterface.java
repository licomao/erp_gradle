package com.daqula.carmore.repository.impl;

import com.daqula.carmore.model.announcement.ErpAnnouncement;

import java.util.List;

/**
 * Created by Administrator on 2015/9/7.
 */
public interface ErpAnnouncementRepositoryInterface {

    List<ErpAnnouncement> findAnnounceByTittleAndUnameLike(String tittle, String uname ,int page, int row, String sord);

}
