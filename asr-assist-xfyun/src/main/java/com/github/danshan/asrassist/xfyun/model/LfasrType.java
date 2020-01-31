package com.github.danshan.asrassist.xfyun.model;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Set;

@Slf4j
public enum LfasrType {
    LFASR_STANDARD_RECORDED_AUDIO("标准版-已录制音频", "wav,flac,opus,mp3,m4a", 0),
    LFASR_TELEPHONY_RECORDED_AUDIO("电话专用版-已录制音频", "wav,flac,mp3", 2);

    public final String name;
    public final int value;
    final Set<String> supportedAudios;

    LfasrType(String name, String supportedAudios, int value) {
        this.name = name;
        this.value = value;
        this.supportedAudios = Sets.newHashSet(supportedAudios.split(","));
    }

    public boolean isSupportedAudios(File file) {
        String fileName = file.getName();

        try {
            int index = fileName.lastIndexOf('.');
            String ext = fileName.substring(index + 1);
            return this.supportedAudios.contains(ext.toLowerCase());
        } catch (Exception e) {
            log.warn("check audio type failed, {}", e.getMessage());
            return false;
        }
    }
}
