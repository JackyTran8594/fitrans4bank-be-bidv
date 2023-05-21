package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.ProfileDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Api
public interface ReportApi {

  @ApiOperation(value = "Get list of profile in the System ", response = Iterable.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 401, message = "Not authorized!"),
      @ApiResponse(code = 403, message = "Forbidden!"),
      @ApiResponse(code = 404, message = "Not found!")})
  Page<ProfileDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber,
      int pageSize);
  
}
