package com.daqula.carmore.controller;

import com.daqula.carmore.ErrorCode;
import com.daqula.carmore.exception.BaseException;
import com.daqula.carmore.utils.JsonResultBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    private final static Log log = LogFactory.getLog(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public @ResponseBody Map<String, Object> defaultErrorHandler(HttpServletResponse response, Exception e) throws Exception {
        log.error("Encounter Exception :: ", e);
        if (e instanceof BaseException) {
            BaseException ex = (BaseException) e;
            return JsonResultBuilder.buildResult(ex.getErrorCode(), e.getLocalizedMessage() + ex.getSource());

        } else {
            return JsonResultBuilder.buildResult(ErrorCode.INTERNAL_SERVER_ERROR, BaseException.summary(e));
        }
    }
}
