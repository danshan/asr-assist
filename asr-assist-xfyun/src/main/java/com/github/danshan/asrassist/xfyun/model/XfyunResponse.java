package com.github.danshan.asrassist.xfyun.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@NoArgsConstructor
public class XfyunResponse {
    private int ok = 0;
    @JsonProperty("err_no")
    private int errNo = 0;
    private String failed;
    private String data;

    public ErrorMsg toMessage() {
        ErrorMsg message = new ErrorMsg();
        message.setErrNo(errNo);
        message.setFailed(failed);
        return message;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
