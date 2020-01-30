package com.github.danshan.asrassist.xfyun.event;

import com.github.danshan.asrassist.xfyun.model.FileSlice;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.model.EventType;
import lombok.Data;

@Data
public class Event {
    private EventType type;
    private UploadParams params;
    private FileSlice fileSlice; // TODO file_slice
    private int retryTimes;
    private long activeTimeMillis; // TODO activeTimeTimeMillis

    public Event(EventType type, UploadParams params) {
        this.type = EventType.LFASR_FILE_DATA_CONTENT;
        this.activeTimeMillis = -1L;
        this.type = type;
        this.params = params;
    }

    public void addRetryTimes() {
        ++this.retryTimes;
    }

    public boolean canActive() {
        if (this.activeTimeMillis == -1L) {
            return true;
        } else {
            return System.currentTimeMillis() > this.activeTimeMillis;
        }
    }
}
