package httputil;

import android.util.Base64;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import httputil.HttpUtil;
import util.NetLogger;

/**
 * okHttp回调函数
 */
public abstract class HttpCallback {
    private Callback callback;
    private Object tag;

    // 需要支持gzip的inputStream
    public void setTag(Object tag) {
        this.tag = tag;
    }
    private boolean gzip;
    /**
     * 是否是Gzip压缩
     */
    public final boolean isGzip() {
        return gzip;
    }

    public final void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    public HttpCallback() {
        callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                NetLogger.log(e.getMessage());
                onError(new HttpException(e), tag);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result;
                    if (isGzip()) {
                        //gzip返回字符串解压缩
                        InputStream inputStream = response.body().byteStream();
                        result = setInputStreamToString(new GZIPInputStream(inputStream));
                        NetLogger.log("request gzip result = " + result);
                    } else {
                        result = response.body().string();
                        NetLogger.json(result);
                    }
                    // 可能需要做二次处理
                    onSuccess(result, tag);
                } else {
                    //请求失败
                    String errorMsg = HttpUtil.getResponseErrorMsg(response);
                    NetLogger.log("request fail the error msg is " + errorMsg);
                    if (response.code() == 500) {
                        //服务器报错
                        onError(new HttpException(HttpException.ERRORCODE_FOR_LOCAL, HttpException.DESCRIPTION_SERVER_INTERNAL_ERROR), tag);
                    } else {
                        onError(new HttpException(new IOException(errorMsg)), tag);
                    }
                }
            }
        };
    }


    public abstract void onSuccess(String result, Object tag);

    public abstract void onError(HttpException exception, Object tag);

    public Callback getCallback() {
        return callback;
    }


    public static String setInputStreamToString(InputStream inputStream) {
        String content = "";
        try {
            content = new String(Base64.decode(inputStream2String(inputStream, "UTF-8"),
                    Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }


    public final static int BUFFER_SIZE = 4 * 1024;

    public static String inputStream2String(InputStream is, String code)
            throws HttpException{
        if (is == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[BUFFER_SIZE];
        int len;
        try {
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
        } catch (IOException e) {
            throw new HttpException(e);
        }

        String result = null;
        try {
            result = new String(baos.toByteArray(), code);
        } catch (UnsupportedEncodingException e) {
            // bug??
            result = new String(baos.toByteArray());
        } finally {
            try {
                baos.close();
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        return result;
    }



}
