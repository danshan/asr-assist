package com.github.danshan.asrassist.xfyun.worker;

import com.alibaba.fastjson.JSON;
import com.github.danshan.asrassist.xfyun.event.Event;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.file.LocalPersistenceFile;
import com.github.danshan.asrassist.xfyun.model.LfasrType;
import com.github.danshan.asrassist.xfyun.model.LocalPersistenceMeta;
import com.github.danshan.asrassist.xfyun.model.Signature;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.file.ChannelFileReader;
import com.iflytek.msp.cpdb.lfasr.model.EventType;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumeWorker {
    private static final Logger LOGGER = Logger.getLogger(ResumeWorker.class);

    public ResumeWorker() {
    }

    public void upload() throws LfasrException {
        new ArrayList();

        List fileList;
        try {
            fileList = LocalPersistenceFile.getFileList(LfasrClientImp.SERV_STORE_PATH_VAL + "/");
        } catch (LfasrException var26) {
            throw var26;
        }

        try {
            for(int i = 0; i < fileList.size(); ++i) {
                String fileName = ((File)fileList.get(i)).toString();

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

                    for(int line = 1; (str = br.readLine()) != null; ++line) {
                        if (line == 1) {
                            LocalPersistenceMeta lpm = (LocalPersistenceMeta)JSON.parseObject(str, LocalPersistenceMeta.class);
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
                        LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "ResumeWorker", upParams.getTaskId(), "", "(-1) ms", "resume file:" + file.getName() + " type is wrong, maybe file was changed, you can delete it and upload a new one"));
                        throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26303\", \"failed\":\"转写恢复断点续传流程错误,请见日志！\", \"data\":\"\"}");
                    }

                    try {
                        ChannelFileReader fr = new ChannelFileReader(upParams.getFile(), file_piece_size);
                        long fileLen = fr.getFileLength();
                        if (fileLen >= 524288000L) {
                            LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "ResumeWorker", upParams.getTaskId(), "", "(-1) ms", "resume file:" + file.getName() + " size is too larger, maybe file was changed, you can delete it and upload a new one "));
                            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26303\", \"failed\":\"转写恢复断点续传流程错误,请见日志！\", \"data\":\"\"}");
                        }

                        SliceWorker sw = new SliceWorker(upParams, (long)file_piece_size, true, hm);
                        sw.sliceFile(fr);
                        fr.close();

                        for(boolean isSend = sw.getEventHandler().isSendAll(); !isSend; isSend = sw.getEventHandler().isSendAll()) {
                            try {
                                Thread.sleep(1000L);
                            } catch (Exception var25) {
                            }
                        }

                        Event event = new Event(EventType.LFASR_FILE_DATA_END, upParams);
                        sw.getEventHandler().addEvent(event);
                        sw.getEventHandler().await();
                        sw.getEventHandler().shutdownNow();
                    } catch (Exception var27) {
                    }
                } catch (Exception var28) {
                    LOGGER.warn(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "ResumeWorker", "", "", "(-1) ms", "resume file:" + fileName + " read error, maybe file was changed, you can delete it and upload a new one"));
                    throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26303\", \"failed\":\"转写恢复断点续传流程错误,请见日志！\", \"data\":\"\"}");
                }
            }

        } catch (Exception var29) {
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26303\", \"failed\":\"转写恢复断点续传流程错误,请见日志！\", \"data\":\"\"}");
        }
    }
}
