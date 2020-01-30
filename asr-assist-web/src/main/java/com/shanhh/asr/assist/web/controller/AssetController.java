package com.shanhh.asr.assist.web.controller;

import com.shanhh.asr.assist.web.service.AssetService;
import com.shanhh.asr.assist.web.service.impl.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@RequestMapping(value = "assets")
@Slf4j
public class AssetController {

    @Resource
    private AssetService assetService;

    @PostMapping("actions/upload")
    public BaseResponse<UploadFile> uploadFile(HttpServletRequest request, HttpServletResponse response) {
        return assetService.saveFile(request).map(BaseResponse::new).orElseGet(BaseResponse::new);
    }


}
