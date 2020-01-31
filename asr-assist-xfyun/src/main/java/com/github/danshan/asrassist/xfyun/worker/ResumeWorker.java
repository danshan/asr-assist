package com.github.danshan.asrassist.xfyun.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.event.Event;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.file.ChannelFileReader;
import com.github.danshan.asrassist.xfyun.file.LocalPersistenceFile;
import com.github.danshan.asrassist.xfyun.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ResumeWorker {

    private final XfyunAsrProperties xfyunAsrProperties;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void upload() throws LfasrException {
        List<File> fileList;
        try {
            fileList = LocalPersistenceFile.getFileList(xfyunAsrProperties.getStorePath() + "/");
        } catch (LfasrException ex) {
            throw ex;
        }
        try {
            for (int i = 0; i < fileList.size(); ++i) {
                String fileName = (fileList.get(i)).toString();

                try {
                    String app_id = "";
                    String secret_key = "";
                    String signa = "";
                    String ts = "";
                    LfasrType lfasr_type = null;
                    int file_piece_size = 0;
                    String task_id = "";
                    File file = null;
                    Map<String, String> params = null;
                    Map<String, String> hm = new HashMap();
                    FileReader reader = new FileReader(fileName);
                    BufferedReader br = new BufferedReader(reader);
                    String str = null;

                    for (int line = 1; (str = br.readLine()) != null; ++line) {
                        if (line == 1) {
                            LocalPersistenceMeta lpm = objectMapper.readValue(str, LocalPersistenceMeta.class);
                            app_id = lpm.getAppId();
                            secret_key = lpm.getSecretKey();
                            signa = lpm.getSigna();
                            ts = lpm.getTs();
                            if (lpm.getLfasrType() == 0) {
                                lfasr_type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
                            } else {
                                lfasr_type = LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO;
                            }

                            file_piece_size = lpm.getFilePieceSize();
                            task_id = lpm.getTaskId();
                            file = new File(lpm.getFile());
                            params = lpm.getParams();
                        } else {
                            hm.put(str, "");
                        }
                    }

                    br.close();
                    reader.close();
                    UploadParams upParams = new UploadParams(new Signature(app_id, secret_key, signa, ts), file, lfasr_type, params, task_id);
                    if (!upParams.getLfasrType().isSupportedAudios(upParams.getFile())) {
                        log.warn("taskId=[{}], resume file=[{}], type is wrong, maybe file was changed, you can delete it and upload a new one", upParams.getTaskId(), file.getName());
                        throw new LfasrException(Message.failed(ErrorCode.ASR_BREAKPOINT_RESUME_ERR, null));
                    }

                    try {
                        ChannelFileReader fr = new ChannelFileReader(upParams.getFile(), file_piece_size);
                        long fileLen = fr.getFileLength();
                        if (fileLen >= 524288000L) {
                            log.warn("taskId=[{}], resume file=[{}], size is too larger, maybe file was changed, you can delete it and upload a new one", upParams.getTaskId(), file.getName());
                            throw new LfasrException(Message.failed(ErrorCode.ASR_BREAKPOINT_RESUME_ERR, null));
                        }

                        SliceWorker sw = new SliceWorker(xfyunAsrProperties, upParams, hm);
                        sw.sliceFile(fr);
                        fr.close();

                        for (boolean isSend = sw.getEventHandler().isSendAll(); !isSend; isSend = sw.getEventHandler().isSendAll()) {
                            try {
                                Thread.sleep(1000L);
                            } catch (Exception var25) {
                            }
                        }

                        Event event = new Event(EventType.LFASR_FILE_DATA_END, upParams);
                        sw.getEventHandler().addEvent(event);
                        sw.getEventHandler().await();
                        sw.getEventHandler().shutdownNow();
                    } catch (Exception ex) {
                        // do nothing
                    }
                } catch (Exception ex) {
                    log.warn("resume file=[{}], read error, maybe file was changed, you can delete it and upload a new one", fileName);
                    throw new LfasrException(Message.failed(ErrorCode.ASR_BREAKPOINT_RESUME_ERR, null));
                }
            }

        } catch (Exception ex) {
            throw new LfasrException(Message.failed(ErrorCode.ASR_BREAKPOINT_RESUME_ERR, null));
        }
    }
}
