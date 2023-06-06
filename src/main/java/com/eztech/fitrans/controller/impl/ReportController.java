package com.eztech.fitrans.controller.impl;

import com.eztech.fitrans.constants.Constants;
import com.eztech.fitrans.constants.ProfileStateEnum;
import com.eztech.fitrans.controller.ProfileApi;
import com.eztech.fitrans.controller.ReportApi;
import com.eztech.fitrans.dto.request.ConfirmRequest;
import com.eztech.fitrans.dto.response.DepartmentDTO;
import com.eztech.fitrans.dto.response.MessageDTO;
import com.eztech.fitrans.dto.response.ProfileDTO;
import com.eztech.fitrans.dto.response.ProfileHistoryDTO;
import com.eztech.fitrans.dto.response.ProfileListDTO;
import com.eztech.fitrans.dto.response.TransactionTypeDTO;
import com.eztech.fitrans.dto.response.UserDTO;
import com.eztech.fitrans.dto.response.dashboard.DashboardDTO;
import com.eztech.fitrans.dto.response.report.ReportProfileDTO;
import com.eztech.fitrans.exception.ResourceNotFoundException;
import com.eztech.fitrans.model.ProfileHistory;
import com.eztech.fitrans.service.DepartmentService;
import com.eztech.fitrans.service.ProfileHistoryService;
import com.eztech.fitrans.service.ProfileListService;
import com.eztech.fitrans.service.ProfileService;
import com.eztech.fitrans.service.ReportService;
import com.eztech.fitrans.service.TransactionTypeService;
import com.eztech.fitrans.service.UserService;
import com.eztech.fitrans.util.DataUtils;
import com.eztech.fitrans.util.ExcelFileWriter;
import com.eztech.fitrans.util.ReadAndWriteDoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/report")
public class ReportController extends BaseController implements ReportApi {
    private static Logger logger = LoggerFactory.getLogger(ReportController.class);
    @Autowired
    private ProfileService service;

    @Autowired
    private ReportService reportService;


    @Override
    @GetMapping("")
    public Page<ReportProfileDTO> getList(
            @RequestParam Map<String, Object> mapParam,
            @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
        }
        mapParam.put("pageNumber", pageNumber);
        mapParam.put("pageSize", pageSize);
        Pageable pageable = pageRequest(new ArrayList<>(), pageSize, pageNumber);
        List<ReportProfileDTO> listData = reportService.search(mapParam);
        Long total = service.count(mapParam);
        return new PageImpl<>(listData, pageable, total);
    }

  


    @GetMapping("/exportExcel")
    public ResponseEntity<InputStreamResource> exportDoc(@RequestParam Map<String, Object> mapParam) throws FileNotFoundException, IOException {

        List<ReportProfileDTO> data = reportService.exportExcel(mapParam);

        String[] headers = new String[] { "STT", "CIF", "Tên khách hàng", "Giá trị",
                "Tình trạng hồ sơ", "Cán bộ QLKH tạo", "TG QLKH tạo hồ sơ",
                "TG QTTD nhận thực tế", "TG bắt đầu tính giờ cho QTTD",
                "TG QTTD xử lý muộn nhất", "TG QTTD hoàn thành thực tế",
                "Cán bộ QTTD xử lý", "TG GDKH nhận thực tế ", "TG bắt đầu tính giờ cho GDKH", "TG GDKH xử lý muộn nhất",
                "TG GDKH hoàn thành thực tế", "Cán bộ GDKH xử lý " };

        String[] listField = new String[] { "no", "cif", "customerName", "value",
        "stateEnum", "staffName", "createdDate",
        "realTimeReceivedCM", "timeReceived_CM",
        "processDate", "endTimeCM",
        "staffNameCM", "realTimeReceivedCT", "timeReceived_CT", "processDateCT",
        "endTimeCT", "staffNameCT" };
        
        byte[] bytes = ExcelFileWriter.writeToExcelFile(Arrays.asList(headers), Arrays.asList(listField), data, Optional.of("Báo cáo danh sách hồ sơ"), mapParam);
        
        HttpHeaders respHeaders = new HttpHeaders();
        // respHeaders.setContentType(new MediaType("text", "json"));
        respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Báo cáo_" + Timestamp.valueOf(LocalDateTime.now()).getTime() + ".xlsx");
        InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
        return ResponseEntity.ok()
                .headers(respHeaders)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .contentLength(bytes.length)
                .body(inputStreamResource);

    }

 

}