package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.response.PriorityCardDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Api
public interface PriorityCardApi {

    @ApiOperation(value = "Get list of PriorityCard in the System ", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Not authorized!"),
            @ApiResponse(code = 403, message = "Forbidden!"),
            @ApiResponse(code = 404, message = "Not found!")})
    Page<PriorityCardDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber, int pageSize);

    @ApiOperation(value = "Get PriorityCard by ID", response = PriorityCardDTO.class)
    PriorityCardDTO getById(Long id);

    @ApiOperation(value = "Create PriorityCard", response = PriorityCardDTO.class)
    PriorityCardDTO create(PriorityCardDTO dto);

    @ApiOperation(value = "Update PriorityCard by ID", response = PriorityCardDTO.class)
    PriorityCardDTO update(Long id, PriorityCardDTO dto);

    @ApiOperation(value = "Delete PriorityCard", response = Boolean.class)
    Boolean delete(Long id);

    @ApiOperation(value = "Delete list PriorityCard", response = Boolean.class)
    Boolean delete(List<Long> ids);

}
