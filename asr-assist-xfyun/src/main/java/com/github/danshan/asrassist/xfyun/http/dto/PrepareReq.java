package com.github.danshan.asrassist.xfyun.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.danshan.asrassist.xfyun.model.Signature;
import lombok.Getter;
import lombok.Setter;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrepareReq extends BaseReq {

    /**
     * 文件大小（单位：字节）
     */
    @JsonProperty("file_len")
    private Long fileLength;
    /**
     * 文件名称（带后缀）
     */
    @JsonProperty("file_name")
    private String fileName;
    /**
     * 文件分片数目（建议分片大小为10M，若文件<10M，则slice_num=1）
     */
    @JsonProperty("slice_num")
    private Integer sliceCount;

    public PrepareReq(Signature signature) {
        super(signature);
    }

}
