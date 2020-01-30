package com.shanhh.asr.assist.web.controller;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
public class BaseResponse<T> implements Serializable {

    public static final String DEFAULT_CODE = "200";
    public static final String DEFAULT_MESSAGE = "ok";

    private String code;
    private String message;
    private T data;

    public BaseResponse(String code, String message, String extraCode, String extraMessage, T data) {
        this.code = StringUtils.defaultIfBlank(code, DEFAULT_CODE);
        this.message = StringUtils.defaultIfBlank(message, DEFAULT_MESSAGE);
        this.data = data;
    }

    public BaseResponse(String code, String message, T data) {
        this(code, message, null, null, data);
    }

    public BaseResponse(T data) {
        this(null, null, data);
    }

    public BaseResponse() {
        this(null);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
