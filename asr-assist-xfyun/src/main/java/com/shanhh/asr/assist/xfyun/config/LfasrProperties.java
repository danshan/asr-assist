package com.shanhh.asr.assist.xfyun.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
@Configuration
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
    /**
     * this is not the store path for the result json file, but the path for the file piece during upload
     */
    private String storePath;

}
