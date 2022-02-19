package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.TestApi;
import com.eztech.fitrans.dto.response.CustomerDTO;
import com.eztech.fitrans.service.CustomerService;
import com.eztech.fitrans.util.ExcelFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController extends BaseController implements TestApi {

  @Autowired
  private CustomerService service;

  @RequestMapping(value = "importCustomer", method = RequestMethod.GET)
  public ResponseEntity<byte[]> test() throws Exception {
    List<CustomerDTO> customerDTOList = service.importFile(null);

    List<String> headerList = Arrays.asList("cif", "Tên khách hàng", "Địa chỉ", "Số điện thoại", "Loại khách hàng", "Loại khách hàng", "Message");
    List<String> propertyList = Arrays.asList("cif", "name", "address", "tel", "type","typeName", "errorMsg");

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header("Content-Disposition", "attachment; filename=success.xlsx")
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(ExcelFileWriter.writeToExcel(headerList, propertyList, customerDTOList));
  }
}