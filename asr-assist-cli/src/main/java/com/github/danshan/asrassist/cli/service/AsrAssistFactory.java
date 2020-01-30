package com.github.danshan.asrassist.cli.service;

import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface AsrAssistFactory {

    Optional<AsrAssist> getAssist(String adapter);

}
