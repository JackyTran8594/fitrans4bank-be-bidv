package com.eztech.fitrans.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class RejectedTaskHandler implements RejectedExecutionHandler {

  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    log.info("RejectedTaskHandler: The task {} has been rejected", r.toString());
    try {
      log.info("Resubmit task to queue: {}", r.toString());
      executor.getQueue().put(r);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
  }
}
