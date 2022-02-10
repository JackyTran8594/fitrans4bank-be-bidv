package com.eztech.fitrans.controller;

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
public interface RoleApi {

    @ApiOperation(value = "Get list of role in the System ", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Not authorized!"),
            @ApiResponse(code = 403, message = "Forbidden!"),
            @ApiResponse(code = 404, message = "Not found!")})
    Page<RoleDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber, int pageSize);

    @ApiOperation(value = "Get role by ID", response = RoleDTO.class)
    RoleDTO getById(Long id);

    @ApiOperation(value = "Create role", response = RoleDTO.class)
    RoleDTO create(RoleDTO dto);

    @ApiOperation(value = "Update role by ID", response = RoleDTO.class)
    RoleDTO update(Long id, RoleDTO dto);

    @ApiOperation(value = "Delete role", response = Boolean.class)
    Boolean delete(Long id);

    @ApiOperation(value = "Map tree role")
    List<RoleTreeDTO> treeAll();
}
