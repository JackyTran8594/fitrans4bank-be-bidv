package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.response.ActionLogDTO;
import com.eztech.fitrans.dto.response.RoleDTO;
import com.eztech.fitrans.dto.response.RoleTreeDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Api
public interface LogApi {

    @ApiOperation(value = "Get list of log in the System ", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Not authorized!"),
            @ApiResponse(code = 403, message = "Forbidden!"),
            @ApiResponse(code = 404, message = "Not found!")})
    Page<ActionLogDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber, int pageSize);

    @ApiOperation(value = "Delete log", response = Boolean.class)
    Boolean delete(Long id);
}
