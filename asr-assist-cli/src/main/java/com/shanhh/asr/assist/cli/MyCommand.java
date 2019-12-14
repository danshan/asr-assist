package com.shanhh.asr.assist.cli;

import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.shanhh.asr.assist.xfyun.service.XfyunService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Component
@Command(name = "asr-assist-cli", mixinStandardHelpOptions = true)
@Slf4j
public class MyCommand implements Callable<Integer> {

    @Resource
    private XfyunService xfyunService;

    @Option(names = {"-f", "--file"}, required = true, description = "audio file")
    private String file;

    @Parameters(description = "files")
    private List<String> positionals;

    @Override
    public Integer call() {
        String taskId = xfyunService.uploadFile(new File(this.file)).orElseThrow(() -> new IllegalStateException("upload failed"));

        while (true) {
            try {
                Thread.sleep(2 * 1000);
                ProgressStatus progress = xfyunService.getProgressStatus(taskId).orElseThrow(() -> new IllegalStateException("get progress failed"));
                log.info("{}, {}", progress.getStatus(), progress.getDesc());
                if (progress.getStatus() == 9) {
                    break;
                }
            } catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }

        xfyunService.getResult(taskId).ifPresent(result -> log.info(result));
        return 0;
    }

}