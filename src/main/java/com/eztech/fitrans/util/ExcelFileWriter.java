package com.eztech.fitrans.util;

import com.eztech.fitrans.dto.response.BaseImportDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author ThuyetLV
 */
@Slf4j
public class ExcelFileWriter {

    private static final String EXCEL_EXPORT_HEADER_FONT = "Calibri";
    private static final String EXCEL_EXPORT_CELL_FONT = "Calibri";

    public static <T> XSSFWorkbook writeToExcel(XSSFWorkbook workbook, String sheetName, List<String> listHeder,
            List<String> listField, List<T> data) {
        try {
            Sheet sheet = workbook.createSheet(sheetName);
            // List<String> fieldNames = getFieldNamesForClass(data.get(0).getClass());
            int rowCount = 0;
            int columnCount = 0;
            Row row;
            if (DataUtils.notNullOrEmpty(listHeder)) {
                row = sheet.createRow(rowCount++);
                for (String fieldName : listHeder) {
                    Cell cell = row.createCell(columnCount++);
                    cell.setCellValue(fieldName);
                }
            }
            if (!DataUtils.isNullOrEmpty(data)) {
                Class<? extends Object> classz = data.get(0).getClass();
                for (T t : data) {
                    row = sheet.createRow(rowCount++);
                    columnCount = 0;
                    for (String fieldName : listField) {
                        Cell cell = row.createCell(columnCount);
                        Method method = null;
                        try {
                            method = classz.getMethod("get" + capitalize(fieldName));
                        } catch (NoSuchMethodException nme) {
                            method = classz.getMethod("get" + fieldName);
                        }
                        Object value = method.invoke(t, (Object[]) null);
                        if (value != null) {
                            if (value instanceof String) {
                                cell.setCellValue((String) value);
                            } else if (value instanceof Long) {
                                cell.setCellValue((Long) value);
                            } else if (value instanceof Integer) {
                                cell.setCellValue((Integer) value);
                            } else if (value instanceof Double) {
                                cell.setCellValue((Double) value);
                            }
                        }
                        columnCount++;
                    }
                }
            }
            return workbook;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> byte[] writeToExcel(List<String> header, List<String> listField, List<T> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            List<String> fieldNames = getFieldNamesForClass(data.get(0).getClass());
            int rowCount = 0;
            int columnCount = 0;
            Row row = null;
            if (DataUtils.notNullOrEmpty(header)) {
                row = sheet.createRow(rowCount++);
                CellStyle headerCellStyle = styleCellHeader(workbook);
                for (String fieldName : header) {
                    Cell cell = row.createCell(columnCount++);
                    cell.setCellStyle(headerCellStyle);
                    cell.setCellValue(fieldName);
                }
            }
            if (DataUtils.notNullOrEmpty(data)) {
                CellStyle styleError = styleCellError(workbook);

                Class<? extends Object> classz = data.get(0).getClass();
                for (T t : data) {
                    row = sheet.createRow(rowCount++);
                    columnCount = 0;
                    for (String fieldName : listField) {
                        Cell cell = row.createCell(columnCount);

                        Method method = null;
                        try {
                            method = classz.getMethod("get" + capitalize(fieldName.trim()));
                        } catch (NoSuchMethodException nme) {
                            method = classz.getMethod("get" + fieldName);
                        }
                        Object value = method.invoke(t, (Object[]) null);

                        // styleCellError
                        if ("errorMsg".equalsIgnoreCase(fieldName) && value != null
                                && !"".equalsIgnoreCase(value.toString())) {
                            cell.setCellStyle(styleError);
                        }

                        if (value != null) {
                            if (value instanceof String) {
                                cell.setCellValue((String) value);
                            } else if (value instanceof Long) {
                                cell.setCellValue((Long) value);
                            } else if (value instanceof Integer) {
                                cell.setCellValue((Integer) value);
                            } else if (value instanceof Double) {
                                cell.setCellValue((Double) value);
                            }
                        }
                        columnCount++;
                    }
                }
            }
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static <T> byte[] writeToExcelFile(List<String> header, List<String> listField, List<T> data,
            Optional<String> title, Map<String, Object> mapParam) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            List<String> fieldNames = getFieldNamesForClass(data.get(0).getClass());
            int rowCount = 0;
            int columnCount = 0;
            Row row = null;
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            DateTimeFormatter dateFormatterSQL = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            // DateTimeFormatterBuilder builderDFT = new
            // DateTimeFormatterBuilder().parseCaseInsensitive().parseLenient()
            // .parseDefaulting(ChronoField.YEAR_OF_ERA,
            // 2016L).appendPattern("[yyyy-MM-dd]");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
            // title
            if (title.isPresent()) {
                row = sheet.createRow(rowCount++);
                int firstRow = 0;
                int lastRow = 0;
                int firstCol = 0;
                int lastCol = header.size() - 1;
                CellStyle titleCellStyle = styleCellTitle(workbook);
                Cell cell = row.createCell(firstRow);
                cell.setCellStyle(titleCellStyle);
                cell.setCellValue(title.get());
                sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
            }
            if (mapParam.containsKey("fromDate")) {
                if (!DataUtils.isNullOrEmpty(mapParam.get("fromDate"))) {
                    LocalDate fromDate = LocalDate.parse(mapParam.get("fromDate").toString(), dateFormatterSQL);
                    row = sheet.createRow(rowCount++);
                    CellStyle titleCellStyle = styleTitle(workbook);
                    Cell cell = row.createCell(2);
                    cell.setCellStyle(titleCellStyle);
                    cell.setCellValue("Từ ngày: " + fromDate.format(dateFormatter));
                }

            }

            if (mapParam.containsKey("toDate")) {
                if (!DataUtils.isNullOrEmpty(mapParam.get("toDate"))) {
                    LocalDate toDate = LocalDate.parse(mapParam.get("toDate").toString(), dateFormatterSQL);
                    row = sheet.createRow(rowCount++);
                    CellStyle titleCellStyle = styleTitle(workbook);
                    Cell cell = row.createCell(2);
                    cell.setCellStyle(titleCellStyle);
                    cell.setCellValue("Đến ngày: " + toDate.format(dateFormatter));
                }

            }

            if (DataUtils.notNullOrEmpty(header)) {
                row = sheet.createRow(rowCount++);
                CellStyle headerCellStyle = styleCellHeader(workbook);
                for (String fieldName : header) {
                    Cell cell = row.createCell(columnCount++);
                    cell.setCellStyle(headerCellStyle);
                    cell.setCellValue(fieldName);
                }
            }
            if (DataUtils.notNullOrEmpty(data)) {
                CellStyle styleError = styleCellError(workbook);
                CellStyle cellStyleBody = styleCellBody(workbook);

                Class<? extends Object> classz = data.get(0).getClass();
                for (T t : data) {
                    row = sheet.createRow(rowCount++);
                    columnCount = 0;
                    for (String fieldName : listField) {
                        Cell cell = row.createCell(columnCount);

                        Method method = null;
                        try {
                            method = classz.getMethod("get" + capitalize(fieldName.trim()));
                        } catch (NoSuchMethodException nme) {
                            method = classz.getMethod("get" + fieldName);
                        }
                        Object value = method.invoke(t, (Object[]) null);

                        // styleCellError
                        if ("errorMsg".equalsIgnoreCase(fieldName) && value != null
                                && !"".equalsIgnoreCase(value.toString())) {
                            cell.setCellStyle(styleError);
                        }

                        if (value != null) {
                            if (value instanceof String) {
                                cell.setCellValue((String) value);
                            } else if (value instanceof LocalDateTime) {
                                // Create DateTimeFormatter instance with specified format
                                LocalDateTime currentDateTime = (LocalDateTime) value;
                                cell.setCellValue(currentDateTime.format(dateTimeFormatter));

                                // cell.setCellValue(sdf.format(value));
                            } else if (value instanceof Long) {
                                cell.setCellValue((Long) value);
                            } else if (value instanceof BigDecimal) {
                                cell.setCellValue(((BigDecimal) value).doubleValue());
                                // format currency
                                applyNumericFormat(workbook, row, cell, "#,###.##");
                            } else if (value instanceof Integer) {
                                cell.setCellValue((Integer) value);
                            } else if (value instanceof Double) {
                                cell.setCellValue((Double) value);
                            }
                        }
                        cell.setCellStyle(cellStyleBody);
                        columnCount++;
                    }
                }
            }
            // autosize column
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static <T> byte[] writeToExcel(List<T> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            List<String> fieldNames = getFieldNamesForClass(data.get(0).getClass());
            int rowCount = 0;
            int columnCount = 0;
            Row row = sheet.createRow(rowCount++);
            for (String fieldName : fieldNames) {
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(fieldName);
            }
            Class<? extends Object> classz = data.get(0).getClass();
            for (T t : data) {
                row = sheet.createRow(rowCount++);
                columnCount = 0;
                for (String fieldName : fieldNames) {
                    Cell cell = row.createCell(columnCount);
                    Method method = null;
                    try {
                        method = classz.getMethod("get" + capitalize(fieldName));
                    } catch (NoSuchMethodException nme) {
                        method = classz.getMethod("get" + fieldName);
                    }
                    Object value = method.invoke(t, (Object[]) null);
                    if (value != null) {
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Long) {
                            cell.setCellValue((Long) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        }
                    }
                    columnCount++;
                }
            }
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static <T> void writeToExcel(String fileName, List<T> data) {
        OutputStream fos = null;
        XSSFWorkbook workbook = null;
        try {
            File file = new File(fileName);
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            List<String> fieldNames = getFieldNamesForClass(data.get(0).getClass());
            int rowCount = 0;
            int columnCount = 0;
            Row row = sheet.createRow(rowCount++);
            for (String fieldName : fieldNames) {
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(fieldName);
            }
            Class<? extends Object> classz = data.get(0).getClass();
            for (T t : data) {
                row = sheet.createRow(rowCount++);
                columnCount = 0;
                for (String fieldName : fieldNames) {
                    Cell cell = row.createCell(columnCount);
                    Method method = null;
                    try {
                        method = classz.getMethod("get" + capitalize(fieldName));
                    } catch (NoSuchMethodException nme) {
                        method = classz.getMethod("get" + fieldName);
                    }
                    Object value = method.invoke(t, (Object[]) null);
                    if (value != null) {
                        if (value instanceof String) {
                            cell.setCellValue((String) value);
                        } else if (value instanceof Long) {
                            cell.setCellValue((Long) value);
                        } else if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                        }
                    }
                    columnCount++;
                }
            }
            fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
            }
        }
    }

    // retrieve field names from a POJO class
    private static List<String> getFieldNamesForClass(Class<?> clazz) throws Exception {
        List<String> fieldNames = new ArrayList<String>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fieldNames.add(fields[i].getName());
        }
        return fieldNames;
    }

    // capitalize the first letter of the field name for retriving value of the
    // field later
    private static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void applyNumericFormat(Workbook outWorkbook, Row row, Cell cell, String styleFormat) {
        CellStyle style = outWorkbook.createCellStyle();
        DataFormat format = outWorkbook.createDataFormat();
        style.setDataFormat(format.getFormat(styleFormat));
        cell.setCellStyle(style);
    }

    public static CellStyle styleCellHeader(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setFontName(EXCEL_EXPORT_HEADER_FONT);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setFillForegroundColor(IndexedColors.TAN.index);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setTopBorderColor(IndexedColors.BLACK.index);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setRightBorderColor(IndexedColors.BLACK.index);
        return headerCellStyle;
    }

    public static CellStyle styleCellTitle(Workbook workbook) {
        Font titleFont = workbook.createFont();
        titleFont.setFontName(EXCEL_EXPORT_HEADER_FONT);
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBold(true);

        CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleFont);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleCellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // titleCellStyle.setBorderTop(BorderStyle.THIN);
        // titleCellStyle.setTopBorderColor(IndexedColors.BLACK.index);
        // titleCellStyle.setBorderBottom(BorderStyle.THIN);
        // titleCellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        // titleCellStyle.setBorderLeft(BorderStyle.THIN);
        // titleCellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        // titleCellStyle.setBorderRight(BorderStyle.THIN);
        // titleCellStyle.setRightBorderColor(IndexedColors.BLACK.index);
        return titleCellStyle;
    }

    public static CellStyle styleCellBody(Workbook workbook) {
        // Tao font cho cell
        Font cellFont = workbook.createFont();
        cellFont.setFontName(EXCEL_EXPORT_CELL_FONT);
        cellFont.setFontHeightInPoints((short) 12);

        // Tao style cho cell
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        // cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.index);
        return cellStyle;
    }

    public static CellStyle styleTitle(Workbook workbook) {
        // Tao font cho cell
        Font cellFont = workbook.createFont();
        cellFont.setFontName(EXCEL_EXPORT_CELL_FONT);
        cellFont.setBold(true);
        cellFont.setFontHeightInPoints((short) 18);

        // Tao style cho cell
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    public static CellStyle styleCellError(Workbook workbook) {
        // Tao font cho cell
        Font cellFont = workbook.createFont();
        cellFont.setFontName(EXCEL_EXPORT_CELL_FONT);
        cellFont.setFontHeightInPoints((short) 12);
        cellFont.setColor(IndexedColors.RED.index);

        // Tao style cho cell
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    public static byte[] writeToExcelWithError(List<String> header, List<String> listField, List<BaseImportDTO> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            List<String> fieldNames = getFieldNamesForClass(data.get(0).getClass());
            int rowCount = 0;
            int columnCount = 0;
            Row row = null;
            if (DataUtils.notNullOrEmpty(header)) {
                row = sheet.createRow(rowCount++);
                CellStyle headerCellStyle = styleCellHeader(workbook);
                for (String fieldName : header) {
                    Cell cell = row.createCell(columnCount++);
                    cell.setCellStyle(headerCellStyle);
                    cell.setCellValue(fieldName);
                }
            }
            if (DataUtils.notNullOrEmpty(data)) {
                Class<? extends Object> classz = data.get(0).getClass();
                CellStyle styleError = styleCellError(workbook);
                for (BaseImportDTO t : data) {
                    row = sheet.createRow(rowCount++);
                    columnCount = 0;
                    for (String fieldName : listField) {
                        Cell cell = row.createCell(columnCount);
                        if (t.getError()) {
                            cell.setCellStyle(styleError);
                        }
                        Method method = null;
                        try {
                            method = classz.getMethod("get" + capitalize(fieldName));
                        } catch (NoSuchMethodException nme) {
                            method = classz.getMethod("get" + fieldName);
                        }
                        Object value = method.invoke(t, (Object[]) null);
                        if (value != null) {
                            if (value instanceof String) {
                                cell.setCellValue((String) value);
                            } else if (value instanceof Long) {
                                cell.setCellValue((Long) value);
                            } else if (value instanceof Integer) {
                                cell.setCellValue((Integer) value);
                            } else if (value instanceof Double) {
                                cell.setCellValue((Double) value);
                            }
                        }
                        columnCount++;
                    }
                }
            }
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
            }
        }
    }

}
