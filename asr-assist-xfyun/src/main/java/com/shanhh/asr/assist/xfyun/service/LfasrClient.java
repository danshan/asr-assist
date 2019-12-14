package com.shanhh.asr.assist.xfyun.service;

import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;

import java.io.File;
import java.util.Map;

public interface LfasrClient {

    Message upload(File file, LfasrType type) throws LfasrException;

    Message upload(File file, LfasrType type, Map<String, String> params) throws LfasrException;

    void resume() throws LfasrException;

    Message getVersion() throws LfasrException;

    Message getProgress(String taskId) throws LfasrException;

    Message getResult(String taskId) throws LfasrException;

}
