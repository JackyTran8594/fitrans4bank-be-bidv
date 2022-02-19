package com.eztech.fitrans.controller;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Api
public interface TestApi {

  @RequestMapping(value = "importCustomer", method = RequestMethod.GET)
  ResponseEntity<byte[]> test() throws Exception;

}
