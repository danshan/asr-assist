package com.github.danshan.asrassist.xfyun.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.model.Progress;
import com.github.danshan.asrassist.xfyun.model.Result;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Service
@Slf4j
public class XfyunServiceImpl implements XfyunService {

    @Resource
    private XfyunAsrService xfyunAsrService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<String> uploadFile(File file) {
        try {
            return xfyunAsrService.upload(file);
        } catch (IOException ex) {
            log.warn("file=[{}], {}", file.getName(), ex.getMessage());
        } catch (LfasrException ex) {
            log.warn("file=[{}], {}", file.getName(), ex.getErrorMsg());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Progress> getProgress(String taskId) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(taskId), "task id should not be blank.");
            return xfyunAsrService.getProgress(taskId);
        } catch (LfasrException ex) {
            log.warn("taskId=[{}], {}", taskId, ex.getErrorMsg());
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<Result>> getResult(String taskId) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(taskId), "task id should not be blank.");
            return xfyunAsrService.getResult(taskId);
        } catch (LfasrException ex) {
            log.warn("taskId=[{}], {}", taskId, ex.getErrorMsg());
        }
        return Optional.empty();
    }

    @Override
    public String resultToMarkdown(File file, List<Result> results, int timeInterval) {
        Preconditions.checkArgument(timeInterval > 0, "time interval should be positive number");

        StringBuilder sb = new StringBuilder();
        // append title
        sb.append("# ").append(file.getAbsoluteFile()).append("\n\n");
        sb.append("> create time: ").append(new SimpleDateFormat("yyyy-MM-dd: HH:mm:ss").format(new Date())).append("\n");
        sb.append("> total: ").append(convertTimestamp(NumberUtils.toLong(results.get(results.size() - 1).getEnd()))).append("\n\n");
        sb.append("---");


        long nextInterval = 0L;
        for (Result result : results) {
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

