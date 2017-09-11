package httputil;

import java.util.List;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by zhangjiaying on 2017/8/12.
 */

public class HttpConfig {
    /**
     * httpClient的唯一标识
     */
    private String tag;
    /**
     * READ 超时
     */
    private int read_timeout;
    /**
     * 连接超时
     */
    private int connect_timeout;
    /**
     * 写超时
     */
    private int write_timeout;

    private List<CKHttpIntercptor> interceptors;

    private SSLSocketFactory sslSocketFactory;
    private HttpConfig() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getRead_timeout() {
        return read_timeout;
    }

    public int getConnect_timeout() {
        return connect_timeout;
    }

    public int getWrite_timeout() {
        return write_timeout;
    }

    public List<CKHttpIntercptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<CKHttpIntercptor> interceptors) {
        this.interceptors = interceptors;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }
    public static class Builder {
        private String tag;
        private int read_timeout;
        private int connect_timeout;
        private int write_timeout;
        private List<CKHttpIntercptor> interceptors;
        private SSLSocketFactory sslSocketFactory;

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }
        public Builder readTimeout(int read_timeout) {
            this.read_timeout = read_timeout;
            return this;
        }

        public Builder connectTimeout(int connect_timeout) {
            this.connect_timeout = connect_timeout;
            return this;
        }

        public Builder writeTimeout(int write_timeout) {
            this.write_timeout = write_timeout;
            return this;
        }

        public Builder addInterceptors(List<CKHttpIntercptor> interceptors) {
            this.interceptors = interceptors;
            return this;
        }
        public Builder setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public HttpConfig build() {
            HttpConfig httpConfig = new HttpConfig();
            httpConfig.read_timeout = this.read_timeout;
            httpConfig.write_timeout = this.write_timeout;
            httpConfig.connect_timeout = this.connect_timeout;
            httpConfig.interceptors = this.interceptors;
            httpConfig.tag = this.tag;
            httpConfig.sslSocketFactory = this.sslSocketFactory;
            return httpConfig;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof HttpConfig) {
            return this.tag.equals(((HttpConfig)o).tag);
        }
        return super.equals(o);
    }
}
