package com.github.danshan.asrassist.xfyun.worker;

import com.alibaba.fastjson.JSON;
import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.file.LocalPersistenceFile;
import com.github.danshan.asrassist.xfyun.model.*;
import com.github.danshan.asrassist.xfyun.util.VersionUtil;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.file.AudioLength;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class UploadWorker {
    public static final long FILE_UPLOAD_MAXSIZE = 500 * 1024 * 1024L;

    private final XfyunAsrProperties xfyunAsrProperties;
    private UploadParams upParams;
    private static final Logger LOGGER = Logger.getLogger(UploadWorker.class);

    public UploadWorker(XfyunAsrProperties xfyunAsrProperties, Signature signature, File file, LfasrType lfasrType, Map<String, String> params) {
        this.xfyunAsrProperties = xfyunAsrProperties;

        long fileLen = file.length();
        int filePieceSize = xfyunAsrProperties.getFilePieceSize();
        int flag = (int) fileLen % filePieceSize;
        int sliceNum = flag == 0 ? (int) fileLen / filePieceSize : (int) fileLen / filePieceSize + 1;
        log.debug("[{}] slice [{}] files", file.getAbsoluteFile(), sliceNum);

        this.upParams = new UploadParams(signature, file, lfasrType, params);
        this.upParams.setSliceNum(sliceNum);
        this.upParams.setClientVersion(VersionUtil.getVersion());
    }

    public Message upload() throws LfasrException {
        if (!this.upParams.getLfasrType().isSupportedAudios(this.upParams.getFile())) {
            Message failed = Message.failed(ErrorCode.ASR_UPLOADFILE_TYPE_ERR, null);
            log.warn("taskId=[{}], [{}]", this.upParams.getTaskId(), failed);
            throw new LfasrException(failed);
        } else {
            ChannelFileReader fr = null;
            Thread hbt = null;

            try {
                fr = new ChannelFileReader(this.upParams.getFile(), this.xfyunAsrProperties.getFilePieceSize());
                if (fr.getFileLength() > FILE_UPLOAD_MAXSIZE) {
                    Message failed = Message.failed(ErrorCode.ASR_UPLOADFILE_SIZE_ERR, null);
                    log.warn("taskId=[{}], [{}]", this.upParams.getTaskId(), failed);
                    throw new LfasrException(failed);
                }

                Double check_length = AudioLength.getLength(this.upParams.getFile().getAbsolutePath());
                this.upParams.setCheckLength(check_length);
                HttpWorker hw = new HttpWorker(this.xfyunAsrProperties);
                Message message = hw.prepare(this.upParams);

                if (message.getOk() != 0 || StringUtils.isEmpty(message.getData())) {
                    log.warn("taskId=[{}], [{}], [{}]", this.upParams.getTaskId(), "prepare error", message.getFailed());
                    fr.close();
                    return message;
                }

                log.info("taskId=[{}], [{}]", this.upParams.getTaskId(), "prepare ok");
                String taskId = message.getData();
                log.info("taskId=[{}], [{}]", this.upParams.getTaskId(), "get task id ok");
                this.upParams.setTaskId(taskId);
                this.metaPersistency(this.upParams);
                UploadThread ut = new UploadThread(this.xfyunAsrProperties, this.upParams, fr);
                hbt = new Thread(ut);
                hbt.start();

                try {
                    if (!this.upParams.getParams().get("not_wait").equalsIgnoreCase("true")) {
                        hbt.join();
                    }
                } catch (Exception ex) {
                    hbt.join();
                }

                return message;
            } catch (LfasrException ex) {
                log.warn("taskId=[{}], [{}], [{}]", this.upParams.getTaskId(), "upload error", ex.getMessage());
                throw ex;
            } catch (Exception ex) {
                Message failed = Message.failed(ErrorCode.ASR_API_UPLOAD_ERR, null);
                log.warn("taskId=[{}], [{}], [{}]", this.upParams.getTaskId(), failed, ex.getMessage());
                throw new LfasrException(failed);
            } finally {
                if (fr != null && (hbt == null || !hbt.isAlive())) {
                    try {
                        fr.close();
                    } catch (IOException ex) {
                        log.warn("taskId=[{}], [{}], [{}]", this.upParams.getTaskId(), "close io error", ex.getMessage());
                    }
                }

            }
        }
    }

    private void metaPersistency(UploadParams upParams) throws LfasrException {
        try {
            LocalPersistenceMeta lpm = new LocalPersistenceMeta();
            lpm.setAppId(upParams.getSignature().getAppId());
            lpm.setSecretKey(upParams.getSignature().getSecretKey());
            lpm.setSigna(upParams.getSignature().getSigna());
            lpm.setTs(upParams.getSignature().getTs());
            lpm.setLfasrType(upParams.getLfasrType().getValue());
            lpm.setFilePieceSize(this.xfyunAsrProperties.getFilePieceSize());
            lpm.setTaskId(upParams.getTaskId());
            lpm.setFile(upParams.getFile().getAbsolutePath());
            lpm.setParams(upParams.getParams());
            String jsonStr = JSON.toJSONString(lpm);
            String fileName = LfasrClientImp.SERV_STORE_PATH_VAL + "/" + upParams.getTaskId() + ".dat";
            LocalPersistenceFile.writeNIO(fileName, jsonStr);
            log.info("taskId=[{}], [{}], file=[{}]", upParams.getTaskId(), "write meta info success", fileName);
        } catch (LfasrException ex) {
            Message failed = Message.failed(ErrorCode.ASR_BREAKPOINT_PERSISTENCE_ERR, null);
            log.warn("taskId=[{}], [{}], [{}]", upParams.getTaskId(), failed, ex.getMessage());
            throw new LfasrException(failed);
        }
    }
}
