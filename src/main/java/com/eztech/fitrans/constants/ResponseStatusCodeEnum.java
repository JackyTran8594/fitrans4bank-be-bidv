package com.eztech.fitrans.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@SuppressWarnings("java:S1192")
@Getter
@AllArgsConstructor
public enum ResponseStatusCodeEnum implements Serializable {

  /**
   * response codes should follow the standard: XXXYYYY where - XXX: application shortname - YYYY: numeric code of the error code - 0YYY:
   * VALIDATION_ERROR - 1YYY: BUSINESS_ERROR
   */

  SUCCESS("00", 200),
  ERROR("ERR2000", 200),
  BUSINESS_ERROR("BUS2000", 200),
  INTERNAL_GENERAL_SERVER_ERROR("INTERNAL", 500),
  UNKNOWN("UNKNOWN", 500);

  private String code;
  private String messageKey;
  private int httpCode;

  ResponseStatusCodeEnum(String code, int httpCode) {
    this.code = code;
    this.httpCode = httpCode;
  }

  @Override
  public String toString() {
    return "ResponseStatus{" +
        "code='" + code + '\'' +
        "httpCode='" + httpCode + '\'' +
        '}';
  }

  @SuppressWarnings("java:S3066")
  public void setMessageKey(String messsage) {
    this.messageKey = messsage;
  }

  public static ResponseStatusCodeEnum getByCode(String code, ResponseStatusCodeEnum defaultValue) {
    for (ResponseStatusCodeEnum e : values()) {
      if (e.code.equals(code)) {
        return e;
      }
    }
    return defaultValue;
  }

  public static ResponseStatusCodeEnum getByCode(String code, String messageKey, ResponseStatusCodeEnum defaultValue) {
    for (ResponseStatusCodeEnum e : values()) {
      if (e.code.equals(code)) {
        e.setMessageKey(messageKey);
        return e;
      }
    }
    return defaultValue;
  }
}
