package com.github.danshan.asrassist.xfyun.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringExclude;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;

@Getter
@RequiredArgsConstructor
public class Signature {
    private final String appId; // TODO app_id
    @ToStringExclude
    private final String secretKey; // TODO secret_key
    @ToStringExclude
    private final String signa;
    private final String ts;

    public Signature(String appId, String secretKey) throws SignatureException {
        this.appId = appId;
        this.secretKey = secretKey;
        this.ts = String.valueOf((new Date()).getTime() / 1000L);
        this.signa = HmacSHA1Encrypt(DigestUtils.md5Hex(appId + this.ts), this.secretKey);
    }

    private static String HmacSHA1Encrypt(String encryptText, String encryptKey) throws SignatureException {
        byte[] rawHmac;
        try {
            byte[] data = encryptKey.getBytes(StandardCharsets.UTF_8);
            String hmacSHA1 = "HmacSHA1";
            SecretKeySpec secretKey = new SecretKeySpec(data, hmacSHA1);
            Mac mac = Mac.getInstance(hmacSHA1);
            mac.init(secretKey);
            byte[] text = encryptText.getBytes(StandardCharsets.UTF_8);
            rawHmac = mac.doFinal(text);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new SignatureException(e.getMessage());
        }

        return new String(Base64.getEncoder().encode(rawHmac));
    }

}
