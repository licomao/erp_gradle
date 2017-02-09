package com.daqula.carmore.exception;

import com.daqula.carmore.ErrorCode;

/**
 * The super class for exceptions
 */
public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 2846815275271113791L;

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public short getErrorCode() {
        return ErrorCode.UNKNOWN_ERROR;
    }

    public String getSource() {
        StackTraceElement element = getInterestingStackTraceElement(this);
        if (element == null) return null;
        String fullClassName = element.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        return String.format("%s.%s(%s)", className, methodName, lineNumber);
    }

    public static StackTraceElement getInterestingStackTraceElement(Throwable cause) {
        for (StackTraceElement stackTraceElement : cause.getStackTrace()) {
            if (stackTraceElement.getLineNumber() > 0) {
                return stackTraceElement;
            }
        }
        return null;
    }

    public static String summary(Throwable cause) {
        StackTraceElement element = getInterestingStackTraceElement(cause);
        if (element == null) return null;
        String fullClassName = element.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        String methodName = element.getMethodName();
        int lineNumber = element.getLineNumber();
        return String.format("%s : %s.%s(%s) : %s", cause.getClass().getSimpleName(),
                className, methodName, lineNumber, cause.getLocalizedMessage());
    }

}