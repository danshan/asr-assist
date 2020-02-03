package com.github.danshan.asrassist.xfyun.model;

import lombok.Data;

import java.util.Map;

@Data
public class LocalPersistenceMeta {
    private String appId;
    private String secretKey;
    private String signa;
    private String ts;
    private int lfasrType;
    private int filePieceSize;
    private String taskId;
    private String file;
    private Map<String, String> params;
}
