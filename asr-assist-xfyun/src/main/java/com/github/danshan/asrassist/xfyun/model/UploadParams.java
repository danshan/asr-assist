package com.github.danshan.asrassist.xfyun.model;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.File;
import java.util.Map;

@Data
public class UploadParams {
    private Signature signature;
    private LfasrType lfasrType;
    private String taskId;
    private File file;
    private int sliceNum;
    private String clientVersion;
    private double checkLength;
    private Map<String, String> params;

    public UploadParams() {
        this.checkLength = -1.0D;
        this.params = null;
    }

    public UploadParams(Signature signature, File file, LfasrType lfasrType, Map<String, String> params, String taskId) {
        this(signature, file, lfasrType, params);
        this.taskId = taskId;
    }

    public UploadParams(Signature signature, File file, LfasrType lfasrType, Map<String, String> params) {
        this.checkLength = -1.0D;
        this.params = null;
        this.signature = signature;
        this.file = file;
        this.lfasrType = lfasrType;
        this.params = params;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

