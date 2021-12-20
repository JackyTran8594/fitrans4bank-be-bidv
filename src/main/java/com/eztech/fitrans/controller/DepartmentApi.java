package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.response.DepartmentDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@Api
public interface DepartmentApi {

    @ApiOperation(value = "Get list of department in the System ", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Suceess"), @ApiResponse(code = 401, message = "Not authorized!"), @ApiResponse(code = 403, message = "Forbidden!"),
            @ApiResponse(code = 404, message = "Not found!")})
    Page<DepartmentDTO> getList(@RequestParam Map<String,Object> mapParam, int pageNumber, int pageSize);

    @ApiOperation(value = "Get department by department ID", response = DepartmentDTO.class)
    DepartmentDTO getById(Long productId);

    @ApiOperation(value = "Create department", response = DepartmentDTO.class)
    DepartmentDTO create(DepartmentDTO product);

    @ApiOperation(value = "Update department by department ID", response = DepartmentDTO.class)
    DepartmentDTO update(Long productId, DepartmentDTO product);

    @ApiOperation(value = "Delete department by department ID", response = Boolean.class)
    Boolean delete(Long productId);
}
