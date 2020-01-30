//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.danshan.asrassist.xfyun.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.Signature;
import com.iflytek.msp.cpdb.lfasr.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.util.VersionUtil;
import com.iflytek.msp.cpdb.lfasr.worker.HttpWorker;
import com.iflytek.msp.cpdb.lfasr.worker.ResumeWorker;
import com.iflytek.msp.cpdb.lfasr.worker.UploadWorker;
import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.security.SignatureException;
import java.util.Map;

@Service
@Slf4j
public class XfyunAsrClientImpl implements XfyunAsrClient {

    @Resource
    private XfyunAsrProperties xfyunAsrProperties;

    @Override
    public Message upload(File file, LfasrType type) throws LfasrException {
        return this.upload(file, type, null);
    }

    @Override
    public Message upload(File file, LfasrType type, Map<String, String> params) throws LfasrException {
        Preconditions.checkArgument(file != null, "file should not be null");
        Preconditions.checkArgument(file.exists(), "file [%s] not exists", file.getAbsoluteFile());
        Preconditions.checkArgument(file.isFile(), "file [%s] is not a legal file", file.getAbsoluteFile());
        Preconditions.checkArgument(file.length() > 0, "file [%s] size is 0", file.getAbsoluteFile());
        Preconditions.checkArgument(type != null, "type should not be null");

        Signature signature = null;
        try {
            signature = genSignature();
        } catch (Exception var8) {
            String err = "generate signature failed";
            log.warn(err);
            throw new LfasrException(err);
        }

        UploadWorker uw = new UploadWorker(signature, file, type, xfyunAsrProperties.getFilePieceSize(), Maps.newHashMap(params));
        return uw.upload();
    }

    @Override
    public void resume() throws LfasrException {
        ResumeWorker rw = new ResumeWorker();
        rw.upload();
    }

    @Override
    public Message getVersion() throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(genSignature());
            params.setClientVersion(VersionUtil.GetVersion());
            String result = (new HttpWorker()).getVersion(params);
            Message message = null;

            return convertToMsg(result);
        } catch (Exception ex) {
            String err = String.format("get version failed, [%s]", ex.getMessage());
            log.warn(err);
            throw new LfasrException(err);
        }
    }

    @Override
    public Message getProgress(String taskId) throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(genSignature());
            params.setTaskId(taskId);
            String result = (new HttpWorker()).getProgress(params);

            return convertToMsg(result);
        } catch (Exception ex) {
            String err = String.format("get progress failed, taskId=[%s], [%s]", taskId, ex.getMessage());
            log.warn(err);
            throw new LfasrException(err);
        }
    }

    @Override
    public Message getResult(String taskId) throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(genSignature());
            params.setTaskId(taskId);
            String result = (new HttpWorker()).getResult(params);

            return convertToMsg(result);
        } catch (Exception ex) {
            String err = String.format("get result failed, taskId=[%s], [%s]", taskId, ex.getMessage());
            log.warn(err);
            throw new LfasrException(err);
        }
    }

    private Signature genSignature() throws SignatureException {
        return new Signature(xfyunAsrProperties.getAppId(), xfyunAsrProperties.getSecretKey());
    }

    private Message convertToMsg(String result) throws LfasrException {
        try {
            return JSON.parseObject(result, Message.class);
        } catch (Exception ex) {
            String err = String.format("deserialize result json failed, [%s], [%s]", result, ex.getMessage());
            log.warn(err);
            throw new LfasrException(err);
        }
    }

}
