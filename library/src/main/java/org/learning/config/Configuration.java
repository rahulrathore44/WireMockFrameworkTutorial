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

    private Configuration(ConfigurationBuilder builder){
        this.url = builder.ulr;
        this.contentType = builder.contentType;
    }

    public static class ConfigurationBuilder  {
        private String ulr;
        private ContentType contentType;

        public ConfigurationBuilder withUrl(String url){
            this.ulr = url;
            return this;
        }

        public ConfigurationBuilder withContentType(ContentType contentType){
            this.contentType = contentType;
            return this;
        }

        public Configuration build(){
            return new Configuration(this);
        }

    }
}
