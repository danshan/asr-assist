package com.github.danshan.asrassist.web.controller.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Data
public class UploadResp implements Serializable{
    /**
     * which will be the error message for the entire batch upload and will help the plugin to identify error in the file upload.
     * For example the response from server would be sent as {error: 'You are not allowed to upload such a file.'}.
     * Note: The plugin will automatically validate and display ajax exception errors
     */
    private String error;
    private List<Serializable> initialPreview = Lists.newLinkedList();
    private List<Serializable> initialPreviewThumbTags = Lists.newLinkedList();
    private Boolean append = false;

}
