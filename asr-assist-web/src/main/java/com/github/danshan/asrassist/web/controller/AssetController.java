package com.github.danshan.asrassist.web.controller;

import com.github.danshan.asrassist.web.controller.dto.UploadResp;
import com.github.danshan.asrassist.web.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "api/assets")
@Slf4j
public class AssetController {

    @Resource
    private AssetService assetService;

    @PostMapping
    public BaseResponse<UploadResp> uploadFile(HttpServletRequest request, HttpServletResponse response) {
        return assetService.uploadFile(request).map(BaseResponse::new).orElseGet(BaseResponse::new);
    }

}
