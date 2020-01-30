package com.github.danshan.asrassist.xfyun.worker;

import com.github.danshan.asrassist.xfyun.event.Event;
import com.github.danshan.asrassist.xfyun.event.EventHandler;
import com.github.danshan.asrassist.xfyun.model.FileSlice;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.exception.SliceException;
import com.iflytek.msp.cpdb.lfasr.file.ChannelFileReader;
import com.iflytek.msp.cpdb.lfasr.model.EventType;
import com.iflytek.msp.cpdb.lfasr.util.DictUtil;

import java.util.Map;

public class SliceWorker {
    private EventHandler eventHandler;
    private DictUtil aTOz = new DictUtil("aaaaaaaaa`");
    private Map<String, String> hm;
    private UploadParams params;

    public SliceWorker(UploadParams params, long file_piece_size, boolean isResume, Map<String, String> hm) {
        this.hm = hm;
        this.params = params;
        this.eventHandler = new EventHandler(params, file_piece_size, isResume);
    }

    public void sliceFile(ChannelFileReader reader) throws SliceException {
        if (reader == null) {
            throw new SliceException("{\"ok\":\"-1\", \"err_no\":\"26404\", \"failed\":\"转写上传文件读取错误!\", \"data\":\"\"}");
        } else {
            try {
                while(reader.read() != -1) {
                    String slice_id = this.aTOz.getNextString();
                    if (!this.hm.containsKey(slice_id)) {
                        this.eventHandler.modifySliceHM(slice_id, false);
                        Event event = new Event(EventType.LFASR_FILE_DATA_CONTENT, this.params);
                        event.setFileSlice(new FileSlice(slice_id, reader.getArray()));
                        this.eventHandler.addEvent(event);
                    }
                }

            } catch (Exception var4) {
                throw new SliceException("{\"ok\":\"-1\", \"err_no\":\"26404\", \"failed\":\"转写上传文件读取错误!\", \"data\":\"\"}");
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
