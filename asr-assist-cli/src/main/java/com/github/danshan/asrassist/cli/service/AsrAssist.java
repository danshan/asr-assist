package com.github.danshan.asrassist.cli.service;

import java.io.File;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface AsrAssist {

    Optional<String> voiceToText(File file, int interval);

}
