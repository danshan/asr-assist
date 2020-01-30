package com.github.danshan.asrassist.xfyun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "asr-assist.xfyun", ignoreUnknownFields = true)
public class XfyunAsrProperties {

    private boolean enabled = false;

    /**
     * app id
     */
    private String appId;
    /**
     * secret key
     */
    private String secretKey;
    /**
     * lfasr host
     */
    private String host = "http://raasr.xfyun.cn/api";
    /**
     * file piece size (KB)
     */
    private int filePieceSize = 10 * 1024 * 1024;
    /**
     * this is not the store path for the result json file, but the path for the file piece during upload
     */
    private String storePath = "/tmp";


}
