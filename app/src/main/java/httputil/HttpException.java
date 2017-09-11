package httputil;

import android.text.TextUtils;

import util.NetLogger;


/**
 * 封装的网络请求异常信息
 */
public class HttpException extends Exception {

    public static final String DESCRIPTION_PARSE_ERROR = "数据解析失败,请检查数据格式";
    public static final String DESCRIPTION_SERVER_INTERNAL_ERROR = "服务器内部错误";
    public static final String DESCRIPTION_NOT_CONTAIN_ERRORCODE = "服务器返回的参数有误";
    public static final String DESCRIPTION_RESULT_DATA_NULL = "服务器返回数据有误";
    public static final String DESCRIPTION_NET_IO_EXCEPTION = "网络异常,请稍后再试";
    //android客户端自定义error code
    public static final int ERRORCODE_FOR_LOCAL = -1;
    // TODO: 16/3/24 需要细化异常信息处理
    private Exception exception;

    public HttpException(Exception exception) {
        this.exception = exception;
    }

    private int error_code;
    private String error_description;

    public HttpException(int error_code, String error_description) {
        this.error_code = error_code;
        this.error_description = error_description;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_description() {
        if (TextUtils.isEmpty(error_description)) {
            if (exception != null) {
                NetLogger.log("getError_description:  = " + exception.getMessage());
            }
            return DESCRIPTION_NET_IO_EXCEPTION;
        }
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
    public String showErrorMessage() {
        return showErrorMessage(false);
    }
    public String showErrorMessage(boolean showErrorCode) {
        if (showErrorCode) {
            return getError_code() + ":" + getError_description();
        } else {
            return getError_description();
        }
    }
}
