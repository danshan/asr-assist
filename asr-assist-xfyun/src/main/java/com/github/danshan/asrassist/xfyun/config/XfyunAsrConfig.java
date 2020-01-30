package com.github.danshan.asrassist.xfyun.config;

import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.file.LocalPersistenceFile;
import com.github.danshan.asrassist.xfyun.service.XfyunAsrClient;
import com.github.danshan.asrassist.xfyun.service.XfyunAsrClientImpl;
import com.github.danshan.asrassist.xfyun.service.XfyunService;
import com.github.danshan.asrassist.xfyun.service.XfyunServiceImpl;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author shanhonghao
 * @since 2.0.0
 */
@Configuration
@ConditionalOnProperty(value = "asr-assist.xfyun.enabled", matchIfMissing = false)
public class XfyunAsrConfig {

    private static final int MAX_FILE_PIECE_SIZE = 30 * 1024 * 1024; // 30MB
    private static final int MIN_FILE_PIECE_SIZE = 10 * 1024 * 1024; // 10MB

    @Resource
    private XfyunAsrProperties xfyunAsrProperties;

    @PostConstruct
    public void init() {
        validateProps();
    }

    private void validateProps() throws IllegalArgumentException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(xfyunAsrProperties.getAppId()), "app-id should not be empty");
        Preconditions.checkArgument(StringUtils.isNotEmpty(xfyunAsrProperties.getSecretKey()), "secret-key should not be empty");
        Preconditions.checkArgument(xfyunAsrProperties.getFilePieceSize() >= MIN_FILE_PIECE_SIZE && xfyunAsrProperties.getFilePieceSize() <= MAX_FILE_PIECE_SIZE,
                "file-piece-size should between [%s, %s]", MIN_FILE_PIECE_SIZE, MAX_FILE_PIECE_SIZE);

        // validate host
        Preconditions.checkArgument(StringUtils.isNotEmpty(xfyunAsrProperties.getHost()), "lfasr-host should not be empty");
        validateStorePath(xfyunAsrProperties.getStorePath());
    }

    public void validateStorePath(String storePath) {
        // validate store path
        Preconditions.checkArgument(StringUtils.isNotEmpty(storePath), "store-path should not be empty");
        String testFile = storePath.endsWith("/") ? (storePath + "test.dat") : (storePath + "/test.dat");
        try {
            Files.createParentDirs(new File(testFile));
            LocalPersistenceFile.writeNIO(testFile, "test");
            LocalPersistenceFile.deleteFile(new File(testFile));
        } catch (LfasrException | IOException ex) {
            throw new IllegalArgumentException(String.format("store-path [%s] permission denied", storePath));
        }
        LfasrClientImp.SERV_STORE_PATH_VAL = storePath;
    }

    @Bean
    public XfyunAsrClient lfasrClient() {
        return new XfyunAsrClientImpl();
    }

    @Bean
    public XfyunService xfyunService() {
        return new XfyunServiceImpl();
    }

}
