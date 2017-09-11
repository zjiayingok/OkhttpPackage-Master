package httputil;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import util.NetLogger;

/**
 * Created by zhangjiaying on 2017/8/12.
 * 网络请求
 */

public class HttpUtil {
    /**
     * JSON格式请求信息
     */
    public static MediaType JSONTYPE
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * 二进制流数据（如常见的文件下载）
     */
    public static final String MIME_APPLICATION_OCTET_STREAM = "application/octet-stream";

    public static final int CONNECT_TIMEOUT = 10;
    public static final int READ_TIMEOUT = 10;
    public static final int WRITE_TIMEOUT = 20;
    //默认okHttpClient配置
    private static HttpConfig defaultConfig;

    private static final String DEFAULT_TAG = "default_httpClient";

    private static final String GZIP_TAG = "gzip_client";

    private static final String UPLOAD_TAG = "upload_tag";

    //默认httpClient对象
    public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    //最多允许创建httpClient对象的个数
    public static final int MAX_CLIENT_SIZE = 3;
    //存放httpClient的集合
    public static TreeMap<String, OkHttpClient> clients = new TreeMap<>();

    public HttpUtil() {
    }

    /**
     * 正常的http请求，使用默认配置信息
     *
     * @param iHttpParams  请求条件集合
     * @param httpCallback 请求回调
     */
    public static void request(IHttpParams iHttpParams, HttpCallback httpCallback) {
        request(getDefaultConfig(), iHttpParams, httpCallback);
    }

    /**
     * 通用网络请求
     */
    public static void request(HttpConfig httpConfig, IHttpParams iHttpParams, HttpCallback httpCallback) {
        OkHttpClient okHttpClient = configHttpClient(httpConfig);
        //调用okhttp发送请求
        httpCallback.setTag(iHttpParams.getTag());
        okHttpClient.newCall(createRequest(iHttpParams)).enqueue(httpCallback.getCallback());
    }

    private static OkHttpClient configHttpClient(HttpConfig httpConfig) {
        OkHttpClient okHttpClient = OK_HTTP_CLIENT;

        if (httpConfig != null && httpConfig.getTag() != null) {
            okHttpClient = clients.get(httpConfig.getTag());
            if (okHttpClient == null) {
                okHttpClient = OK_HTTP_CLIENT.clone();
            }
            setOkHttpConfig(okHttpClient, httpConfig);
            if (clients.size() > MAX_CLIENT_SIZE) {
                //防止多余的client
                clients.clear();
            }
            clients.put(httpConfig.getTag(), okHttpClient);
        } else {
            setOkHttpConfig(okHttpClient, getDefaultConfig());
        }
        return okHttpClient;
    }


    //设置okHttpClient配置
    private static void setOkHttpConfig(OkHttpClient okHttpClient, HttpConfig httpConfig) {
        okHttpClient.setConnectTimeout(httpConfig.getConnect_timeout(), TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(httpConfig.getRead_timeout(), TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(httpConfig.getWrite_timeout(), TimeUnit.SECONDS);
        okHttpClient.setSslSocketFactory(httpConfig.getSslSocketFactory());
        okHttpClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        if (GZIP_TAG.equals(httpConfig.getTag()) && httpConfig.getInterceptors() != null && !httpConfig.getInterceptors().isEmpty()) {
            //拦截器，主要用于gzip压缩
            okHttpClient.interceptors().addAll(httpConfig.getInterceptors());
        }
    }

    //创建默认配置
    private static HttpConfig getDefaultConfig() {
        if (defaultConfig == null) {
            defaultConfig = new HttpConfig.Builder().setTag(DEFAULT_TAG).connectTimeout(CONNECT_TIMEOUT).
                    readTimeout(READ_TIMEOUT).writeTimeout(WRITE_TIMEOUT).setSslSocketFactory(newInstanceForSSLContext().
                    getSocketFactory()).build();

        }
        return defaultConfig;
    }

    private static Request createGzipRequest(IHttpParams iHttpParams) {
        if (iHttpParams == null) {
            throw new NullPointerException("http params is null,you must init a httpParams");
        }
        Map<String, String> paramsMap = iHttpParams.getRequestParams();
        JSONObject jsonObject = new JSONObject();
        for (String s : paramsMap.keySet()) {
            try {
                jsonObject.put(s, paramsMap.get(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonStr = jsonObject.toString();
        RequestBody requestBody = RequestBody.create(JSONTYPE, jsonStr);
        return new Request.Builder().url(iHttpParams.getUrl()).post(requestBody).addHeader("Content-Type", "text/html;charset=UTF-8").addHeader("Accept-Encoding", "gzip, deflate").build();
    }

    /**
     * 创建okHttp request对象
     *
     * @param iHttpParams 请求条件集合 包括请求url，请求方法，请求参数等
     * @return request对象
     */
    private static Request createRequest(IHttpParams iHttpParams) {
        if (iHttpParams == null) {
            throw new NullPointerException("http params is null,you must init a httpParams");
        }
        Request mRequest;
        String url = iHttpParams.getUrl();
        NetLogger.logWithLongMethodTrace("request url = " + url);
        HttpRequestMethod httpRequestMethod = iHttpParams.getRequestMethod();
        Map<String, String> paramsMap = iHttpParams.getEncryptRequestParams();
        Object tag = iHttpParams.getTag();
        RequestBody requestBody = null;
        //支持多文件上传
        if (iHttpParams.getUploadFileList() != null && iHttpParams.getUploadFileList().size() > 0) {
            MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
            if (paramsMap != null) {
                // 这里可以对集合参数做二次处理
                for (String key : paramsMap.keySet()) {
                    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(paramsMap.get(key))) {
                        continue;
                    }
                    String value = paramsMap.get(key);
                    multipartBuilder.addFormDataPart(key, value);
                }
                NetLogger.d(paramsMap);
            }
            //getUploadFile和getUploadFileList()互斥
            List<File> files = iHttpParams.getUploadFileList();
//            if (files == null || files.isEmpty()) {
//                files = Collections.singletonList(iHttpParams.getUploadFile());
//            }
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file == null || TextUtils.isEmpty(file.getPath())) {
                    continue;
                }
                String name = iHttpParams.getUploadFileToServerParamName();
                if (name == null || name.length() <= 0) {
                    //只有正常的上传文件是后面要加index的，其它的上传key都是一样的
                    name = (i == 0 ? IHttpParams.DEFAULT_UPLOAD_FILE_PARAM_NAME : IHttpParams.DEFAULT_UPLOAD_FILE_PARAM_NAME + i);
                }
                NetLogger.log("request upload file is " + file.getName() + "----------> " + file.getPath() + ",netName -------->"+name);
                multipartBuilder.addFormDataPart(name, file.getName(), RequestBody.create(MediaType.parse(MIME_APPLICATION_OCTET_STREAM), file));
            }
            requestBody = multipartBuilder.build();
        } else {
            FormEncodingBuilder builder = new FormEncodingBuilder();
            if (paramsMap != null && paramsMap.size() > 0) {
                NetLogger.d(paramsMap);
                // 这里可以对集合参数做二次处理
                for (String key : paramsMap.keySet()) {
                    if (TextUtils.isEmpty(key) || TextUtils.isEmpty(paramsMap.get(key))) {
                        continue;
                    }
                    String value = paramsMap.get(key);
                    builder.add(key, value);
                }
                requestBody = builder.build();
            }
        }
        if (requestBody == null) {
            //如果参数为空，默认使用get方法获取
            mRequest = new Request.Builder().url(url).get().tag(tag).build();
        } else {
            //默认使用post方法
            switch (httpRequestMethod) {
                case GET:
                    mRequest = new Request.Builder().url(url).get().tag(tag).build();
                    break;
                case DELETE:
                    mRequest = new Request.Builder().url(url).delete(requestBody).tag(tag).build();
                    break;
                case POST:
                default:
                    mRequest = new Request.Builder().url(url).post(requestBody).tag(tag).build();
                    break;
            }
        }
        return mRequest;
    }


    private static SSLContext newInstanceForSSLContext() {
        SSLContext sslContext = null;
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] x509Certificates = new X509Certificate[0];
                        return x509Certificates;
                    }
                }};
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (Exception e) {
        }
        return sslContext;
    }

    //请求失败异常信息
    public static String getResponseErrorMsg(Response response) {
        return "request fail the response code =" + response.code() + ",message = " + response.message();
    }

    /**
     * 取消http请求
     *
     * @param tag tag对象
     */
    public static void cancel(Object tag) {
        if (tag == null) {
            return;
        }
        //这个在StrictMode下报错android.os.NetworkOnMainThreadException,新的oKHttp版本已经修复了,不过现在程序还是用的旧版本,so,先catch住,等待后待续升级
        try {
            OK_HTTP_CLIENT.cancel(tag);
            if (!clients.isEmpty()) {
                for (OkHttpClient okHttpClient : clients.values()) {
                    if (okHttpClient != null) {
                        okHttpClient.cancel(tag);
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 上传文件
     */
    public static void upload(IHttpParams iHttpParams, HttpCallback httpCallback) {
        HttpConfig httpConfig = new HttpConfig.Builder().setTag(UPLOAD_TAG).connectTimeout(100 * 1000).readTimeout(100 * 1000).writeTimeout(100 * 1000).build();
        request(httpConfig, iHttpParams, httpCallback);
    }

    /**
     * gzip 发送请求
     * httpCallback必须设置isGzip方法为true
     */
    public static void requestByGzip(IHttpParams iHttpParams, HttpCallback httpCallback) {
        HttpConfig httpConfig = new HttpConfig.Builder().setTag(GZIP_TAG).connectTimeout(CONNECT_TIMEOUT).readTimeout(READ_TIMEOUT).writeTimeout(WRITE_TIMEOUT).build();
        httpConfig.setInterceptors(Collections.<CKHttpIntercptor>singletonList(new GzipInterceptor()));
        OkHttpClient okHttpClient = configHttpClient(httpConfig);
        httpCallback.setGzip(true);
        okHttpClient.newCall(createGzipRequest(iHttpParams)).enqueue(httpCallback.getCallback());
    }


    /**
     * 在子线程中直接运行请求
     *
     * @throws IOException
     */
    public static String execute(HttpConfig httpConfig, IHttpParams iHttpParams) throws IOException {
        OkHttpClient okHttpClient = configHttpClient(httpConfig);
        Response response = okHttpClient.newCall(createRequest(iHttpParams)).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException(getResponseErrorMsg(response));
        }
    }

    public static String execute(IHttpParams iHttpParams) throws IOException {
        return execute(getDefaultConfig(), iHttpParams);
    }

}
