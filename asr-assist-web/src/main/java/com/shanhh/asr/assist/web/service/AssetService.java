package com.shanhh.asr.assist.web.service;

import com.shanhh.asr.assist.web.service.impl.UploadFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface AssetService {
    Optional<UploadFile> saveFile(HttpServletRequest request);
}
