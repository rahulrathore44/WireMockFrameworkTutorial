package org.learning.config;

import org.apache.http.entity.ContentType;

public class Configuration {

    public String getUrl() {
        return url;
    }

    public ContentType getContentType() {
        return contentType;
    }

    private final String url;
    private final ContentType contentType;
    private final Integer connectionTimeout;

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    private final Integer socketTimeout;

    private Configuration(ConfigurationBuilder builder) {
        this.url = builder.ulr;
        this.contentType = builder.contentType;
        if (builder.connectionTimeout == null || builder.connectionTimeout == 0) {
            this.connectionTimeout = 10000;
        } else {
            this.connectionTimeout = builder.connectionTimeout;
        }

        if (builder.socketTimeout == null || builder.socketTimeout == 0) {
            this.socketTimeout = 10000;
        } else {
            this.socketTimeout = builder.socketTimeout;
        }
    }

    public static class ConfigurationBuilder {
        private String ulr;
        private ContentType contentType;
        private Integer connectionTimeout;
        private Integer socketTimeout;

        public ConfigurationBuilder withUrl(String url) {
            this.ulr = url;
            return this;
        }

        public ConfigurationBuilder withContentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public ConfigurationBuilder withConnectionTimeOut(Integer connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public ConfigurationBuilder withSocketTimeOut(Integer socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }

    }
}
