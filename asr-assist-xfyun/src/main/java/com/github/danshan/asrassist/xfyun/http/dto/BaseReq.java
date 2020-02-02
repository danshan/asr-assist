package com.github.danshan.asrassist.xfyun.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.danshan.asrassist.xfyun.model.Signature;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseReq implements Serializable {

    /**
     * 讯飞开放平台应用ID
     */
    @JsonProperty("app_id")
    private final String appId;
    /**
     * 基于HMACSHA1算法
     */
    @JsonProperty("signa")
    private final String signature;
    /**
     * 从1970年1月1日0点0分0秒开始到现在的秒数
     */
    @JsonProperty("ts")
    private final Long timestamp;

    public BaseReq(Signature signature) {
        this.appId = signature.getAppId();
        this.signature = signature.getSignature();
        this.timestamp = signature.getTimestamp();
    }
}
