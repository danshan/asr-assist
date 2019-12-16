package com.shanhh.asr.assist.cli;

import com.google.common.io.Files;
import com.shanhh.asr.assist.cli.service.AsrAssist;
import com.shanhh.asr.assist.cli.service.AsrAssistFactory;
import com.shanhh.asr.assist.xfyun.config.LfasrConfig;
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
    private LfasrConfig lfasrConfig;

    @Option(names = {"-f", "--file"}, required = true, description = "audio file")
    private String file;

    @Option(names = {"-a", "--adapter"}, required = false, defaultValue = "xfyun", description = "asr adapter. default 'xfyun'")
    private String adapter;

    @Option(names = {"-i", "--interval"}, required = false, defaultValue = "120", description = "time interval, seconds. default '120'")
    private int interval;

    @Option(names = {"-p", "--store-path"}, required = false, defaultValue = "/tmp", description = "store path, this is not the store path for the result json file, but the path for the file piece during upload")
    private String storePath;

    @Override
    public Integer call() {
        lfasrConfig.validateStorePath(this.storePath);
        
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