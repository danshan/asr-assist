package com.github.danshan.asrassist.xfyun.worker;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.event.Event;
import com.github.danshan.asrassist.xfyun.event.EventHandler;
import com.github.danshan.asrassist.xfyun.exception.SliceException;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.FileSlice;
import com.github.danshan.asrassist.xfyun.model.Message;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.model.EventType;
import com.iflytek.msp.cpdb.lfasr.util.DictUtil;

import java.util.Map;

public class SliceWorker {
    private final XfyunAsrProperties xfyunAsrProperties;
    private final UploadParams params;
    private final Map<String, String> hm;

    private EventHandler eventHandler;
    private DictUtil aTOz = new DictUtil("aaaaaaaaa`");

    public SliceWorker(XfyunAsrProperties xfyunAsrProperties, UploadParams params, Map<String, String> hm) {
        this.xfyunAsrProperties = xfyunAsrProperties;
        this.hm = hm;
        this.params = params;
        this.eventHandler = new EventHandler(xfyunAsrProperties, params);
    }

    public void sliceFile(ChannelFileReader reader) throws SliceException {
        if (reader == null) {
            throw new SliceException(Message.failed(ErrorCode.ASR_UPLOADFILE_PERMISSION_ERR, null));
        } else {
            try {
                while (reader.read() != -1) {
                    String slice_id = this.aTOz.getNextString();
                    if (!this.hm.containsKey(slice_id)) {
                        this.eventHandler.modifySliceHM(slice_id, false);
                        Event event = new Event(EventType.LFASR_FILE_DATA_CONTENT, this.params);
                        event.setFileSlice(new FileSlice(slice_id, reader.getArray()));
                        this.eventHandler.addEvent(event);
                    }
                }

            } catch (Exception ex) {
                throw new SliceException(Message.failed(ErrorCode.ASR_UPLOADFILE_PERMISSION_ERR, null));
            }
        }
    }

    public void setFileEnd() {
        Event event = new Event(EventType.LFASR_FILE_DATA_END, this.params);
        this.eventHandler.addEvent(event);
    }

    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

}
