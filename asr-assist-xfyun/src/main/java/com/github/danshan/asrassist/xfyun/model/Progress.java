package com.github.danshan.asrassist.xfyun.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Progress implements Serializable {

    private Integer status;
    private String desc;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonCreator
    public static Progress jsonCreator(String data) throws JsonProcessingException {
        return objectMapper.readValue(data, Progress.class);
    }

}
