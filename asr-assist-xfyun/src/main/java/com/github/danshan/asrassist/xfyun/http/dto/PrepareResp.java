package com.github.danshan.asrassist.xfyun.http.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrepareResp extends BaseResp {
    @JsonProperty("data")
    private String taskId;
}
