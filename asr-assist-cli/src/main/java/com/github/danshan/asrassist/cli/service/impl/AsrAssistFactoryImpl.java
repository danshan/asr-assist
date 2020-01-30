package com.github.danshan.asrassist.cli.service.impl;

import com.github.danshan.asrassist.cli.service.AsrAssist;
import com.github.danshan.asrassist.cli.service.AsrAssistFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Service
@Slf4j
public class AsrAssistFactoryImpl implements AsrAssistFactory {

    /**
     * https://www.xfyun.cn
     */
    private static final String XFYUN = "xfyun";

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Optional<AsrAssist> getAssist(String adapter) {
        switch (adapter) {
            case XFYUN:
                String beanName = getBeanName(adapter);
                if (applicationContext.containsBean(beanName)) {
                    return Optional.of((AsrAssist) applicationContext.getBean(beanName));
                } else {
                    return Optional.empty();
                }
            default:
                return Optional.empty();
        }
    }

    private String getBeanName(String adapter) {
        return adapter + "AsrAssist";
    }

}
