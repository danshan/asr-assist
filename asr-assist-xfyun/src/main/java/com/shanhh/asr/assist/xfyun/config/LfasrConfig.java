package com.shanhh.asr.assist.xfyun.config;

import com.google.common.base.Preconditions;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.file.LocalPersistenceFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;

/**
 * @author shanhonghao
 * @since 2.0.0
 */
@Configuration
@ConditionalOnProperty(value = "asr-assist.lfasr.enabled", matchIfMissing = false)
public class LfasrConfig {

    private static final int MAX_FILE_PIECE_SIZE = 30 * 1024 * 1024; // 30MB
    private static final int MIN_FILE_PIECE_SIZE = 10 * 1024 * 1024; // 10MB

    @Resource
    private LfasrProperties lfasrProperties;

    @PostConstruct
    public void init() {
        validateProps();
    }

    private void validateProps() throws IllegalArgumentException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(lfasrProperties.getAppId()), "app-id should not be empty");
        Preconditions.checkArgument(StringUtils.isNotEmpty(lfasrProperties.getSecretKey()), "secret-key should not be empty");
        Preconditions.checkArgument(StringUtils.isNotEmpty(lfasrProperties.getLfasrHost()), "lfasr-host should not be empty");
        Preconditions.checkArgument(lfasrProperties.getFilePieceSize() >= MIN_FILE_PIECE_SIZE && lfasrProperties.getFilePieceSize() <= MAX_FILE_PIECE_SIZE,
                "file-piece-size should between [%s, %s]", MIN_FILE_PIECE_SIZE, MAX_FILE_PIECE_SIZE);
        Preconditions.checkArgument(StringUtils.isNotEmpty(lfasrProperties.getStorePath()), "store-path should not be empty");

        String testFile = lfasrProperties.getStorePath().endsWith("/") ? (lfasrProperties + "test.dat") : (lfasrProperties + "/test.dat");

        try {
            LocalPersistenceFile.writeNIO(testFile, "test");
            LocalPersistenceFile.deleteFile(new File(testFile));
        } catch (LfasrException ex) {
            throw new IllegalArgumentException(String.format("store-path [%s] permission denied", lfasrProperties.getStorePath()));
        }
    }

}
