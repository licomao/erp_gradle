package com.daqula.carmore.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.daqula.carmore.ErrorCode.*;

public class JsonResultBuilder {

    public static Map<String, Object> buildSuccessResult() {
        return buildResult(null, OK, "");
    }

    public static Map<String, Object> buildSuccessResult(Object data, String msg) {
        return buildResult(data, OK, msg);
    }

    public static Map<String, Object> buildSuccessResult(Object data) {
        return buildResult(data, OK, "");
    }

    public static Map<String, Object> buildEntityNotFoundResult(String entityName, Long entityId) {
        return buildResult(null, ENTITY_NOT_FOUND,
                String.format("Entity %s identified by %s is not found.", entityName, entityId));
    }

    public static Map<String, Object> buildResult(Object data, int retCode, String msg) {
        Map<String, Object> result = new HashMap<>();
        if (data != null) result.put("result", data);
        if (!StringUtils.isEmpty(msg)) result.put("msg", msg);
        result.put("retCode", retCode);
        result.put("svrTime", DateTime.now().getMillis());
        return result;
    }

    public static Map<String, Object> buildResult(int retCode, String msg) {
        return buildResult(null, retCode, msg);
    }

    public static Map<String, Object> buildResult(Map<String, Object> data, int retCode, String msg) {
        Map<String, Object> result = new HashMap<>();
        if (data != null) {
            data.forEach((key, value) -> {
                result.put(key, value);
            });
        }
        if (!StringUtils.isEmpty(msg)) result.put("msg", msg);
        result.put("retCode", retCode);
        result.put("svrTime", DateTime.now().getMillis());
        return result;
    }

    public static <T> T fromJSON(final TypeReference<T> type,
                                 final String jsonPacket) {
        T data = null;

        try {
            data = new ObjectMapper().readValue(jsonPacket, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
