package com.github.danshan.asrassist.xfyun.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@NoArgsConstructor
public class ErrorMsg {
    private int errNo = 0;
    private String failed;

    public static ErrorMsg failed(ErrorCode error) {
        ErrorMsg msg = new ErrorMsg();
        msg.errNo = error.code;
        msg.failed = error.error;
        return msg;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
