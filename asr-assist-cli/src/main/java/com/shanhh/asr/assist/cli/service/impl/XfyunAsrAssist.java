package com.shanhh.asr.assist.cli.service.impl;

import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.shanhh.asr.assist.cli.service.AsrAssist;
import com.shanhh.asr.assist.xfyun.service.XfyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Service
@Slf4j
public class XfyunAsrAssist implements AsrAssist {

    @Resource
    private XfyunService xfyunService;

    @Override
    public Optional<String> voiceToText(File file, int interval) {
        String taskId = xfyunService.uploadFile(file).orElseThrow(() -> new IllegalStateException("upload failed"));

        while (true) {
            try {
                Thread.sleep(2 * 1000);
                ProgressStatus progress = xfyunService.getProgressStatus(taskId).orElseThrow(() -> new IllegalStateException("get progress failed"));
                log.info("{}, {}", progress.getStatus(), progress.getDesc());
                if (progress.getStatus() == 9) {
                    break;
                }
            } catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }

        return xfyunService.getResult(taskId).map(result -> xfyunService.resultToMarkdown(file, result, interval));
    }
}
