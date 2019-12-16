package com.shanhh.asr.assist.xfyun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "asr-assist.lfasr", ignoreUnknownFields = true)
public class LfasrProperties {

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
    private String lfasrHost;
    /**
     * file piece size
     */
    private int filePieceSize;

}
