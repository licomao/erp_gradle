package com.daqula.carmore;

public interface ErrorCode {

    short OK = 0;

    short UNKNOWN_ERROR = 1000;
    short AUTHORIZATION_REQUIRED = 1001;
    short INTERNAL_SERVER_ERROR = 1002;
    short ENTITY_NOT_FOUND = 1003;

    short VEHICLE_MODEL_NOT_SET = 2001;
    short EDITED_VEHICLE_INFO_EXISTED = 2002;
    short CHARGE_NOT_PAID = 2003;

    short SMS_FAIL = 2004;
    short INCORRECT_VERIFY_CODE = 2005;

    short UNFINISHED_APPOINTMENT = 3001;
    short NO_TIMES_LEFT = 3002;
    short INVALID_PAYMENT = 3003;
    short DUPLICATED_PURCHASED = 3004;

}
