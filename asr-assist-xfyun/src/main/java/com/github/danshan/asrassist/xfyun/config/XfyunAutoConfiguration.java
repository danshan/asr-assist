package com.github.danshan.asrassist.xfyun.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(XfyunAsrProperties.class)
@Import(XfyunAsrConfig.class)
public class XfyunAutoConfiguration {
}
