package com.eztech.fitrans.controller;

import java.util.Map;

import com.eztech.fitrans.dto.response.StaffContactDTO;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;


@Api
public interface StaffContactApi {
    
    @ApiOperation(value = "Get list of staffContact in the System", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 401, message = "Not authorized!"),
    @ApiResponse(code = 403, message = "Forbidden!"),
    @ApiResponse(code = 404, message = "Not found!")})

    Page<StaffContactDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber, int pageSize);

    @ApiOperation(value = "Get staffContact by ID", response = StaffContactDTO.class)
    StaffContactDTO getById(Long id);

    @ApiOperation(value = "Create staffContact", response = StaffContactDTO.class)
    StaffContactDTO create(StaffContactDTO dto);

    @ApiOperation(value = "Update staffContact by ID", response = StaffContactDTO.class)
    StaffContactDTO update(Long id, StaffContactDTO dto);

    @ApiOperation(value = "Delete staffContact", response = StaffContactDTO.class)
    Boolean delete(Long id);

}
