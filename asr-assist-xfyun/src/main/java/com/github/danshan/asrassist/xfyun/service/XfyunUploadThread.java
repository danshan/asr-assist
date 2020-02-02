package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.event.UploadEventHandler;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.http.dto.UploadReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class XfyunUploadThread implements Runnable {

    private final XfyunSliceService xfyunSliceService;
    private final UploadReq uploadReq;
    private final ChannelFileReader reader;

    public void run() {
        try {
            UploadEventHandler eventHandler = xfyunSliceService.sliceFile(uploadReq, reader, new HashSet<>());

            while (!eventHandler.isSendAll()) {
                try {
                    Thread.sleep(500L);
                } catch (Exception ex) {
                    log.warn(ex.getMessage(), ex);
                }
            }

            log.info("taskId=[{}], upload file send success", this.uploadReq.getTaskId());
            xfyunSliceService.setFileEnd(eventHandler, uploadReq);
            eventHandler.await();
        } catch (Exception ex) {
            log.warn(String.format("taskId=[%s], upload file send failed", this.uploadReq.getTaskId()), ex);
        }

    }
}
