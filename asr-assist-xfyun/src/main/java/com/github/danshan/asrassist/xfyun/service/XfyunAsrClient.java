package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.model.LfasrType;
import com.github.danshan.asrassist.xfyun.model.Message;

import java.io.File;
import java.util.Map;

public interface XfyunAsrClient {

    Message upload(File file, LfasrType type) throws LfasrException;

    Message upload(File file, LfasrType type, Map<String, String> params) throws LfasrException;

    void resume() throws LfasrException;

    Message getVersion() throws LfasrException;

    Message getProgress(String taskId) throws LfasrException;

    Message getResult(String taskId) throws LfasrException;

}
