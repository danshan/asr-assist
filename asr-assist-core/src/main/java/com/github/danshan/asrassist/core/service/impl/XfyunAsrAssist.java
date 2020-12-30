package com.github.danshan.asrassist.core.service.impl;

import com.github.danshan.asrassist.core.service.AsrAssist;
import com.github.danshan.asrassist.xfyun.config.XfyunAsrConfig;
import com.github.danshan.asrassist.xfyun.model.Progress;
import com.github.danshan.asrassist.xfyun.service.XfyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Service
@ConditionalOnClass(XfyunAsrAssist.class)
@ConditionalOnBean(XfyunAsrConfig.class)
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
                Progress progress = xfyunService.getProgress(taskId).orElseThrow(() -> new IllegalStateException("get progress failed"));
                log.info("taskId=[{}], progress=[{}], [{}]", taskId, progress.getStatus(), progress.getDesc());
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
