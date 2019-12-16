package com.shanhh.asr.assist.xfyun.config;

import com.google.common.base.Preconditions;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.file.LocalPersistenceFile;
import com.shanhh.asr.assist.xfyun.service.LfasrClient;
import com.shanhh.asr.assist.xfyun.service.LfasrClientImpl;
import com.shanhh.asr.assist.xfyun.service.XfyunService;
import com.shanhh.asr.assist.xfyun.service.XfyunServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
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
        Preconditions.checkArgument(lfasrProperties.getFilePieceSize() >= MIN_FILE_PIECE_SIZE && lfasrProperties.getFilePieceSize() <= MAX_FILE_PIECE_SIZE,
                "file-piece-size should between [%s, %s]", MIN_FILE_PIECE_SIZE, MAX_FILE_PIECE_SIZE);

        // validate host
        Preconditions.checkArgument(StringUtils.isNotEmpty(lfasrProperties.getLfasrHost()), "lfasr-host should not be empty");
        LfasrClientImp.SERV_LFASR_HOST_VAL = lfasrProperties.getLfasrHost();
    }

    public void validateStorePath(String storePath) {
        // validate store path
        Preconditions.checkArgument(StringUtils.isNotEmpty(storePath), "store-path should not be empty");
        String testFile = storePath.endsWith("/") ? (storePath + "test.dat") : (storePath + "/test.dat");
        try {
            LocalPersistenceFile.writeNIO(testFile, "test");
            LocalPersistenceFile.deleteFile(new File(testFile));
        } catch (LfasrException ex) {
            throw new IllegalArgumentException(String.format("store-path [%s] permission denied", storePath));
        }
        LfasrClientImp.SERV_STORE_PATH_VAL = storePath;
    }

    @Bean
    public LfasrClient lfasrClient() {
        return new LfasrClientImpl();
    }

    @Bean
    public XfyunService xfyunService() {
        return new XfyunServiceImpl();
    }

}
