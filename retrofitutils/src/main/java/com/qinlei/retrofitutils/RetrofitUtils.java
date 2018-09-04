package com.qinlei.retrofitutils;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.qinlei.retrofitutils.body.ProgressRequestBody;
import com.qinlei.retrofitutils.call.RequestCall;
import com.qinlei.retrofitutils.service.HttpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by cyc
 * Created on 2018/1/13
 * Created description :
 */

public class RetrofitUtils {
    private Call call;
    private RequestCall requestCall;
    private HttpService httpService = HttpCreator.getRestService();
    private static final HashMap<Object, List<Call>> callMap = new HashMap<>();

    private static HttpMethod httpMethod;
    private Object tag;
    private static int id;
    private String requestUrl;
    private WeakHashMap<String, Object> params = new WeakHashMap<>();
    private String content;//请求体 raw 时
    private List<FileUpload> uploads = new ArrayList<>();
    private String mediaType = "application/json;charset=UTF-8";

    protected RetrofitUtils(int id,Object tag, String requestUrl, WeakHashMap<String, Object> params, String content, List<FileUpload> uploads, String mediaType) {
        this.tag = tag;
        RetrofitUtils.id = id;
        this.requestUrl = requestUrl;
        if (params != null && params.size() != 0) {
            this.params.putAll(params);
        }
        this.content = content;
        if (uploads != null && uploads.size() != 0) {
            this.uploads.addAll(uploads);
        }
        if (!TextUtils.isEmpty(mediaType)) {
            this.mediaType = mediaType;
        }
    }
    /**
     * 构建上传的 MultipartBody.Part 可以上传多个文件
     *
     * @return
     */
    private List<MultipartBody.Part> getUploadParts() {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (FileUpload upload : uploads) {
            final RequestBody requestBody =
                    RequestBody.create(MediaType.parse(MultipartBody.FORM.toString()), upload.getFile());
            final MultipartBody.Part body =
                    MultipartBody.Part.createFormData(
                            upload.getKey(),
                            upload.getFile().getName(),
                            new ProgressRequestBody(requestBody, new ProgressRequestBody.ProgressRequestListener() {
                                @Override
                                public void onRequestProgress(int progress, int progressMax) {
                                    if (requestCall != null && requestCall.getBaseCallback() != null) {
                                        requestCall.getBaseCallback().inProgress(progress, progressMax);
                                    }
                                }
                            }));
            parts.add(body);
        }
        return parts;
    }

    /**
     * 构建 MediaType.parse("application/json;charset=UTF-8") 的请求体
     *
     * @return
     */
    @NonNull
    private RequestBody getRequestBody() {
        if (content == null || "".equals(content)) {
            throw new RuntimeException("build body is null");
        }
        return RequestBody.create(MediaType.parse(mediaType), content);
    }

    public static HttpBuilder get() {
        RetrofitUtils.httpMethod = HttpMethod.GET;
        return new HttpBuilder();
    }

    public static HttpBuilder post() {
        RetrofitUtils.httpMethod = HttpMethod.POST;
        return new HttpBuilder();
    }

    public static HttpBuilder postRaw() {
        RetrofitUtils.httpMethod = HttpMethod.POST_RAW;
        return new HttpBuilder();
    }

    public static HttpBuilder put() {
        RetrofitUtils.httpMethod = HttpMethod.PUT;
        return new HttpBuilder();
    }

    public static HttpBuilder putRaw() {
        RetrofitUtils.httpMethod = HttpMethod.PUT_RAW;
        return new HttpBuilder();
    }

    public static HttpBuilder delete() {
        RetrofitUtils.httpMethod = HttpMethod.DELETE;
        return new HttpBuilder();
    }

    public static HttpBuilder upload() {
        RetrofitUtils.httpMethod = HttpMethod.UPLOAD;
        return new HttpBuilder();
    }

    public static HttpBuilder download() {
        RetrofitUtils.httpMethod = HttpMethod.DOWNLOAD;
        return new HttpBuilder();
    }

    public RequestCall build() {
        switch (httpMethod) {
            case GET:
                call = httpService.get(requestUrl, params);
                break;
            case POST:
                call = httpService.post(requestUrl, params);
                break;
            case POST_RAW:
                call = httpService.postRaw(requestUrl, getRequestBody());
                break;
            case PUT:
                call = httpService.put(requestUrl, params);
                break;
            case PUT_RAW:
                call = httpService.putRaw(requestUrl, getRequestBody());
                break;
            case DELETE:
                call = httpService.delete(requestUrl, params);
                break;
            case UPLOAD:
                List<MultipartBody.Part> parts = getUploadParts();
                call = httpService.upload(requestUrl, parts);
                break;
            case DOWNLOAD:
                call = httpService.download(requestUrl, params);
                break;
        }

        List<Call> calls = callMap.get(tag);
        if (calls == null) {
            List<Call> callList = new ArrayList<>();
            callList.add(call);
            callMap.put(tag, callList);
        } else {
            calls.add(call);
        }
        requestCall = new RequestCall(call);
        return requestCall;
    }

    /**
     * 取消所有请求
     */
    public static void cancelAll() {
        for (Map.Entry<Object, List<Call>> entry : callMap.entrySet()) {
            List<Call> calls = entry.getValue();
            if (calls != null && calls.size() > 0) {
                for (Call callItem : calls) {
                    if (!callItem.isCanceled()) {
                        callItem.cancel();
                    }
                }
            }
        }
        callMap.clear();
    }

    /**
     * 根据设置的 tag 取消请求
     *
     * @param tag
     */
    public static void cancelTag(Object tag) {
        List<Call> calls = callMap.get(tag);
        if (calls != null && calls.size() > 0) {
            for (Call callItem : calls) {
                if (!callItem.isCanceled()) {
                    callItem.cancel();
                }
            }
        }
    }
    /**
     * 根据 RequestCall 取消请求
     *
     * @param call
     */
    public static void cancel(RequestCall call) {
        call.cancel();
    }

    public static int getId()
    {
        return id;
    }
}
