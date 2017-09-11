package util;

/**
 * yanyi on 2016/10/27.
 * 网络请求日志
 */

public class NetLogger {
    public static final String TAG = "new_http";

    public static void log(String log) {
        Logger.e(TAG,log);
    }

    public static void logWithLongMethodTrace(String log) {
        Logger.logWithMethodTraceCount(TAG,log,10);
    }
    public static void json(String json) {
        Logger.json(TAG,json);
    }

    public static void d(Object object) {
       Logger.d(TAG,object);
    }

}
