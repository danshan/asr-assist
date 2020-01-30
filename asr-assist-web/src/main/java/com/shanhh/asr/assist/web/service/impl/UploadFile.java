package com.shanhh.asr.assist.web.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@RequiredArgsConstructor
@Data
public class UploadFile {

    private final String originName;
    private String tempName;
    private String contentType;

    @JsonIgnore
    private String getHashPath() {
        Preconditions.checkArgument(StringUtils.isNotEmpty(tempName), "tempName should not be empty");
        return this.tempName.substring(0, 2) + "/" + this.tempName.substring(2, 4);
    }

    @JsonIgnore
    public String getRelativePath() {
        return getHashPath() + "/" + tempName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}

