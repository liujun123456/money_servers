package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class HttpUtils {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * GET请求
     */
    public String get(String url) throws IOException {
        return get(url, null);
    }

    public String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);
        addHeaders(builder, headers);

        Request request = builder.build();
        return executeRequest(request);
    }

    /**
     * POST请求 - JSON格式
     */
    public String postJson(String url, Object body) throws IOException {
        return postJson(url, body, null);
    }

    public String postJson(String url, Object body, Map<String, String> headers) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(body);
        RequestBody requestBody = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);

        addHeaders(builder, headers);

        Request request = builder.build();
        return executeRequest(request);
    }

    /**
     * POST请求 - Form格式
     */
    public String postForm(String url, Map<String, String> formData) throws IOException {
        return postForm(url, formData, null);
    }

    public String postForm(String url, Map<String, String> formData, Map<String, String> headers) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (formData != null) {
            formData.forEach(formBuilder::add);
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBuilder.build());

        addHeaders(builder, headers);

        Request request = builder.build();
        return executeRequest(request);
    }

    /**
     * PUT请求
     */
    public String putJson(String url, Object body) throws IOException {
        return putJson(url, body, null);
    }

    public String putJson(String url, Object body, Map<String, String> headers) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(body);
        RequestBody requestBody = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(requestBody);

        addHeaders(builder, headers);

        Request request = builder.build();
        return executeRequest(request);
    }

    /**
     * DELETE请求
     */
    public String delete(String url) throws IOException {
        return delete(url, null);
    }

    public String delete(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url).delete();
        addHeaders(builder, headers);

        Request request = builder.build();
        return executeRequest(request);
    }

    /**
     * 执行请求
     */
    private String executeRequest(Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response + ", body: " +
                        (response.body() != null ? response.body().string() : ""));
            }

            ResponseBody body = response.body();
            return body != null ? body.string() : "";
        }
    }

    /**
     * 异步GET请求
     */
    public void getAsync(String url, Callback callback) {
        getAsync(url, null, callback);
    }

    public void getAsync(String url, Map<String, String> headers, Callback callback) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeaders(builder, headers);

        Request request = builder.build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 添加请求头
     */
    private void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        // 添加默认请求头
        builder.addHeader("User-Agent", "MyApp/1.0");
        builder.addHeader("Accept", "application/json");
    }

    /**
     * 下载文件
     */
    public byte[] downloadFile(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download file: " + response);
            }

            ResponseBody body = response.body();
            return body != null ? body.bytes() : new byte[0];
        }
    }
}