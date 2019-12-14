package com.shanhh.asr.assist.xfyun.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Service
@Slf4j
public class XfyunServiceImpl implements XfyunService {

    private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;

    private static final String PARAM_HAS_PARTICIPLE = "has_participle";

    @Resource
    private LfasrClient lfasrClient;

    @Override
    public Optional<String> uploadFile(File file) {

        Map<String, String> params = ImmutableMap.<String, String>builder()
                .put(PARAM_HAS_PARTICIPLE, "true")
                .build();
        try {
            // 上传音频文件
            Message uploadMsg = lfasrClient.upload(file, type, params);

            // 判断返回值
            if (uploadMsg.getOk() == 0) {
                String taskId = uploadMsg.getData();
                log.info("start uploading success, taskId=[{}]", taskId);
                return Optional.of(taskId);
            } else {
                log.warn("start uploading failed, ecode=[{}], failed=[{}]", uploadMsg.getErr_no(), uploadMsg.getFailed());
                return Optional.empty();
            }
        } catch (LfasrException e) {
            Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
            log.warn("start uploading failed, ecode=[{}], failed=[{}]", uploadMsg.getErr_no(), uploadMsg.getFailed());
            return Optional.empty();
        }
    }

    @Override
    public Optional<ProgressStatus> getProgressStatus(String taskId) {
        // 循环等待音频处理结果
        try {
            // 获取处理进度
            Message progressMsg = lfasrClient.getProgress(taskId);

            // 如果返回状态不等于0，则任务失败
            if (progressMsg.getOk() != 0) {
                log.warn("task failed, taskId=[{}], ecode=[{}], failed=[{}]", taskId, progressMsg.getErr_no(), progressMsg.getFailed());
                return Optional.empty();
            } else {
                ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
                return Optional.of(progressStatus);
            }
        } catch (LfasrException e) {
            Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
            log.warn("get progress stauts failed, taskId=[{}], ecode=[{}], failed=[{}]", taskId, progressMsg.getErr_no(), progressMsg.getFailed());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getResult(String taskId) {
        try {
            Message resultMsg = lfasrClient.getResult(taskId);
            // 如果返回状态等于0，则获取任务结果成功
            if (resultMsg.getOk() == 0) {
                return Optional.of(resultMsg.getData());
            } else {
                log.warn("get result failed, taskId=[{}], ecode=[{}], failed=[{}]", taskId, resultMsg.getErr_no(), resultMsg.getFailed());
                return Optional.empty();
            }
        } catch (LfasrException e) {
            Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
            log.warn("get result failed, taskId=[{}], ecode=[{}], failed=[{}]", taskId, progressMsg.getErr_no(), progressMsg.getFailed());
            return Optional.empty();
        }
    }

}

