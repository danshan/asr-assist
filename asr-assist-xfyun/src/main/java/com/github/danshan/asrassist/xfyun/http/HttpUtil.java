package com.github.danshan.asrassist.xfyun.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.Message;
import com.github.danshan.asrassist.xfyun.model.XfyunResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Slf4j
public class HttpUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public HttpUtil() {
    }

    public static List<NameValuePair> convertMapToPair(Map<String, String> params) {
        List<NameValuePair> pairs = new ArrayList();
        Iterator var2 = params.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, String> entry = (Entry)var2.next();
            pairs.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
        }

        return pairs;
    }

    public static Message post(String url, Map<String, String> param) {
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(convertMapToPair(param), "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            return objectMapper.readValue(httpResponse.getEntity().getContent(), XfyunResponse.class).toMessage();
        } catch (Exception var19) {
            log.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "HttpUtil", "", "", "(-1) ms", "http post request error"), var19);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException var18) {
                log.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "HttpUtil", "", "", "(-1) ms", "httpResponse close error"), var18);
            }

            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException var17) {
                log.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "HttpUtil", "", "", "(-1) ms", "httpClient close error"), var17);
            }

        }

        return Message.failed(ErrorCode.ASR_API_ERR, null);
    }

    public static Message postMulti(String url, Map<String, String> param, byte[] body) {
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
        reqEntity.addPart("content", new ByteArrayBody(body, ContentType.DEFAULT_BINARY, (String)param.get("slice_id")));
        Iterator var8 = param.entrySet().iterator();

        while(var8.hasNext()) {
            Entry<String, String> entry = (Entry)var8.next();
            StringBody value = new StringBody((String)entry.getValue(), ContentType.create("text/plain", Consts.UTF_8));
            reqEntity.addPart((String)entry.getKey(), value);
        }

        HttpEntity httpEntiy = reqEntity.build();

        try {
            httpPost.setEntity(httpEntiy);
            httpResponse = httpClient.execute(httpPost);
            return objectMapper.readValue(httpResponse.getEntity().getContent(), XfyunResponse.class).toMessage();
        } catch (Exception var19) {
            log.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "HttpUtil", "", "", "(-1) ms", "http post multi request error"), var19);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException var18) {
                log.error(String.format("[COMPENT]-%s [PROCESS]-%s [ID]-%s [STATUS]-%s [MEASURE]-%s [DEF]-%s", "CLIENT", "HttpUtil", "", "", "(-1) ms", "httpClient close error"), var18);
            }

        }

        return Message.failed(ErrorCode.ASR_API_ERR, null);
    }
}