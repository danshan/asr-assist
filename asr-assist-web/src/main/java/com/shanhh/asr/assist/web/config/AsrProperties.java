package com.shanhh.asr.assist.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "asr-aasist")
@Data
public class AsrProperties {

    private final Upload upload = new Upload();

    @Data
    public static final class Upload {
        private String storePath;
    }

}
