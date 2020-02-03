package com.github.danshan.asrassist.xfyun.event;

import com.github.danshan.asrassist.xfyun.http.dto.UploadReq;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
public class UploadEvent implements Serializable {

    private EventType type;
    private UploadReq uploadReq;
    private byte[] content;
    private int retryTimes;
    private long activeTimeMillis;

    public UploadEvent(EventType type, UploadReq uploadReq) {
        this.type = EventType.DATA_CONTENT;
        this.activeTimeMillis = -1L;
        this.type = type;
        this.uploadReq = uploadReq;
    }

    public void addRetryTimes() {
        this.retryTimes++;
    }

    public boolean canActive() {
        if (this.activeTimeMillis == -1L) {
            return true;
        } else {
            return System.currentTimeMillis() > this.activeTimeMillis;
        }
    }

}
