package util;

import android.text.TextUtils;


/**
 */
public class Logger {
    //默认打印的方法栈长度
    private static final int DEFAULT_METHOD_COUNT = 5;
    private Logger() {

    }

    public static void i(String tag, String message) {
        if (TextUtils.isEmpty(message)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.i(message);
    }


    public static void i(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.i(msg, tr);
    }

    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.d(msg);
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.d(msg, tr);
    }

    public static void w(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.w(msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.w(msg, tr);
    }

    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(msg)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.e(msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (TextUtils.isEmpty(msg)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.e(msg, tr);
    }

    public static void d(String tag, Object object) {
        if (object == null) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.d(object);
    }

    /**
     * 打印对应长度的方法调用栈
     */
    public static void logWithMethodTraceCount(String tag, String log , int methodCount) {
        if (TextUtils.isEmpty(log)) return;
            com.orhanobut.logger.Logger.t(tag,methodCount);
            com.orhanobut.logger.Logger.e(log);
    }

    public static void json(String tag, String json) {
        if (TextUtils.isEmpty(json)) return;
            com.orhanobut.logger.Logger.t(tag,DEFAULT_METHOD_COUNT);
            com.orhanobut.logger.Logger.json(json);
    }
}
