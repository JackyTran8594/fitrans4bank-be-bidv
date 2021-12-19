package com.eztech.fitrans.util;

import com.eztech.fitrans.dto.response.ErrorCodeEnum;
import com.eztech.fitrans.dto.response.ErrorMessageDTO;
import com.eztech.fitrans.locale.Translator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseFactory {

  public static ErrorMessageDTO error(ErrorCodeEnum code, Object... args) {
    return getResponseMessage(code, args);
  }

  //Common function
  private static ErrorMessageDTO getResponseMessage(ErrorCodeEnum code, Object... args) {
    return new ErrorMessageDTO(code, getMessI18n(code, args));
  }

  private static String getMessI18n(ErrorCodeEnum code, Object... args) {
    return Translator.toMessage(code, args);
  }
}
