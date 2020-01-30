package com.github.danshan.asrassist.xfyun.worker;

import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.file.ChannelFileReader;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class UploadThread implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(UploadThread.class);
    private UploadParams params;
    private int filePieceSize;
    private ChannelFileReader fr;

    public UploadThread(UploadParams params, int filePieceSize, ChannelFileReader fr) {
        this.params = params;
        this.filePieceSize = filePieceSize;
        this.fr = fr;
    }

    public void run() {
        try {
            SliceWorker sw = new SliceWorker(this.params, this.filePieceSize, false, new HashMap());
            sw.sliceFile(this.fr);

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
            if (this.fr != null) {
                try {
                    this.fr.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

    }
}
