package com.github.danshan.asrassist.xfyun.service;


import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;

import java.io.File;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface XfyunService {

    Optional<String> uploadFile(File file);

    Optional<ProgressStatus> getProgressStatus(String taskId);

    Optional<String> getResult(String taskId);

    String resultToMarkdown(File file, String resultJson, int timeInterval);
}
