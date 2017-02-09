package com.daqula.carmore.model.admin;

import com.daqula.carmore.model.BaseEntity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 作业项目，国家标准，门店不能修改
 */
@Entity
public class OperationItem extends BaseEntity {

    /** 排量分类*/
    public final static String LEVEL_A = "A";
    public final static String LEVEL_B = "B";
    public final static String LEVEL_C = "C";
    public final static String LEVEL_D = "D";
    public final static String LEVEL_E = "E";

    /**工时分类*/
    public final static int OPERATION_TYPE_ONE = 1;
    public final static int OPERATION_TYPE_TWO = 2;
    public final static int OPERATION_TYPE_THREE = 3;
    public final static int OPERATION_TYPE_FOUR = 4;
    public final static int OPERATION_TYPE_FIVE = 5;
    public final static int OPERATION_TYPE_SIX = 6;
    public final static int OPERATION_TYPE_SEVEN = 7;
    public final static int OPERATION_TYPE_EIGHT = 8;
    public final static int OPERATION_TYPE_NIGH = 9;
    public final static int OPERATION_TYPE_TEN = 10;
    public final static int OPERATION_TYPE_ELEVEN = 11;
    public final static int OPERATION_TYPE_TWELVE = 12;
    public final static int OPERATION_TYPE_THREETEEN = 13;



    /** 工时，手工费=工时*工时费 */
    public double laborHours;

    /**作业项目名称 */
    public String name;

    /** 车排量档次 */
    public String carLevel;

    /** 工时类型*/
    public int operationType;

    public String getStrType(){
        switch (operationType) {
            case OPERATION_TYPE_ONE:
                return "维 护";
            case OPERATION_TYPE_TWO:
                return "大修和全车喷漆";
            case OPERATION_TYPE_THREE:
                return "发动机机械";
            case OPERATION_TYPE_FOUR:
                return "发动机电气";
            case OPERATION_TYPE_FIVE:
                return "变速箱";
            case OPERATION_TYPE_SIX:
                return "转向系统";
            case OPERATION_TYPE_SEVEN:
                return "悬挂系统";
            case OPERATION_TYPE_EIGHT:
                return "驱动桥";
            case OPERATION_TYPE_NIGH:
                return "制动系统";
            case OPERATION_TYPE_TEN:
                return "电 气";
            case OPERATION_TYPE_ELEVEN:
                return "空 调";
            case OPERATION_TYPE_TWELVE:
                return "钣 金";
            case OPERATION_TYPE_THREETEEN:
                return "喷 漆";
            default:
                return "";
        }
    }

}
