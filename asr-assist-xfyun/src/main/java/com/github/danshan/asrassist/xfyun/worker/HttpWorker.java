package com.github.danshan.asrassist.xfyun.worker;

import com.github.danshan.asrassist.xfyun.event.Event;
import com.github.danshan.asrassist.xfyun.http.HttpUtil;
import com.github.danshan.asrassist.xfyun.model.FileSlice;
import com.github.danshan.asrassist.xfyun.model.UploadParams;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpWorker {
    private static String url_prepare;
    private static String url_upload;
    private static String url_meger;
    private static String url_result;
    private static String url_progress;
    private static String url_version;

    public HttpWorker() {
    }

    public String prepare(UploadParams upParams) {
        Map<String, String> params = new HashMap();
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
        Map<String, String> p = upParams.getParams();
        if (p != null) {
            Iterator iter = p.entrySet().iterator();

            while(iter.hasNext()) {
                Entry entry = (Entry)iter.next();
                params.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        return HttpUtil.post(url_prepare, params);
    }

    public String handle(Event event) {
        FileSlice fileSlice = event.getFileSlice();
        UploadParams params = event.getParams();
        HashMap<String, String> hm = new HashMap();
        hm.put("app_id", params.getSignature().getAppId());
        hm.put("signa", params.getSignature().getSigna());
        hm.put("ts", params.getSignature().getTs());
        hm.put("slice_id", fileSlice.getSliceId());
        hm.put("task_id", params.getTaskId());
        return HttpUtil.postMulti(url_upload, hm, fileSlice.getBody());
    }

    public String merge(Event event) {
        UploadParams params = event.getParams();
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        map.put("file_name", params.getFile().getName());
        return HttpUtil.post(url_meger, map);
    }

    public String getResult(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        return HttpUtil.post(url_result, map);
    }

    public String getProgress(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("task_id", params.getTaskId());
        return HttpUtil.post(url_progress, map);
    }

    public String getVersion(UploadParams params) {
        HashMap<String, String> map = new HashMap();
        map.put("app_id", params.getSignature().getAppId());
        map.put("signa", params.getSignature().getSigna());
        map.put("ts", params.getSignature().getTs());
        map.put("client_version", params.getClientVersion());
        return HttpUtil.post(url_version, map);
    }

    static {
        url_prepare = LfasrClientImp.SERV_LFASR_HOST_VAL + "/prepare";
        url_upload = LfasrClientImp.SERV_LFASR_HOST_VAL + "/upload";
        url_meger = LfasrClientImp.SERV_LFASR_HOST_VAL + "/merge";
        url_result = LfasrClientImp.SERV_LFASR_HOST_VAL + "/getResult";
        url_progress = LfasrClientImp.SERV_LFASR_HOST_VAL + "/getProgress";
        url_version = LfasrClientImp.SERV_LFASR_HOST_VAL + "/getVersion";
    }
}
