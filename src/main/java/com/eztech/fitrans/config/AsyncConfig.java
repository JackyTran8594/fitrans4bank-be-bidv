package com.eztech.fitrans.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
@Getter
public class AsyncConfig {

  @Value("${executor.corePoolSize:30}")
  private Integer poolSize;

  @Value("${executor.maxPoolSize:200}")
  private Integer setMaxPoolSize;

  @Value("${executor.queueCapacity:10}")
  private Integer queueSize;

  @Value("${executor.keepAliveSeconds:120}")
  private Integer timeout;

  @Bean("ayncTaskExecutor")
  public TaskExecutor ayncTaskExecutor() {
    log.info("ayncTaskExecutor corePoolSize : " + poolSize + ", MaxPoolSize : " + setMaxPoolSize);
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(poolSize);
    executor.setMaxPoolSize(setMaxPoolSize);
    executor.setQueueCapacity(queueSize);
    executor.setKeepAliveSeconds(timeout);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setRejectedExecutionHandler(new RejectedTaskHandler());
    executor.setThreadNamePrefix("ayncTaskExecutor-");
    return executor;
  }
}
