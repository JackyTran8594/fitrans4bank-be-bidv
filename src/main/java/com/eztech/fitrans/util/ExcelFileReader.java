package com.eztech.fitrans.util;

import com.eztech.fitrans.constants.FieldType;
import com.eztech.fitrans.dto.ExcelFieldDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.eztech.fitrans.util.DataUtils.readFileResource;
import static com.eztech.fitrans.util.DataUtils.readInputStreamResource;

@Slf4j
public final  class ExcelFileReader {
    final static SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");

    private ExcelFileReader() {
    }

    public static Workbook readExcel(final MultipartFile file) throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (InvalidFormatException e) {
            log.error(e.getMessage(),e);
        }
        return wb;
    }

    public static Workbook readExcelFromResource(final String fullFilePath) throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(readInputStreamResource(fullFilePath));
        } catch (InvalidFormatException e) {
            log.error(e.getMessage(),e);
        }
        return wb;
    }

    public static Workbook readExcel(final String fullFilePath) throws IOException, org.apache.poi.openxml4j.exceptions.InvalidFormatException {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new File(fullFilePath));
        } catch (InvalidFormatException e) {
            log.error(e.getMessage(),e);
        }
        return wb;
    }

    public static Map<String, List<ExcelFieldDTO[]>> getExcelRowValues(final Sheet sheet, String jsonPath, int begin) {
        Map<String, List<ExcelFieldDTO[]>> excelMap = new HashMap<>();
        Map<String, ExcelFieldDTO[]> excelSectionHeaders = getExcelHeaderSections(jsonPath);
        int totalRows = sheet.getLastRowNum();
        excelSectionHeaders.forEach((section, excelFields) -> {
            List<ExcelFieldDTO[]> excelFieldList = new ArrayList<>();
            for (int i = begin; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                ExcelFieldDTO[] excelFieldArr = new ExcelFieldDTO[excelFields.length];
                int k = 0;
                for (ExcelFieldDTO ehc : excelFields) {
                    int cellIndex = ehc.getExcelIndex();
                    String cellType = ehc.getExcelColType();
                    Cell cell = row.getCell(cellIndex);
                    ExcelFieldDTO excelField = new ExcelFieldDTO();
                    excelField.setExcelColType(ehc.getExcelColType());
                    excelField.setExcelHeader(ehc.getExcelHeader());
                    excelField.setExcelIndex(ehc.getExcelIndex());
                    excelField.setPojoAttribute(ehc.getPojoAttribute());
                    if(cell == null){
                        excelField.setExcelValue("");
                        continue;
                    }
                    if (FieldType.STRING.getValue().equalsIgnoreCase(cellType)) {
                        try {
                            excelField.setExcelValue(cell.getStringCellValue());
                        }catch (Exception ex){
                            excelField.setExcelValue(String.valueOf(cell.getNumericCellValue()));
                        }
                    } else if (FieldType.DOUBLE.getValue().equalsIgnoreCase(cellType)
                            || FieldType.INTEGER.getValue().equalsIgnoreCase(cellType)) {
                        try {
                            excelField.setExcelValue(String.valueOf(cell.getNumericCellValue()));
                        }catch (Exception ex){
                            excelField.setExcelValue(cell.getStringCellValue());
                        }
                    } else if (DateUtil.isCellDateFormatted(cell)) {
                        excelField.setExcelValue(String.valueOf(dtf.format(cell.getDateCellValue())));
                    }
                    excelFieldArr[k++] = excelField;
                }
                excelFieldList.add(excelFieldArr);
            }
            excelMap.put(section, excelFieldList);
        });
        return excelMap;
    }

    private static Map<String, ExcelFieldDTO[]> getExcelHeaderSections(String jsonPath) {
        List<Map<String, List<ExcelFieldDTO>>> jsonConfigMap = getExcelHeaderFieldSections(jsonPath);
        Map<String, ExcelFieldDTO[]> jsonMap = new HashMap<>();
        jsonConfigMap.forEach(jps -> {
            jps.forEach((section, values) -> {
                ExcelFieldDTO[] excelFields = new ExcelFieldDTO[values.size()];
                jsonMap.put(section, values.toArray(excelFields));
            });
        });
        return jsonMap;
    }

    private static List<Map<String, List<ExcelFieldDTO>>> getExcelHeaderFieldSections(String jsonPath) {
        List<Map<String, List<ExcelFieldDTO>>> jsonMap = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonPath = jsonPath == null ? "excel.json" : jsonPath;

            byte[] resource = readFileResource(jsonPath);

            String jsonConfig = new String(resource);
//      String jsonConfig = new String(
//          Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(jsonPath).toURI())));

            jsonMap = objectMapper.readValue(jsonConfig, new TypeReference<List<Map<String, List<ExcelFieldDTO>>>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return jsonMap;
    }
}