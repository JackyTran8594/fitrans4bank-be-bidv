package com.eztech.fitrans.dto;

import lombok.Data;

@Data
public class ExcelFieldDTO {
  private String excelHeader;
  private int excelIndex;
  private String excelColType;
  private String excelValue;
  private String pojoAttribute;
}
