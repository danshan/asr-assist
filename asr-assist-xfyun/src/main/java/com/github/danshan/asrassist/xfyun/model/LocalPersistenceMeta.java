package com.github.danshan.asrassist.xfyun.model;

import lombok.Data;

import java.util.Map;

@Data
public class LocalPersistenceMeta {
    private String appId; // TODO app_id
    private String secretKey; // TODO secret_key
    private String signa;
    private String ts;
    private int lfasrType; // TODO lfasr_type
    private int filePieceSize; // TODO file_piece_size
    private String taskId; //  TODO task_id
    private String file;
    private Map<String, String> params;
}
