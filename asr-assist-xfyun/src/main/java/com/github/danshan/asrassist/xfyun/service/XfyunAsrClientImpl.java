package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.model.*;
import com.github.danshan.asrassist.xfyun.util.VersionUtil;
import com.github.danshan.asrassist.xfyun.worker.HttpWorker;
import com.github.danshan.asrassist.xfyun.worker.ResumeWorker;
import com.github.danshan.asrassist.xfyun.worker.UploadWorker;
import com.google.common.base.Preconditions;
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

        Signature signature;
        try {
            signature = genSignature();
        } catch (Exception ex) {
            throw new LfasrException(Message.failed(ErrorCode.ASR_SIGN_ERR, null));
        }

        UploadWorker uw = new UploadWorker(this.xfyunAsrProperties, signature, file, type, params);
        return uw.upload();
    }

    @Override
    public void resume() throws LfasrException {
        ResumeWorker rw = new ResumeWorker(this.xfyunAsrProperties);
        rw.upload();
    }

    @Override
    public Message getVersion() throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(genSignature());
            params.setClientVersion(VersionUtil.getVersion());
            return new HttpWorker(this.xfyunAsrProperties).getVersion(params);

        } catch (Exception ex) {
            throw new LfasrException(Message.failed(ErrorCode.ASR_API_VERSION_ERR, null));
        }
    }

    @Override
    public Message getProgress(String taskId) throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(genSignature());
            params.setTaskId(taskId);
            return new HttpWorker(this.xfyunAsrProperties).getProgress(params);
        } catch (Exception ex) {
            Message failed = Message.failed(ErrorCode.ASR_API_PROGRESS_ERR, null);
            log.warn("[{}], taskId=[{}], [{}]", failed, taskId, ex.getMessage());
            throw new LfasrException(failed);
        }
    }

    @Override
    public Message getResult(String taskId) throws LfasrException {
        try {
            UploadParams params = new UploadParams();
            params.setSignature(genSignature());
            params.setTaskId(taskId);
            return new HttpWorker(this.xfyunAsrProperties).getResult(params);
        } catch (Exception ex) {
            Message failed = Message.failed(ErrorCode.ASR_API_RESULT_ERR, null);
            log.warn("[{}], taskId=[{}], [{}]", failed, taskId, ex.getMessage());
            throw new LfasrException(failed);
        }
    }

    private Signature genSignature() throws SignatureException {
        return new Signature(xfyunAsrProperties.getAppId(), xfyunAsrProperties.getSecretKey());
    }

}
