package com.github.danshan.asrassist.xfyun.service;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.ErrorMsg;
import com.github.danshan.asrassist.xfyun.model.Signature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.SignatureException;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Slf4j
@Service
public class XfyunSignatureServiceImpl implements XfyunSignatureService {

    @Resource
    private XfyunAsrProperties xfyunAsrProperties;

    @Override
    public Signature generateSignature() {
        try {
            log.debug("generate signature for [{}]", xfyunAsrProperties.getAppId());
            return new Signature(xfyunAsrProperties.getAppId(), xfyunAsrProperties.getSecretKey());
        } catch (SignatureException e) {
            throw new LfasrException(ErrorMsg.failed(ErrorCode.ASR_SIGN_ERR));
        }
    }
}
