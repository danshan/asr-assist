package com.github.danshan.asrassist.web.service.impl;

import com.github.danshan.asrassist.web.config.AsrProperties;
import com.github.danshan.asrassist.web.controller.dto.UploadResp;
import com.github.danshan.asrassist.web.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Service
@Slf4j
public class AssetServiceImpl implements AssetService {

    @Resource
    private AsrProperties asrProperties;

    @Override
    public Optional<UploadResp> uploadFile(HttpServletRequest request) {
        // 转换为文件类型的request
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        // 获取对应file对象
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Iterator<String> fileIterator = multipartRequest.getFileNames();

        UploadResp uploadResp = new UploadResp();

        while (fileIterator.hasNext()) {
            String filename = fileIterator.next();
            // 获取对应文件
            MultipartFile multipartFile = fileMap.get(filename);

            if (multipartFile.getSize() != 0L) {
                validateFile(multipartFile);

                Optional<UploadFile> uploadFile = this.saveFile(multipartFile);
                if (uploadFile.isPresent()) {
                    uploadResp.getInitialPreview().add(multipartFile.getOriginalFilename() + " waiting for conversion");
                } else {
                    uploadResp.setError(multipartFile.getOriginalFilename() + " upload failed");
                    break;
                }
            }
        }

        return Optional.of(uploadResp);
    }

    private Optional<UploadFile> saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.debug("original filename: {}", originalFilename);

        String contentType = file.getContentType();
        String type = contentType.substring(contentType.indexOf('/') + 1);
        String tempName = UUID.randomUUID().toString().replace("-", "") + "." + type;

        UploadFile uploadFile = new UploadFile(originalFilename);
        uploadFile.setContentType(contentType);
        uploadFile.setTempName(tempName);
        uploadFile.setPath(getAbsolutePath(uploadFile));
        log.info("uploaded filed: {}", uploadFile);

        try {
            FileUtils.writeByteArrayToFile(new File(uploadFile.getPath()), file.getBytes());
            return Optional.of(uploadFile);
        } catch (IOException e) {
            log.error("save file failed, file={}, {}", file, e.getMessage());
            return Optional.empty();
        }
    }

    private String getAbsolutePath(UploadFile file) {
        String storePath = this.asrProperties.getUpload().getStorePath();
        return storePath.endsWith("/") ? (storePath + file.getRelativePath()) : (storePath + "/" + file.getRelativePath());
    }

    private void validateFile(MultipartFile file) {
    }
}
