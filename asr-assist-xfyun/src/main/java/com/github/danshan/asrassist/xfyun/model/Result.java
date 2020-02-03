package com.github.danshan.asrassist.xfyun.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result implements Serializable {

    /**
     * 句子相对于本音频的起始时间，单位为ms
     */
    @JsonProperty("bg")
    private String begin;

    /**
     * 句子相对于本音频的终止时间，单位为ms
     */
    @JsonProperty("ed")
    private String end;
    /**
     * 句子内容
     */
    @JsonProperty("onebest")
    private String content;

    /**
     * 说话人编号，从1开始，未开启说话人分离时speaker都为0
     */
    private String speaker;

}
