package com.shanhh.asr.assist.web.service.impl;

import com.shanhh.asr.assist.web.config.AsrProperties;
import com.shanhh.asr.assist.web.service.AssetService;
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
    public Optional<UploadFile> saveFile(HttpServletRequest request) {
        log.debug("fetching file");

        // 转换为文件类型的request
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        // 获取对应file对象
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Iterator<String> fileIterator = multipartRequest.getFileNames();

        while (fileIterator.hasNext()) {
            String filename = fileIterator.next();
            log.debug("filename: {}", filename);

            // 获取对应文件
            MultipartFile multipartFile = fileMap.get(filename);

            if (multipartFile.getSize() != 0L) {
                validateImage(multipartFile);

                return this.saveFile(multipartFile);
            }
        }

        return Optional.empty();
    }

    private Optional<UploadFile> saveFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        log.debug("original filename: {}" + originalFilename);

        String contentType = file.getContentType();
        String type = contentType.substring(contentType.indexOf('/') + 1);
        String tempName = UUID.randomUUID().toString().replaceAll("-", "") + "." + type;

        UploadFile uploadFile = new UploadFile(originalFilename);
        uploadFile.setContentType(contentType);
        uploadFile.setTempName(tempName);
        log.debug("uploaded filed: {}", uploadFile);

        try {
            FileUtils.writeByteArrayToFile(new File(getAbsolutePath(uploadFile)), file.getBytes());
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

    private void validateImage(MultipartFile image) {
    }
}
