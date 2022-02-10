package com.eztech.fitrans.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "app.cache")
@Data
public class LocalCacheConfigurationProperties {

    private String cacheNames = "";
    @NestedConfigurationProperty
    private CaffeineCacheConfig caffeine;

    @Data
    public static class CaffeineCacheConfig {

        private String spec = "";
    }
}

