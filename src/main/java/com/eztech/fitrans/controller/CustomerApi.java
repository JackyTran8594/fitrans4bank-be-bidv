package com.eztech.fitrans.controller;

import com.eztech.fitrans.dto.response.CustomerDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Api
public interface CustomerApi {

  @ApiOperation(value = "Get list of customer in the System ", response = Iterable.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
          @ApiResponse(code = 401, message = "Not authorized!"),
          @ApiResponse(code = 403, message = "Forbidden!"),
          @ApiResponse(code = 404, message = "Not found!")})
  List<CustomerDTO> listAll();

  @ApiOperation(value = "Get list of customer in the System ", response = Iterable.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 401, message = "Not authorized!"),
      @ApiResponse(code = 403, message = "Forbidden!"),
      @ApiResponse(code = 404, message = "Not found!")})
  Page<CustomerDTO> getList(@RequestParam Map<String, Object> mapParam, int pageNumber,
      int pageSize);

  @ApiOperation(value = "Get customer by ID", response = CustomerDTO.class)
  CustomerDTO getById(Long id);

  @ApiOperation(value = "Create customer", response = CustomerDTO.class)
  CustomerDTO create(CustomerDTO dto);

  @ApiOperation(value = "Update customer by ID", response = CustomerDTO.class)
  CustomerDTO update(Long id, CustomerDTO dto);

  @ApiOperation(value = "Delete customer", response = Boolean.class)
  Boolean delete(Long id);

  @ApiOperation(value = "Delete list customer", response = Boolean.class)
  Boolean delete(List<Long> ids);

  @RequestMapping(value = "test", method = RequestMethod.GET)
  ResponseEntity<byte[]> test() throws Exception;

  @RequestMapping(value = "download", method = RequestMethod.GET)
  ResponseEntity<byte[]> downloadTemplate() throws Exception;

  @RequestMapping(value = "import", method = RequestMethod.POST)
  ResponseEntity<byte[]> importFile(@RequestParam("file") MultipartFile file) throws Exception;

}
