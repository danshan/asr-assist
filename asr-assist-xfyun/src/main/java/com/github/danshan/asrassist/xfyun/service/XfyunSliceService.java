package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.event.UploadEventHandler;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.http.dto.UploadReq;

import java.io.IOException;
import java.util.Set;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface XfyunSliceService {
    UploadEventHandler sliceFile(UploadReq uploadReq, ChannelFileReader reader, Set<String> finishedSliceIds) throws IOException;

    void setFileEnd(UploadEventHandler eventHandler, UploadReq uploadReq);
}
