package com.github.danshan.asrassist.core.service.impl;

import com.github.danshan.asrassist.core.service.AsrAssist;
import com.github.danshan.asrassist.core.service.AsrAssistFactory;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
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
        Preconditions.checkNotNull(adapter, "adapter should not be empty");
        switch (adapter) {
            case XFYUN:
                try {
                    return Optional.ofNullable(applicationContext.getBean(XfyunAsrAssist.class));
                } catch (BeansException e){
                    return Optional.empty();
                }
            default:
                return Optional.empty();
        }
    }

}
