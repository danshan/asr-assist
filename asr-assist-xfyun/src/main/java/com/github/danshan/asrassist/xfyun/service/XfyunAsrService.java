package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.model.Progress;
import com.github.danshan.asrassist.xfyun.model.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface XfyunAsrService {

    Optional<String> upload(File file) throws LfasrException, IOException;

    Optional<Progress> getProgress(String taskId) throws LfasrException;

    Optional<List<Result>> getResult(String taskId) throws LfasrException;

}
