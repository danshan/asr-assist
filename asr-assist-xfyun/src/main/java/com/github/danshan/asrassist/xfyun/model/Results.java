package com.github.danshan.asrassist.xfyun.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results implements Serializable {

    private List<Result> list;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonCreator
    public Results jsonCreator(String data) throws JsonProcessingException {
        Results results = new Results();
        results.setList(Lists.newArrayList(objectMapper.readValue(data, Result[].class)));
        return results;
    }
}
