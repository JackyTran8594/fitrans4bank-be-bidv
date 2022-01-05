package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.response.OptionSetDTO;
import com.eztech.fitrans.dto.response.OptionSetValueDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Api
public interface OptionSetApi {

    @ApiOperation(value = "Get list of optionset", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 401, message = "Not authorized!"),
            @ApiResponse(code = 403, message = "Forbidden!"),
            @ApiResponse(code = 404, message = "Not found!")})
    Page<OptionSetDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber,
                               int pageSize);

    @ApiOperation(value = "Get value by optionset", response = OptionSetDTO.class)
    OptionSetDTO getById(Long id);

    @ApiOperation(value = "Get value by optionset", response = OptionSetDTO.class)
    OptionSetDTO getByCode(String code);

    @ApiOperation(value = "Get value by optionset", response = Iterable.class)
    List<OptionSetValueDTO> listByCode(String code);

    @ApiOperation(value = "Create optionset", response = OptionSetDTO.class)
    OptionSetDTO create(OptionSetDTO dto);

    @ApiOperation(value = "Update optionset by ID", response = OptionSetDTO.class)
    OptionSetDTO update(Long id, OptionSetDTO dto);

    @ApiOperation(value = "Delete optionset", response = Boolean.class)
    Boolean delete(Long id);
}
