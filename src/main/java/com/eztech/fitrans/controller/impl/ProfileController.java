package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.controller.ProfileApi;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.service.ProfileHistoryService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
public class ProfileController extends BaseController implements ProfileApi {

  @Autowired
  private ProfileService service;

  @Autowired
  private ProfileHistoryService historyService;

  private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

  @Autowired
  private ReadAndWriteDoc readandwrite;

  @Override
  @GetMapping("")
  public Page<ProfileDTO> getList(
      @RequestParam Map<String, Object> mapParam,
      @RequestParam int pageNumber,
      @RequestParam int pageSize) {
    if (pageNumber > 0) {
      pageNumber = pageNumber - 1;
    }
    mapParam.put("pageNumber", pageNumber);
    mapParam.put("pageSize", pageSize);
    Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
    List<ProfileDTO> listData = service.search(mapParam);
    Long total = service.count(mapParam);
    return new PageImpl<>(listData, pageable, total);
  }

  @Override
  @GetMapping("/{id}")
  public ProfileDTO getById(@PathVariable(value = "id") Long id) {
    ProfileDTO dto = service.detailById(id);
    if (dto == null) {
      throw new ResourceNotFoundException("Profile " + id + " not found");
    }
    return dto;
  }

  @Override
  @PostMapping("")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ProfileDTO create(@RequestBody ProfileDTO item) {
    return service.save(item);
  }

  @Override
  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ProfileDTO update(@PathVariable(value = "id") Long id, @RequestBody ProfileDTO item) {
    item.setId(id);
    return service.save(item);
  }

  @Override
  @DeleteMapping("/{id}")
  public Boolean delete(@PathVariable(value = "id") Long id) {
    service.deleteById(id);
    return true;
  }

  @PostMapping("/exportDoc")
  public ResponseEntity<InputStreamResource> exportDoc(@RequestBody ProfileDTO item) throws FileNotFoundException {

    MediaType mediaType = MediaType.APPLICATION_JSON;
    readandwrite.ExportDocFile(item);
    File file = new File("D:\\destination.docx");
    InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
    // InputStreamResource inputStr = new Input
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
        .contentType(mediaType)
        .contentLength(file.length()) 
        .body(inputStreamResource);

  }

  @GetMapping("/historyProfile")
  public List<ProfileHistoryDTO> getHistory() {
    List<ProfileHistoryDTO> listData = historyService.findAll();
    return listData;
  }

  @PostMapping("/reivewProfile/{id}")
  public Boolean reviewProfile(@PathVariable(value = "id") Long id,@RequestBody ProfileDTO item) {
    item.setId(id);
    service.save(item);
    return true;
  }

  @PostMapping("/confirmProfile/{id}")
  public Boolean confirmProfile(@PathVariable(value = "id") Long id, @RequestBody ProfileDTO item) {
    item.setId(id);
    service.save(item);
    return true;
  }

}