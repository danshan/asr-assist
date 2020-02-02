package com.github.danshan.asrassist.xfyun.service;


import com.github.danshan.asrassist.xfyun.model.Progress;
import com.github.danshan.asrassist.xfyun.model.Result;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface XfyunService {

    Optional<String> uploadFile(File file);

    Optional<Progress> getProgress(String taskId);

    Optional<List<Result>> getResult(String taskId);

    String resultToMarkdown(File file, List<Result> results, int timeInterval);
}
