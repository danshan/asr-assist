package com.github.danshan.asrassist.xfyun.event;

import com.github.danshan.asrassist.xfyun.model.EventType;
import com.github.danshan.asrassist.xfyun.model.FileSlice;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import lombok.Data;

@Data
public class Event {
    private EventType type;
    private UploadParams params;
    private FileSlice fileSlice;
    private int retryTimes;
    private long activeTimeMillis;

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
