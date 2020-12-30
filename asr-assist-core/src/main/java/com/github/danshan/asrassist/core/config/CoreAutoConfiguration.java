package com.github.danshan.asrassist.core.config;

import com.github.danshan.asrassist.xfyun.config.XfyunAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.github.danshan.asrassist.core")
@AutoConfigureAfter({XfyunAutoConfiguration.class})
public class CoreAutoConfiguration {

}
