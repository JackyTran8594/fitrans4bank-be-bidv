package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.response.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@Api
public interface UserApi {

  @ApiOperation(value = "Get list of user in the System ", response = Iterable.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 401, message = "Not authorized!"),
      @ApiResponse(code = 403, message = "Forbidden!"),
      @ApiResponse(code = 404, message = "Not found!")})
  Page<UserDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber,
      int pageSize);

  @ApiOperation(value = "Get user by ID", response = UserDTO.class)
  UserDTO getById(Long id);

  @ApiOperation(value = "Create user", response = UserDTO.class)
  UserDTO create(UserDTO dto);

  @ApiOperation(value = "Update user by ID", response = UserDTO.class)
  UserDTO update(Long id, UserDTO dto);

  @ApiOperation(value = "Delete user", response = Boolean.class)
  Boolean delete(Long id);
}
