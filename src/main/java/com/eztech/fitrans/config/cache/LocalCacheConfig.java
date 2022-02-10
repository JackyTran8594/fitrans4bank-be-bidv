package com.eztech.fitrans.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableConfigurationProperties(LocalCacheConfigurationProperties.class)
@ConditionalOnProperty(value = "app.cache.enable", havingValue = "true", matchIfMissing = true)
@SuppressWarnings("squid:S3740")
public class LocalCacheConfig {

    @Bean
    public Caffeine caffeineCacheBuilder(LocalCacheConfigurationProperties properties) {
        return Caffeine.from(properties.getCaffeine().getSpec());
    }

    @Bean("localCacheManager")
    @Primary
    public CacheManager localCacheManager(
            Caffeine caffeineCacheBuilder, LocalCacheConfigurationProperties properties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        if (properties.getCacheNames() == null || "".equals(properties.getCacheNames())) {
            cacheManager.setCacheNames(null); // dynamic
        } else {
            cacheManager.setCacheNames(Arrays.asList(properties.getCacheNames()));
        }
        log.info("Configuring local cache manager");
        log.info("Using caffeine: {}", caffeineCacheBuilder.toString());
        log.info("Cache names: {}", properties.getCacheNames());
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }
}

