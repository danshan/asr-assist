package com.github.danshan.asrassist.xfyun.worker;

import com.github.danshan.asrassist.xfyun.config.XfyunAsrProperties;
import com.github.danshan.asrassist.xfyun.event.Event;
import com.github.danshan.asrassist.xfyun.http.HttpUtil;
import com.github.danshan.asrassist.xfyun.model.FileSlice;
import com.github.danshan.asrassist.xfyun.model.Message;
import com.github.danshan.asrassist.xfyun.model.UploadParams;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpWorker {
    private final String urlPrepare;
    private final String urlUpload;
    private final String urlMerge;
    private final String urlResult;
    private final String urlProgress;
    private final String urlVersion;

    public HttpWorker(XfyunAsrProperties asrProperties) {
        urlPrepare = asrProperties.getHost() + "/prepare";
        urlUpload = asrProperties.getHost() + "/upload";
        urlMerge = asrProperties.getHost() + "/merge";
        urlResult = asrProperties.getHost() + "/getResult";
        urlProgress = asrProperties.getHost() + "/getProgress";
        urlVersion = asrProperties.getHost() + "/getVersion";
    }

    public Message prepare(UploadParams upParams) {
        Map params = new HashMap<String, String>();
        params.put("app_id", upParams.getSignature().getAppId());
        params.put("secret_key", upParams.getSignature().getSecretKey());
        params.put("signa", upParams.getSignature().getSigna());
        params.put("ts", upParams.getSignature().getTs());
        params.put("file_len", String.valueOf(upParams.getFile().length()));
        params.put("file_name", upParams.getFile().getName());
        params.put("lfasr_type", String.valueOf(upParams.getLfasrType().getValue()));
        params.put("slice_num", String.valueOf(upParams.getSliceNum()));
        params.put("client_version", upParams.getClientVersion());
        params.put("check_length", upParams.getCheckLength() + "");

        Optional.ofNullable(upParams.getParams()).ifPresent(params::putAll);
        return HttpUtil.post(urlPrepare, params);
    }

    public Message handle(Event event) {
        FileSlice fileSlice = event.getFileSlice();
        UploadParams params = event.getParams();
        HashMap<String, String> hm = new HashMap();
        hm.put("app_id", params.getSignature().getAppId());
        hm.put("signa", params.getSignature().getSigna());
        hm.put("ts", params.getSignature().getTs());
        hm.put("slice_id", fileSlice.getSliceId());
        hm.put("task_id", params.getTaskId());
        return HttpUtil.postMulti(urlUpload, hm, fileSlice.getBody());
    }

    public Message merge(Event event) {
        UploadParams params = event.getParams();
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        map.put("file_name", params.getFile().getName());
        return HttpUtil.post(urlMerge, map);
    }

    public Message getResult(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        return HttpUtil.post(urlResult, map);
    }

    public Message getProgress(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        return HttpUtil.post(urlProgress, map);
    }

    public Message getVersion(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("client_version", params.getClientVersion());
        return HttpUtil.post(urlVersion, map);
    }

}
