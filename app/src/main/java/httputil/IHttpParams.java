package httputil;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangjiaying on 2017/8/12.
 */

public interface IHttpParams {

    /**
     * @return 返回 请求的api
     */
    String getUrl();

    /**
     * @return 返回传递的请求参数
     */
    Map<String,String> getRequestParams();

    /**
     * @return get or post
     */
    HttpRequestMethod getRequestMethod();

    /**
     * @return 返回tag
     */
    Object getTag();

    /**
     * 批量上传文件
     * @return
     */
    List<File> getUploadFileList();

    /**
     * 默认上传文件对应的key，如果是多个的话file+index
     */
    String DEFAULT_UPLOAD_FILE_PARAM_NAME = "file";
    /**
     * 默认上传头像对应的key，在"我"里面的个人设置中使用
     */
    String DEFAULT_UPLOAD_FILE_PARAM_NAME_FOR_AVATAR = "avatar";
    /**
     * 默认上传发言图片，语音等文件的对应key
     */
    String DEFAULT_UPLOAD_FILE_PARAM_NAME_FOR_FEED = "file[]";

    /**
     * 上传到服务器的属性名称，默认为"list"，但是在修改个人资料的地方叫"avatar"
     */
    String getUploadFileToServerParamName();

    Map<String, String> getEncryptRequestParams();

    /**
     * 不需要加盐处理
     */
    boolean noNeedEncrypt();
}

