package com.github.danshan.asrassist.xfyun.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * static 方式获取 spring bean
 *
 * @author shanhonghao
 * @date 2018-05-30 10:46
 */
@Component("springContextHolder")
@Lazy(false)
public class XfyunContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext; // NOSONAR
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        ApplicationContext applicationContext = getApplicationContext();
        return applicationContext == null ? null : applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        ApplicationContext applicationContext = getApplicationContext();
        return applicationContext == null ? null : applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        ApplicationContext applicationContext = getApplicationContext();
        return applicationContext == null ? null : applicationContext.getBean(name, clazz);
    }

}
