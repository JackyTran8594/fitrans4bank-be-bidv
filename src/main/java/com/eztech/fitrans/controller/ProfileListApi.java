package com.eztech.fitrans.controller;

import java.util.Map;

import com.eztech.fitrans.dto.response.ProfileListDTO;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;


@Api
public interface ProfileListApi {
    
    @ApiOperation(value = "Get list of ProfileList in the System", response = Iterable.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 401, message = "Not authorized!"),
    @ApiResponse(code = 403, message = "Forbidden!"),
    @ApiResponse(code = 404, message = "Not found!")})

    Page<ProfileListDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber, int pageSize);

    @ApiOperation(value = "Get ProfileList by ID", response = ProfileListDTO.class)
    ProfileListDTO getById(Long id);

    @ApiOperation(value = "Create ProfileList", response = ProfileListDTO.class)
    ProfileListDTO create(ProfileListDTO dto);

    @ApiOperation(value = "Update ProfileList by ID", response = ProfileListDTO.class)
    ProfileListDTO update(Long id, ProfileListDTO dto);

    @ApiOperation(value = "Delete ProfileList", response = ProfileListDTO.class)
    Boolean delete(Long id);

}
