package com.github.danshan.asrassist.xfyun.http;

import com.github.danshan.asrassist.xfyun.http.dto.*;
import com.github.danshan.asrassist.xfyun.model.Progress;
import com.github.danshan.asrassist.xfyun.model.Result;
import com.github.danshan.asrassist.xfyun.model.Results;

import java.util.List;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface XfyunRepo {

    Optional<String> prepare(PrepareReq req);

    void upload(UploadReq req, byte[] content, String desc);

    void merge(MergeReq req);

    Optional<Progress> getProgress(ProgressReq req);

    Optional<List<Result>> getResult(ResultReq req);
}
