package com.github.danshan.asrassist.xfyun.worker;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class UploadThread implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(UploadThread.class);

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

            LOGGER.info(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadThread", this.params.getTaskId(), "", "(-1) ms", "upload file send success, file:" + this.params.getFile().getAbsolutePath() + ", task_id:" + this.params.getTaskId()));
            sw.setFileEnd();
            sw.getEventHandler().await();
        } catch (Exception var13) {
            LOGGER.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "UploadThread", this.params.getTaskId(), "", "(-1) ms", "upload file send error, file:" + this.params.getFile().getAbsolutePath() + ", task_id:" + this.params.getTaskId()), var13);
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
