package com.daqula.carmore.exception;

public class BizException extends BaseException {

    private short errorCode;

    public BizException(short errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizException(short errorCode, String message, Throwable e) {
        super(message + ": " + e.getMessage(), e);
        this.errorCode = errorCode;
    }

    @Override
    public short getErrorCode() {
        return errorCode;
    }
}

