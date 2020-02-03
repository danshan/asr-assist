package com.github.danshan.asrassist.xfyun.config;

import com.github.danshan.asrassist.xfyun.model.LfasrType;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public class XfyunConstants {

    private XfyunConstants() {
    }

    public static final long FILE_UPLOAD_MAXSIZE = 500 * 1024 * 1024L;

    public static final int BLOCKINGQUEUE_MAXSIZE = 1024;

    public static final LfasrType LFASR_TYPE = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;

}
