package com.eztech.fitrans.util;

import com.eztech.fitrans.constants.FieldType;
import com.eztech.fitrans.dto.ExcelFieldDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ThuyetLV
 */
@Slf4j
public final class ExcelFieldMapper {

  final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  public static <T> List<T> getPojos(List<ExcelFieldDTO[]> excelFields, Class<T> clazz) {

    if (CollectionUtils.isEmpty(excelFields)) {
      return new ArrayList<>();
    }

    List<T> list = new ArrayList<>();
    excelFields.forEach(evc -> {

      T t = null;

      try {
        t = clazz.getConstructor().newInstance();
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException | NoSuchMethodException | SecurityException e1) {
        e1.printStackTrace();
      }

      Class<? extends Object> classz = t.getClass();

      for (int i = 0; i < evc.length; i++) {

        for (Field field : classz.getDeclaredFields()) {
          field.setAccessible(true);

          if (evc[i] != null && DataUtils.safeEqualIgnoreCase(evc[i].getPojoAttribute(),field.getName())) {

            try {
              if (FieldType.STRING.getValue().equalsIgnoreCase(evc[i].getExcelColType())) {
                field.set(t, evc[i].getExcelValue());
              } else if (FieldType.DOUBLE.getValue().equalsIgnoreCase(evc[i].getExcelColType())) {
                field.set(t, Double.valueOf(evc[i].getExcelValue()));
              } else if (FieldType.INTEGER.getValue().equalsIgnoreCase(evc[i].getExcelColType())) {
                field.set(t, Double.valueOf(evc[i].getExcelValue()).intValue());
              }else if (FieldType.LONG.getValue().equalsIgnoreCase(evc[i].getExcelColType())) {
                field.set(t, DataUtils.parseToLong(evc[i].getExcelValue()));
              } else if (FieldType.DATE.getValue().equalsIgnoreCase(evc[i].getExcelColType())) {
                field.set(t, LocalDate.parse(evc[i].getExcelValue(), dtf));
              }
            } catch (IllegalArgumentException | IllegalAccessException e) {
              log.error(e.getMessage(),e);
            }

            break;
          }
        }
      }

      list.add(t);
    });

    return list;
  }
}
