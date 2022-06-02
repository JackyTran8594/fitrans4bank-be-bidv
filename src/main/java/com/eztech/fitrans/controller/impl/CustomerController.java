package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.CustomerApi;
import com.eztech.fitrans.dto.response.CustomerDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.CustomerService;

import java.util.*;

import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.ExcelFileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/customers")
public class CustomerController extends BaseController implements CustomerApi {

  @Autowired
  private CustomerService service;

  @Override
  @GetMapping("/all")
  public List<CustomerDTO> listAll() {
    return service.findAll();
  }

  @Override
  @GetMapping("")
  public Page<CustomerDTO> getList(
      @RequestParam Map<String, Object> mapParam,
      @RequestParam int pageNumber,
      @RequestParam int pageSize) {
    if (pageNumber > 0) {
      pageNumber = pageNumber - 1;
    }
    mapParam.put("pageNumber", pageNumber);
    mapParam.put("pageSize", pageSize);
    Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
    List<CustomerDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public CustomerDTO getById(@PathVariable(value = "id") Long id) {
    CustomerDTO dto = service.findById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Customer " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  public CustomerDTO create(@RequestBody CustomerDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  // @PreAuthorize("hasRole('ROLE_ADMIN')")
  public CustomerDTO update(@PathVariable(value = "id") Long id, @RequestBody CustomerDTO item) {
    item.setId(id);
    return service.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    service.deleteById(id);
    return true;
  }

  @Override
  @DeleteMapping("")
  public Boolean delete(@RequestParam(value = "ids") List<Long> ids) {
    service.deleteById(ids);
    return true;
  }

  @RequestMapping(value = "test", method = RequestMethod.GET)
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

  @Override
  @RequestMapping(value = "download", method = RequestMethod.GET)
  public ResponseEntity<byte[]> downloadTemplate() throws Exception {
    Map<String, Object> mapParam = new HashMap<>();
    mapParam.put("pageNumber", 1);
    mapParam.put("pageSize", 1);
    List<CustomerDTO> listData = service.search(mapParam);
    if(DataUtils.notNullOrEmpty(listData)){
      int i = 1;
      for(CustomerDTO dto: listData){
        dto.setStt(i++);
      }
    }

    List<String> headerList = Arrays.asList("STT","CIF", "Tên khách hàng", "Địa chỉ", "Số điện thoại", "Loại khách hàng (1: Thông thường, 2: VIP)");
    List<String> propertyList = Arrays.asList("stt","cif", "name", "address", "tel", "type");

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header("Content-Disposition", "attachment; filename=template.xlsx")
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(ExcelFileWriter.writeToExcel(headerList, propertyList, listData));
  }

  @Override
  @RequestMapping(value = "import", method = RequestMethod.POST)
  public ResponseEntity<byte[]> importFile(@RequestParam("file") MultipartFile file) throws Exception {
    List<CustomerDTO> customerDTOList = service.importFile(file);

    List<String> headerList = Arrays.asList("cif", "Tên khách hàng", "Địa chỉ", "Số điện thoại", "Loại khách hàng", "Loại khách hàng", "Message");
    List<String> propertyList = Arrays.asList("cif", "name", "address", "tel", "type","typeName", "errorMsg");

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header("Content-Disposition", "attachment; filename=success.xlsx")
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(ExcelFileWriter.writeToExcel(headerList, propertyList, customerDTOList));
  }

  @GetMapping("/Cif/{cif}")
  public List<CustomerDTO> getByCif(@PathVariable(value = "cif") String cif) {
    List<CustomerDTO> dto = service.findByCif(cif);
    if (dto == null) {
      throw new ResourceNotFoundException("Customer " + cif + "not found");
    }
    return dto;
  }
}