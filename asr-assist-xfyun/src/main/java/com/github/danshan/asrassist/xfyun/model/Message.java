package com.github.danshan.asrassist.xfyun.model;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class Message {
    private int ok = 0;
    private int errNo; // TODO err_no
    private String failed;
    private String data;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
