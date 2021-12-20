package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.response.StaffDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@Api
public interface StaffApi {

  @ApiOperation(value = "Get list of staff in the System ", response = Iterable.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 401, message = "Not authorized!"),
      @ApiResponse(code = 403, message = "Forbidden!"),
      @ApiResponse(code = 404, message = "Not found!")})
  Page<StaffDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber,
      int pageSize);

  @ApiOperation(value = "Get staff by ID", response = StaffDTO.class)
  StaffDTO getById(Long id);

  @ApiOperation(value = "Create staff", response = StaffDTO.class)
  StaffDTO create(StaffDTO dto);

  @ApiOperation(value = "Update staff by ID", response = StaffDTO.class)
  StaffDTO update(Long id, StaffDTO dto);

  @ApiOperation(value = "Delete staff", response = Boolean.class)
  Boolean delete(Long id);
}
