package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.ProfileDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Api
public interface ProfileApi {

  @ApiOperation(value = "Get list of profile in the System ", response = Iterable.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 401, message = "Not authorized!"),
      @ApiResponse(code = 403, message = "Forbidden!"),
      @ApiResponse(code = 404, message = "Not found!")})
  Page<ProfileDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber,
      int pageSize);

  @ApiOperation(value = "Get profile by ID", response = ProfileDTO.class)
  ProfileDTO getById(@RequestParam Map<String, Object> mapParam);

  @ApiOperation(value = "Create profile", response = ProfileDTO.class)
  ProfileDTO create(ConfirmRequest dto);

  @ApiOperation(value = "Update profile by ID", response = ProfileDTO.class)
  ProfileDTO update(Long id, ProfileDTO dto);

  @ApiOperation(value = "Delete profile", response = Boolean.class)
  Boolean delete(Long id);

}
