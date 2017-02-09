package com.daqula.carmore.model.template;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.Entity;

/**
 * 城市列表
 */
@Entity
public class City extends BaseEntity {

    public static final int AREA_SOUTH = 0;
    public static final int AREA_EAST = 1;
    public static final int AREA_NORTH = 2;
    public static final int AREA_WEST = 3;

    public String name;

    public int area;

}
