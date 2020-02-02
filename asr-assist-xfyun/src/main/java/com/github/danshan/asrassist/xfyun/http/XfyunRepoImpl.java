package com.github.danshan.asrassist.xfyun.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.http.dto.*;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.ErrorMsg;
import com.github.danshan.asrassist.xfyun.model.Progress;
import com.github.danshan.asrassist.xfyun.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
@Slf4j
@Repository
public class XfyunRepoImpl implements XfyunRepo {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Resource
    XfyunAsrProperties xfyunAsrProperties;

    @Override
    public Optional<String> prepare(PrepareReq req) {
        PrepareResp prepareResp = this.doPost("prepare", req, PrepareResp.class)
            .orElseThrow(() -> new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_PREPARE_ERR)));
        if (prepareResp.isOk()) {
            return Optional.of(prepareResp.getTaskId());
        } else {
            throw new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_PREPARE_ERR));
        }
    }

    @Override
    public void upload(UploadReq req, byte[] content, String desc) {
        BaseResp baseResp = this.doUpload("upload", req, content, desc, BaseResp.class)
            .orElseThrow(() -> new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_UPLOAD_ERR)));
        if (!baseResp.isOk()) {
            ErrorMsg failed = ErrorMsg.failed(ErrorCode.ASR_API_UPLOAD_ERR);
            log.warn("upload failed, [{}]", failed);
            throw new LfasrException(failed);
        }
    }

    @Override
    public void merge(MergeReq req) {
        BaseResp baseResp = this.doPost("merge", req, BaseResp.class)
            .orElseThrow(() -> new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_MERGE_ERR)));
        if (!baseResp.isOk()) {
            throw new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_MERGE_ERR));
        }
    }

    /**
     * 在调用方发出合并文件请求后，服务端已将任务列入计划。在获取结果前，调用方需轮询该接口查询任务当前状态。
     * 当且仅当任务状态=9（转写结果上传完成），才可调用获取结果接口获取转写结果。
     * 轮询策略由调用方决定，建议每隔10分钟轮询一次。状态码说明见附录。
     */
    @Override
    public Optional<Progress> getProgress(ProgressReq req) {
        ProgressResp resp = this.doPost("getProgress", req, ProgressResp.class)
            .orElseThrow(() -> new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_PROGRESS_ERR)));
        if (!resp.isOk()) {
            throw new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_PROGRESS_ERR));
        }
        return Optional.of(resp.getProgress());
    }

    /**
     * 当任务处理进度状态=9（见查询处理进度接口），调用该接口获取转写结果。这是转写流程的最后一步。
     * 转写结果各字段的详细说明见转写结果说明文档。
     * 服务端也支持主动回调，转写完成之后主动发送转写结果到用户配置的回调地址，配置回调地址请联系技术支持。
     *
     * @param req
     * @return
     */
    @Override
    public Optional<List<Result>> getResult(ResultReq req) {
        ResultResp resp = this.doPost("getResult", req, ResultResp.class)
            .orElseThrow(() -> new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_RESULT_ERR)));
        if (!resp.isOk()) {
            throw new LfasrException(ErrorMsg.failed(ErrorCode.ASR_API_RESULT_ERR));
        }
        return Optional.of(resp.getResults().getList());
    }

    public <T> Optional<T> doPost(String path, Serializable paramPojo, Class<T> respType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, Object> params = pojoToPostParams(paramPojo);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        return doExchange(path, request, respType);
    }

    public <T> Optional<T> doUpload(String path, Serializable paramPojo, byte[] content, String desc, Class<T> respType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = pojoToPostParams(paramPojo);
        FileNameAwareByteArrayResource bytes = new FileNameAwareByteArrayResource(desc, content);
        params.add("content", bytes);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, headers);

        return doExchange(path, request, respType);
    }

    private <T> Optional<T> doExchange(String path, HttpEntity<MultiValueMap<String, Object>> request, Class<T> respType) {
        RestTemplate client = new RestTemplate();
        try {
            String url = buildUrl(path);
            ResponseEntity<String> response = client.exchange(url, HttpMethod.POST, request, String.class);
            log.debug("path=[{}], {}", path, response.getBody());
            return Optional.ofNullable(objectMapper.readValue(response.getBody(), respType));
        } catch (RestClientException ex) {
            log.warn("http post request error", ex);
        } catch (JsonProcessingException ex) {
            log.warn("convert json failed", ex);
        }
        return Optional.empty();
    }

    public MultiValueMap<String, Object> pojoToPostParams(Serializable pojo) {
        Map<String, Object> map = objectMapper.convertValue(pojo, Map.class);
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        map.forEach((key, value) -> multiValueMap.add(key, value));
        return multiValueMap;
    }

    private String buildUrl(String path) {
        return xfyunAsrProperties.getHost().endsWith("/")
            ? (xfyunAsrProperties.getHost() + path)
            : (xfyunAsrProperties.getHost() + '/' + path);
    }

    public static class FileNameAwareByteArrayResource extends ByteArrayResource {

        private String filename;

        public FileNameAwareByteArrayResource(String filename, byte[] byteArray) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }

    }

}
