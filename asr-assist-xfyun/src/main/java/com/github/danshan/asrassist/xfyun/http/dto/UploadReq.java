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
public class UploadReq extends BaseReq implements Cloneable {
    /**
     * 任务ID（预处理接口返回值）
     */
    @JsonProperty("task_id")
    private String taskId;
    @JsonProperty("slice_id")
    private String sliceId;

    public UploadReq(Signature signature) {
        super(signature);
    }

    public UploadReq clone() throws CloneNotSupportedException {
        return (UploadReq) super.clone();
    }
}
