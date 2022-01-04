package com.eztech.fitrans.controller;

import java.util.Map;

import com.eztech.fitrans.dto.response.TransactionTypeDTO;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;


@Api
public interface TransactionTypeApi {
    
    @ApiOperation(value = "Get list of TransactionType in the System", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 401, message = "Not authorized!"),
    @ApiResponse(code = 403, message = "Forbidden!"),
    @ApiResponse(code = 404, message = "Not found!")})

    Page<TransactionTypeDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber, int pageSize);

    @ApiOperation(value = "Get TransactionType by ID", response = TransactionTypeDTO.class)
    TransactionTypeDTO getById(Long id);

    @ApiOperation(value = "Create TransactionType", response = TransactionTypeDTO.class)
    TransactionTypeDTO create(TransactionTypeDTO dto);

    @ApiOperation(value = "Update TransactionType by ID", response = TransactionTypeDTO.class)
    TransactionTypeDTO update(Long id, TransactionTypeDTO dto);

    @ApiOperation(value = "Delete TransactionType", response = TransactionTypeDTO.class)
    Boolean delete(Long id);

}
