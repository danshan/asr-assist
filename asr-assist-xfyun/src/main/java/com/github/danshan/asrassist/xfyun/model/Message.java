package com.github.danshan.asrassist.xfyun.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@NoArgsConstructor
public class Message {
    private int ok = 0;
    private int errNo = 0;
    private String failed;
    private String data;

    public static Message ok(String data) {
        Message msg = new Message();
        msg.data = data;
        return msg;
    }

    public static Message failed(ErrorCode error, String data) {
        Message msg = new Message();
        msg.errNo = error.code;
        msg.failed = error.error;
        msg.data = StringUtils.trimToEmpty(data);
        return msg;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
