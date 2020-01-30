package com.github.danshan.asrassist.web.service;

import com.github.danshan.asrassist.web.controller.dto.UploadResp;
import com.github.danshan.asrassist.web.service.impl.UploadFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public interface AssetService {
    Optional<UploadResp> uploadFile(HttpServletRequest request);
}
