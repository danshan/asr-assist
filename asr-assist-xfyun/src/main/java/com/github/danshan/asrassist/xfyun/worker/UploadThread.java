package com.github.danshan.asrassist.xfyun.worker;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class UploadThread implements Runnable {

    private final XfyunAsrProperties xfyunAsrProperties;
    private final UploadParams params;
    private final ChannelFileReader fileReader;

    public UploadThread(XfyunAsrProperties xfyunAsrProperties, UploadParams params, ChannelFileReader fileReader) {
        this.xfyunAsrProperties = xfyunAsrProperties;
        this.params = params;
        this.fileReader = fileReader;
    }

    public void run() {
        try {
            SliceWorker sw = new SliceWorker(this.xfyunAsrProperties, this.params, new HashMap());
            sw.sliceFile(this.fileReader);

            while(!sw.getEventHandler().isSendAll()) {
                try {
                    Thread.sleep(500L);
                } catch (Exception var12) {
                }
            }

            log.info("taskId=[{}], upload file send success, file=[{}]", this.params.getTaskId(), this.params.getFile().getAbsolutePath());
            sw.setFileEnd();
            sw.getEventHandler().await();
        } catch (Exception ex) {
            log.warn("taskId=[{}], upload file send faild, file=[{}], {}", this.params.getTaskId(), this.params.getFile().getAbsolutePath(), ex.getMessage());
        } finally {
            if (this.fileReader != null) {
                try {
                    this.fileReader.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

    }
}
