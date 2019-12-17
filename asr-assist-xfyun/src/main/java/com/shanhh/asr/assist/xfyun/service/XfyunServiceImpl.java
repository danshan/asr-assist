package com.shanhh.asr.assist.xfyun.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.shanhh.asr.assist.xfyun.dto.AsrResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Resource
    private LfasrClient lfasrClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<String> uploadFile(File file) {

        Map<String, String> params = ImmutableMap.<String, String>builder().build();
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

    @Override
    public String resultToMarkdown(File file, String resultJson, int timeInterval) {
        Preconditions.checkArgument(timeInterval > 0, "time interval should be positive number");
        AsrResult[] results = new AsrResult[0];
        try {
            results = objectMapper.readValue(resultJson, AsrResult[].class);
        } catch (IOException e) {
            log.error("convert result json failed, {}", e.getMessage());
        }

        StringBuilder sb = new StringBuilder();
        // append title
        sb.append("# ").append(file.getAbsoluteFile()).append("\n\n");
        sb.append("> create time: ").append(new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss").format(new Date())).append("\n");
        sb.append("> total: ").append(convertTimestamp(NumberUtils.toLong(results[results.length - 1].getEnd()))).append("\n\n");
        sb.append("---");


        long nextInterval = 0L;
        for (AsrResult result : results) {
            long timestamp = NumberUtils.toLong(result.getBegin());
            if (needInterval(timestamp, timeInterval, nextInterval)) {
                sb.append("\n\n").append("## ").append(convertTimestamp(timestamp)).append("\n\n");
                nextInterval++;
            }
            sb.append(result.getContent());
        }
        return sb.toString();
    }

    private boolean needInterval(long timestamp, int timeInterval, long nextInterval) {
        return timestamp >= (timeInterval * nextInterval);
    }

    private static final int MS_IN_SEC = 1000;
    private static final int MS_IN_MIN = 60 * 1000;
    private static final int MS_IN_HOUR = 60 * 60 * 1000;

    private String convertTimestamp(long timestamp) {

        long hr = 0;
        long min = 0;
        long sec = 0;
        long ms = 0;
        if (timestamp <= 0) {
            return "00:00:00.000";
        } else {
            hr = timestamp / MS_IN_HOUR;
            min = (timestamp - (hr * MS_IN_HOUR)) / MS_IN_MIN;
            sec = (timestamp - (hr * MS_IN_HOUR) - (min * MS_IN_MIN)) / MS_IN_SEC;
            ms = (timestamp - (hr * MS_IN_HOUR) - (min * MS_IN_MIN) - (sec * MS_IN_SEC));

            DecimalFormat fmt = new DecimalFormat("00");
            return String.format("%s:%s:%s:%s", fmt.format(hr), fmt.format(min), fmt.format(sec), fmt.format(ms));
        }
    }

}

