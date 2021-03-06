package com.github.danshan.asrassist.cli;

import com.github.danshan.asrassist.cli.service.AsrAssist;
import com.github.danshan.asrassist.cli.service.AsrAssistFactory;
import com.github.danshan.asrassist.xfyun.config.XfyunAsrConfig;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Component
@Command(name = "asr-assist-cli", mixinStandardHelpOptions = true)
@Slf4j
public class AsrCommand implements Callable<Integer> {

    @Resource
    private AsrAssistFactory asrAssistFactory;
    @Resource
    private XfyunAsrConfig xfyunAsrConfig;

    @Option(names = {"-f", "--file"}, required = true, description = "audio file")
    private String file;

    @Option(names = {"-a", "--adapter"}, required = false, defaultValue = "xfyun", description = "asr adapter. default 'xfyun'")
    private String adapter;

    @Option(names = {"-i", "--interval"}, required = false, defaultValue = "120", description = "time interval, seconds. default '120'")
    private int interval;

    @Override
    public Integer call() {
        AsrAssist assist = asrAssistFactory.getAssist(this.adapter).orElseThrow(() -> new IllegalArgumentException("unknown adapter: " + adapter));
        return assist.voiceToText(new File(file), interval * 1000).map(text -> {
            String target = file.substring(0, file.lastIndexOf('.')) + ".md";
            try {
                Files.asCharSink(new File(target), Charset.defaultCharset()).write(text);
                log.info("write text success, [{}]", target);
            } catch (IOException e) {
                log.error("write file failed: [{}], {}", target, e.getMessage());
            }
            return 0;
        }).orElse(-1);
    }

}
